package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;

public class GitNormalBranch extends GitBranch {

	GitNormalBranch(GitRepository repository, String name) throws IOException {
		super(repository, name);
	}
	
	GitNormalBranch(GitBranch branch) throws IOException {
		super(branch.repository, branch.name);
	}

	public GitWorkspace checkoutTo() throws CheckoutConflictException, GitAPIException, IOException {
		
		if (repository.isJustInitialized()) {
			/*
			 * For empty repository, no branch exists. so there is no reason
			 * to do any real checkout. Just return the home directory.
			 */
			;
		}
		else {
			try (Git git = repository.getJGitGit()) {
				git.checkout().setName(name).call();
			}
		}
		
		assert repository instanceof GitNormalRepository;
		return GitWorkspace.getInstance(this, (GitNormalRepository)repository);
	}
}
