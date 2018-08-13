package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitWorkspaceTest {

	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testWorkspaceChangesBranchAfterCheckout() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCommit(folder);
		GitNormalBranch master = repository.getCurrentBranch();
		GitWorkspace workspace = master.checkoutTo();
		assertEquals(workspace.getBranch().getName(), "master");
		
		repository.createBranch("a-different-branch");
		GitNormalBranch aDifferentBranch = repository.getBranch("a-different-branch");
		aDifferentBranch.checkoutTo();
		assertEquals(workspace.getBranch().getName(), "a-different-branch");
	}
	
	@Test
	public void testDifferentRepositoriesDontShareWorkspace() throws IOException, GitAPIException {
		
		GitNormalRepository repository1 = GitNormalRepositorySetup.getOneWithCommit(folder);
		GitNormalRepository repository2 = GitNormalRepositorySetup.getOneWithCommit(folder);
		
		repository1.createBranch("repository-1-branch");
		repository2.createBranch("repository-2-branch");
		
		GitWorkspace workspace1 = repository1.getBranch("repository-1-branch").checkoutTo();
		GitWorkspace workspace2 = repository2.getBranch("repository-2-branch").checkoutTo();
		
		assertNotEquals(workspace1.getBranch().getName(), workspace2.getBranch().getName());
	}

}
