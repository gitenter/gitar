package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitTag {

	protected final String name;
	protected final ObjectId objectId;
	
	protected final GitRepository repository;
	
	private CommitPlaceholder commitPlaceholder;
	
	public String getName() {
		return name;
	}
	
	public GitCommit getCommit() throws IOException, GitAPIException {
		return commitPlaceholder.getCommit();
	}

	GitTag(GitRepository repository, String name, ObjectId objectId) throws IOException {
		
		this.name = name;
		this.objectId = objectId;
		this.repository = repository;	
		
		this.commitPlaceholder = new ProxyCommitPlaceholder();
	}
	
	GitTag downCasting() throws IOException, GitAPIException {
		
		try (RevWalk revWalk = new RevWalk(repository.getJGitRepository())) {
			RevTag jGitTag = revWalk.parseTag(objectId);
			return new GitAnnotatedTag(this, jGitTag);
		}
		catch(IncorrectObjectTypeException notAnAnnotatedTag) {
			return new GitLightweightTag(this);
		}
	}
	
	private interface CommitPlaceholder {
		public GitCommit getCommit() throws IOException, GitAPIException;
	}
	
	private class ProxyCommitPlaceholder implements CommitPlaceholder {
		
		private RealCommitPlaceholder placeholder = null;
		
		@Override
		public GitCommit getCommit() throws IOException, GitAPIException {
			
			if (placeholder == null) {
				placeholder = new RealCommitPlaceholder();
			}
			
			return placeholder.getCommit();
		}
	}
	
	private class RealCommitPlaceholder implements CommitPlaceholder {
		
		private GitCommit gitCommit;
		
		RealCommitPlaceholder() throws IOException, GitAPIException {
			load();
		}
		
		private void load() throws IOException, GitAPIException {
			gitCommit = repository.getCommit(repository.getJGitRepository().exactRef("refs/tags/"+name).getObjectId().getName());
		}
		
		@Override
		public GitCommit getCommit() {
			return gitCommit;
		}
	}
}
