package com.gitenter.gitar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gitenter.gitar.setup.GitBareRepositorySetup;
import com.gitenter.gitar.setup.GitNormalRepositorySetup;
import com.gitenter.gitar.setup.GitWorkspaceSetup;

public class GitBranchTest {

	@Test
	public void testBranchNotExist(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		assertEquals(repository.getBranch("branch-not-exist"), null);
	}
	
	/*
	 * TODO:
	 * 
	 * Currently returns `RefNotFoundException` with no HEAD error.
	 * However in git the actual return is:
	 * > fatal: Not a valid object name: 'master'.
	 * 
	 * Should correct the difference later, and/or define customized
	 * classes in here.
	 */
	@Test
	public void testCreateBranchEmptyNormalRepository(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		
		assertThrows(RefNotFoundException.class, () -> {
			repository.createBranch("a-branch");
		});
	}
	
	@Test
	public void testCheckoutToFirstCommit(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		
		GitNormalBranch currentBranch = repository.getCurrentBranch();
		assertEquals(currentBranch.getName(), "master");
		assertEquals(repository.getBranches().size(), 0);
		assertEquals(repository.getBranch("master"), null);
		assertEquals(repository.getBranch("refs/heads/master"), null);
		
		GitWorkspace workspace = currentBranch.checkoutTo();
		GitWorkspaceSetup.addACommit(workspace, "First commit message");
		
		assertEquals(currentBranch.getName(), "master");
		assertEquals(repository.getBranches().size(), 1);
		assertNotEquals(repository.getBranch("master"), null);
		assertNotEquals(repository.getBranch("refs/heads/master"), null);
	}

	@Test
	public void testSwitchBetweenMultipleBranchesNormalRepository(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCommit(tmpFolder);
		
		repository.createBranch("a-branch");
		repository.createBranch("another-branch");
		
		assertEquals(repository.getBranches().size(), 3);
		assertEquals(repository.getCurrentBranch().getName(), "master");
		assertNotEquals(repository.getBranch("a-branch"), null);
		assertNotEquals(repository.getBranch("another-branch"), null);
		
		repository.getBranch("a-branch").checkoutTo();
		assertEquals(repository.getCurrentBranch().getName(), "a-branch");
		
		repository.getBranch("another-branch").checkoutTo();
		assertEquals(repository.getCurrentBranch().getName(), "another-branch");
	}
	
	@Test
	public void testCreateBranchBareRepository(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitBareRepository repository = GitBareRepositorySetup.getOneWithCommit(tmpFolder);
		
		assertEquals(repository.getBranches().size(), 1);
		assertNotEquals(repository.getBranch("master"), null);
		
		repository.createBranch("a-branch");
		repository.createBranch("another-branch");
		
		assertEquals(repository.getBranches().size(), 3);
		assertNotEquals(repository.getBranch("a-branch"), null);
		assertNotEquals(repository.getBranch("another-branch"), null);
	}
	
	@Test
	public void testGetLogNormalRepository(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		
		GitNormalBranch master = repository.getCurrentBranch();
		GitWorkspace workspace = master.checkoutTo();
		GitWorkspaceSetup.addACommit(workspace, "First commit message to test getLog()");
		GitWorkspaceSetup.addACommit(workspace, "Second commit message to test getLog()");
		GitWorkspaceSetup.addACommit(workspace, "Third commit message to test getLog()");
		GitWorkspaceSetup.addACommit(workspace, "Fourth commit message to test getLog()");
		
		List<GitCommit> log = master.getLog();
		assertEquals(log.size(), 4);
		assertEquals(log.get(0).getMessage(), "Fourth commit message to test getLog()");
		assertEquals(log.get(1).getMessage(), "Third commit message to test getLog()");
		assertEquals(log.get(2).getMessage(), "Second commit message to test getLog()");
		assertEquals(log.get(3).getMessage(), "First commit message to test getLog()");
		String[] shas = new String[] {
				log.get(0).getSha(),
				log.get(1).getSha(),
				log.get(2).getSha(),
				log.get(3).getSha()
				};
			
		log = master.getLog(1, 2);
		assertEquals(log.size(), 1);
		assertEquals(log.get(0).getMessage(), "Second commit message to test getLog()");
		
		log = master.getLog(GitCommit.EMPTY_SHA, shas[2]);
		assertEquals(log.size(), 2);
		assertEquals(log.get(0).getMessage(), "Second commit message to test getLog()");
		assertEquals(log.get(1).getMessage(), "First commit message to test getLog()");
		
		log = master.getLog(shas[3], shas[1]);
		assertEquals(log.size(), 2);
		assertEquals(log.get(0).getMessage(), "Third commit message to test getLog()");
		assertEquals(log.get(1).getMessage(), "Second commit message to test getLog()");
	}
	
	@Test
	public void testExistCommitEvenEmptyFolderStructureShaNotEmpty(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCleanWorkspace(tmpFolder);
		GitCommit commit = repository.getCurrentBranch().getHead();
		assertNotEquals(commit.getSha(), GitCommit.EMPTY_SHA);
	}
}
