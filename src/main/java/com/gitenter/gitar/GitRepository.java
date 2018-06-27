package com.gitenter.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import lombok.Getter;

public abstract class GitRepository {
	
	/*
	 * It is actually not quite crucial in our case to have all
	 * subclasses share the same static instance, because a folder
	 * cannot be at the same time a bare and normal repository
	 * no matter what (if not in the case that the user come
	 * to edit the file on the fly, but for that we'll fail anyway).
	 */
	protected static Map<File,GitRepository> instances = new Hashtable<File,GitRepository>();
	
	@Getter
	protected final File directory;
	
	protected GitRepository(File directory) {
		this.directory = directory;
	}

	/*
	 * TODO:
	 * This is mostly for bare repository only. Actions are through
	 * JGit "Git" class which implements "AutoCloseable" (so always
	 * need to stay in try blocks.
	 * 
	 * Consider at least to move this one to GitBareRepository, but
	 * still need a universal way to get JGit "git" and handle close()
	 * of it at the same time.
	 */
	abstract Git getJGitGit() throws IOException;
	
	abstract Repository getJGitRepository() throws IOException;
	
	protected boolean isNormalRepository() {
		/*
		 * TODO: 
		 * What about the case if it is a broken git repo with only
		 * part of the files exist?
		 */
		if (!new File(directory, ".git").isDirectory()) {
			return false;
		}
		return true;
	}
	
	protected boolean isBareRepository() {
		/* 
		 * There is a git command `git rev-parse --is-bare-repository`
		 * but JGit does not implement it. Consider directory call that command.
		 */
		if (!new File(directory, "branches").isDirectory()
				|| !new File(directory, "hooks").isDirectory()
				|| !new File(directory, "logs").isDirectory()
				|| !new File(directory, "objects").isDirectory()
				|| !new File(directory, "refs").isDirectory()
				|| !new File(directory, "config").isFile()
				|| !new File(directory, "HEAD").isFile()) {
			return false;
		}
		return true;
	}
	
	boolean isJustInitialized() throws IOException, GitAPIException {
		
		/*
		 * This is a hack, that a just initialized repository doesn't
		 * have any branch (even master does not exist).
		 * 
		 * TODO:
		 * Consider using other ways to decide if a repository is just
		 * initialized. For example, to check that no HEAD exists yet.
		 */
		if (getBranches().isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public GitCommit getCommit(String sha) throws IOException {
		return new GitCommit(this, sha);
	}

	public GitBranch getBranch(String branchName) throws IOException, GitAPIException {
		
		try (Git git = getJGitGit()) {
			List<Ref> call = git.branchList().call();
			for (Ref ref : call) {
				if (branchName.equals(ref.getName().split("/")[2])) {
					return new GitBranch(this, branchName);
				}
			}
		}
		
		/*
		 * TODO:
		 * 
		 * Consider raise an error rather than return null if the targeting branch
		 * is not existed.
		 */
		return null;
	}
	
	/*
	 * An empty normal/bare repository don't have any branch. After the first
	 * commit, the "master" branch appears. 
	 */
	public Collection<GitBranch> getBranches() throws IOException, GitAPIException {
		
		Collection<GitBranch> branches = new ArrayList<GitBranch>();
		try (Git git = getJGitGit()) {
			List<Ref> call = git.branchList().call();
			for (Ref ref : call) {
				/*
				 * Parse "refs/heads/master" and get the name "master".
				 * So for other branches.
				 */
				String branchName = ref.getName().split("/")[2];
				branches.add(new GitBranch(this, branchName));
			}
		}
		
		return branches;
	}
	
	
	/* 
	 * Create new branch is not working when when the repository is empty
	 * (without any commit), with the error message: 
	 * > fatal: Not a valid object name: 'master'.
	 * 
	 * Create new branch works for a normal/bare repository which has at least
	 * one commit. However, for bare repository one cannot checkout since there
	 * is no work tree.
	 */
	public void createBranch(String branchName) throws IOException, GitAPIException {
		try (Git git = getJGitGit()) {
			git.branchCreate().setName(branchName).call();
		}
	}
	
	public GitTag getTag(String tagName) throws IOException, GitAPIException {
		
		try (Git git = getJGitGit()) {
			List<Ref> call = git.tagList().call();
			for (Ref ref : call) {
				if (tagName.equals(ref.getName().split("/")[2])) {
					return new GitTag(this, tagName, ref.getObjectId()).downCasting();
				}
			}
		}
		
		return null;
	}
	
	public Collection<GitTag> getTags() throws IOException, GitAPIException {
		
		Collection<GitTag> tags = new ArrayList<GitTag>();
		try (Git git = getJGitGit()) {
			List<Ref> call = git.tagList().call();
			for (Ref ref : call) {
				String tagName = ref.getName().split("/")[2];
				tags.add(new GitTag(this, tagName, ref.getObjectId()).downCasting());
			}
		}
		
		return tags;
	}
	
	/* 
	 * Create new tag is not working when when the repository is empty
	 * (without any commit), with the error message: 
	 * > fatal: Failed to resolve 'HEAD' as a valid ref.
	 * 
	 * Create new tag works for a normal/bare repository which has at least
	 * one commit. 
	 */
	public void createTag(String tagName) throws NoHeadException, GitAPIException, IOException {
		try (Git git = getJGitGit()) {
			/*
			 * It is weird in here. In Git if you
			 * > git tag -a tag-name
			 * without "-m", Git will redirect you to a text editor.
			 * But if I do not have "setAnnotated(false)" in here, will
			 * JGit redirect me to any kind of editors!?
			 */
			git.tag().setName(tagName).setAnnotated(false).call();
		}
	}

	public void createTag(String tagName, String message) throws NoHeadException, GitAPIException, IOException {
		try (Git git = getJGitGit()) {
			git.tag().setName(tagName).setMessage(message).call();
		}
	}
	
	public void addAHook(File filepath, String hookName) throws IOException {
		File hookFilepath = new File(getHooksDirectory(), hookName);
		FileUtils.copyFile(filepath, hookFilepath);
		hookFilepath.setExecutable(true);
	}
	
	public void addHooks(File folderpath) throws IOException {
		FileUtils.copyDirectory(folderpath, getHooksDirectory());
		for(File hookFile : getHooksDirectory().listFiles()) {
			hookFile.setExecutable(true);
		}
	}
	
	protected abstract File getHooksDirectory();
}
