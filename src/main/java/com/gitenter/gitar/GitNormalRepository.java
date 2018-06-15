package com.gitenter.gitar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

public class GitNormalRepository extends GitRepository {
	
	private Map<String,GitRemote> remotes = new HashMap<String,GitRemote>();

	/*
	 * TODO:
	 * Further wrap JGit exceptions.
	 */
	private GitNormalRepository(File directory) throws IOException, GitAPIException {
		super(directory);
		
		if (isBareRepository()) {
			/*
			 * This part is not redundant, because the corresponding
			 * bare repository may or may not in our lookup hashtable.
			 */
			throw new IOException("The provided directory is a bare git directory: "+directory);
		}
		else if (!isNormalRepository()) {
			Git.init().setDirectory(directory).setBare(false).call();
		}
	}
	
	public static GitNormalRepository getInstance(File directory) throws IOException, GitAPIException {
		
		if (instances.containsKey(directory)) {
			GitRepository repository = instances.get(directory);
			if (repository instanceof GitNormalRepository) {
				return (GitNormalRepository)repository;
			}
			else {
				/*
				 * TODO:
				 * Consider throw a customized exception.
				 */
				throw new IOException("The provided directory is a bare git directory: "+directory);
			}
		}
		else {
			GitNormalRepository repository = new GitNormalRepository(directory);
			instances.put(directory, repository);
			return repository;
		}
	}
	
	@Override
	Git getJGitGit() throws IOException {
		return Git.open(directory);
	}
	
	@Override
	Repository getJGitRepository() throws IOException {
		return getJGitGit().getRepository();
	}
	
	public void createOrUpdateRemote(String name, String url) {	
		remotes.put(name, new GitRemote(this, name, url));
	}
	
	public GitRemote getRemote(String name) {
		return remotes.get(name);
	}
	
	@Override
	public GitNormalBranch getBranch(String branchName) throws IOException, CheckoutConflictException, GitAPIException {
		GitBranch branch = super.getBranch(branchName);
		if (branch != null) {
			return new GitNormalBranch(branch);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Collection<GitBranch> getBranches() throws IOException, GitAPIException {
		
		/*
		 * Because `Collection<GitNormalBranch>` is not a superclass of
		 * `Collection<GitBranch>`, we still need to return the later one.
		 */
		Collection<GitBranch> normalBranches = new ArrayList<GitBranch>();
		for (GitBranch branch : super.getBranches()) {
			normalBranches.add(new GitNormalBranch(branch));
		}
		
		return normalBranches;
	}
	
	/*
	 * Get current branch returns "master" even for an empty repository which 
	 * `getBranches()` returns zero branch.
	 */
	public GitNormalBranch getCurrentBranch() throws IOException {
		return new GitNormalBranch(this, getJGitRepository().getBranch());
	}
	
	@Override
	protected File getHooksDirectory() {
		return new File(new File(directory, ".git"), "hooks");
	}
}
