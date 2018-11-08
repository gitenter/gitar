package com.gitenter.gitar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import lombok.Getter;

public class GitBranch {
	
	@Getter
	protected final String name;
	
	protected final GitRepository repository;
	
	private final Ref jGitBranch;

	GitBranch(GitRepository repository, String name) throws IOException {
		this.repository = repository;
		this.name = name;
		
		jGitBranch = repository.getJGitRepository().exactRef("refs/heads/"+name);
	}
	
	/*
	 * TODO:
	 * May have a "refresh()", but it is kind of replace itself, so
	 * not sure whether it can be done or not.
	 */
	
	public GitCommit getHead() throws IOException {
		return new GitCommit(repository, jGitBranch.getObjectId().getName());
	}
	
	/*
	 * Newest commit first.
	 */
	public List<GitCommit> getLog() throws IOException, NoHeadException, GitAPIException {
		
		try (Git git = repository.getJGitGit()) {
			Iterable<RevCommit> jGitCommits = git.log()
					.add(repository.getJGitRepository().resolve(name))
					.call();
			
			return buildCommits(jGitCommits);
		}
	}
	
	public List<GitCommit> getLog(Integer maxCount, Integer skip) throws IOException, NoHeadException, GitAPIException {
		
		try (Git git = repository.getJGitGit()) {
			Iterable<RevCommit> jGitCommits = git.log()
					.add(repository.getJGitRepository().resolve(name))
					.setMaxCount(maxCount)
					.setSkip(skip)
					.call();
			
			return buildCommits(jGitCommits);
		}
	}

	/*
	 * "oldSha" is exclusive. "newSha" is inclusive.
	 * 
	 * The reason we need to input String, rather than GitCommit,
	 * is because the empty one (GitCommit.EMPTY_SHA) doesn't correspond
	 * to any concrete GitCommit.
	 */
	public List<GitCommit> getLog(String oldSha, String newSha) throws IOException, NoHeadException, GitAPIException {
		
		try (Git git = repository.getJGitGit()) {
			
			if (oldSha.equals(GitCommit.EMPTY_SHA)) {
				/*
				 * Unfortunately, JGit doesn't have a method for that, so I need to
				 * iterate it by myself.
				 * 
				 * NOTE:
				 * 
				 * The problem is, for a new branch, although it starts from an existing
				 * commit, the "oldSha" "update"/"post-receive" hooks provided
				 * is still a null value, so this log may contain several commits which
				 * shouldn't belong to this branch. However, there is no easy way to handle
				 * that in here (as "git log" itself has no knowledge on the knowledge
				 * of other branches, and/or the topology of commits. Methods includes 
				 * (1) Use "git log --graph" or JGit's "RevWalk"
				 * (2) Handle this problem somewhere else.
				 * 
				 * I am currently using the second method (in 
				 * "enterovirus.gihook.postreceive.UpdateDatabaseFromGit")
				 */
				Iterable<RevCommit> jGitCommits = git.log()
						.add(repository.getJGitRepository().resolve(name))
						.call();
				
				return buildCommits(jGitCommits, newSha);
			}
			else {
				ObjectId oldObjectId = ObjectId.fromString(oldSha);
				ObjectId newObjectId = ObjectId.fromString(newSha);
				
				Iterable<RevCommit> jGitCommits = git.log()
						.add(repository.getJGitRepository().resolve(name))
						.addRange(oldObjectId, newObjectId)
						.call();
				
				/*
				 * JGit seems to have a bug in here that for "addRange()" "newObjectId"
				 * doesn't work. Everything (starting from the newest commit) will be
				 * returned. Therefore, we need to manually remove the ones newer than
				 * the "newSha".
				 */
				List<GitCommit> commits = buildCommits(jGitCommits, newSha);
				
				return commits;
			}
		}
	}
	
	private List<GitCommit> buildCommits(Iterable<RevCommit> jGitCommits) {
		
		List<GitCommit> commits = new ArrayList<GitCommit>();
		for (RevCommit jGitCommit : jGitCommits) {
			commits.add(new GitCommit(repository, jGitCommit));
		}
		
		return commits;
	}
	
	private List<GitCommit> buildCommits(Iterable<RevCommit> jGitCommits, String newSha) {
		
		List<GitCommit> commits = new ArrayList<GitCommit>();
		
		boolean find = false;
		for (RevCommit jGitCommit : jGitCommits) {			
			if (find == false && newSha.equals(jGitCommit.getName())) {
				find = true;
			}
			if (find == true) {
				/*
				 * So the "newCommitSha" is inclusive.
				 */
				commits.add(new GitCommit(repository, jGitCommit));
			}
		}

		return commits;
	}
	
	public List<GitFileDiff> diff(GitBranch anotherBranch) throws IOException, GitAPIException {
		
		AbstractTreeIterator oldTreeParser = prepareTreeParser(repository.getJGitRepository(), "refs/heads/"+anotherBranch.name);
		AbstractTreeIterator newTreeParser = prepareTreeParser(repository.getJGitRepository(), "refs/heads/"+name);
		
		List<GitFileDiff> fileDiffs = new ArrayList<GitFileDiff>();
		List<DiffEntry> diff = repository.getJGitGit().diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
		for (DiffEntry entry : diff) {
			fileDiffs.add(new GitFileDiff(entry).downCasting());
			System.out.println("Entry: " + entry);
			System.out.println("=========");
			try (DiffFormatter formatter = new DiffFormatter(System.out)) {
				formatter.setRepository(repository.getJGitRepository());
				formatter.format(entry);
				System.out.println("~~~~~~~~~");
			}
		}
		
		return fileDiffs;
	}
	
	private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
		// from the commit we can build the tree which allows us to construct the TreeParser
		Ref head = repository.exactRef(ref);
		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit commit = walk.parseCommit(head.getObjectId());
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}

			walk.dispose();

			return treeParser;
		}
	}
}
