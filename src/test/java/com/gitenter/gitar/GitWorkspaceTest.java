package com.gitenter.gitar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitWorkspaceTest {

	@Test
	public void testWorkspaceChangesBranchAfterCheckout(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCommit(tmpFolder);
		GitNormalBranch master = repository.getCurrentBranch();
		GitWorkspace workspace = master.checkoutTo();
		assertEquals(workspace.getBranch().getName(), "master");
		
		repository.createBranch("a-different-branch");
		GitNormalBranch aDifferentBranch = repository.getBranch("a-different-branch");
		aDifferentBranch.checkoutTo();
		assertEquals(workspace.getBranch().getName(), "a-different-branch");
	}
	
	@Test
	public void testDifferentRepositoriesDontShareWorkspace(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository1 = GitNormalRepositorySetup.getOneWithCommit(tmpFolder);
		GitNormalRepository repository2 = GitNormalRepositorySetup.getOneWithCommit(tmpFolder);
		
		repository1.createBranch("repository-1-branch");
		repository2.createBranch("repository-2-branch");
		
		GitWorkspace workspace1 = repository1.getBranch("repository-1-branch").checkoutTo();
		GitWorkspace workspace2 = repository2.getBranch("repository-2-branch").checkoutTo();
		
		assertNotEquals(workspace1.getBranch().getName(), workspace2.getBranch().getName());
	}

}
