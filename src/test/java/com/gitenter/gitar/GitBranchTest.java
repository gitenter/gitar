package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitNormalBranch;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitWorkspace;

public class GitBranchTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testBranchNotExist() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
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
	@Test(expected = RefNotFoundException.class)
	public void testCreateBranchEmptyNormalRepository() throws IOException, GitAPIException {
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		repository.createBranch("a-branch");
	}
	
	@Test
	public void testCheckoutToFirstCommit() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		
		GitNormalBranch currentBranch = repository.getCurrentBranch();
		assertEquals(currentBranch.getName(), "master");
		assertEquals(repository.getBranches().size(), 0);
		assertEquals(repository.getBranch("master"), null);
		
		GitWorkspace workspace = currentBranch.checkoutTo();
		GitWorkspaceTest.addACommit(workspace, "First commit message");
		
		assertEquals(currentBranch.getName(), "master");
		assertEquals(repository.getBranches().size(), 1);
		assertNotEquals(repository.getBranch("master"), null);
	}

	@Test
	public void testSwitchBetweenMultipleBranchesNormalRepository() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneWithCommit(folder);
		
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
	public void testCreateBranchBareRepository() throws IOException, GitAPIException {
		
		GitBareRepository repository = GitBareRepositoryTest.getOneWithCommit(folder);
		
		assertEquals(repository.getBranches().size(), 1);
		assertNotEquals(repository.getBranch("master"), null);
		
		repository.createBranch("a-branch");
		repository.createBranch("another-branch");
		
		assertEquals(repository.getBranches().size(), 3);
		assertNotEquals(repository.getBranch("a-branch"), null);
		assertNotEquals(repository.getBranch("another-branch"), null);
	}
	
	@Test
	public void testGetLogNormalRepository() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		
		GitNormalBranch master = repository.getCurrentBranch();
		GitWorkspace workspace = master.checkoutTo();
		GitWorkspaceTest.addACommit(workspace, "First commit message to test getLog()");
		GitWorkspaceTest.addACommit(workspace, "Second commit message to test getLog()");
		GitWorkspaceTest.addACommit(workspace, "Third commit message to test getLog()");
		GitWorkspaceTest.addACommit(workspace, "Fourth commit message to test getLog()");
		
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
	public void testExistCommitEvenEmptyFolderStructureShaNotEmpty() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneWithCleanWorkspace(folder);
		GitCommit commit = repository.getCurrentBranch().getHead();
		assertNotEquals(commit.getSha(), GitCommit.EMPTY_SHA);
	}
}
