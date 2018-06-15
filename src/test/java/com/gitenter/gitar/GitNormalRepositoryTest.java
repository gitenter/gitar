package com.gitenter.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitNormalBranch;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitRemote;
import com.gitenter.gitar.GitWorkspace;

public class GitNormalRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	private static File getDirectory(TemporaryFolder folder) throws IOException {
		
		/*
		 * Although temporary folders on different test will not mix together,
		 * there is possibility that one test will need to initialize multiple
		 * repositories (e.g. GitWorkspaceTest.testDifferentRepositoriesDontShareWorkspace),
		 * so we use this random number to make sure folders are not crashing.
		 */
		Random rand = new Random();
		String name = "repo-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		return folder.newFolder(name);
	}
	
	static GitNormalRepository getOneJustInitialized(TemporaryFolder folder) throws IOException, GitAPIException {
		
		File directory = getDirectory(folder);
		return GitNormalRepository.getInstance(directory);
	}
	
	static GitNormalRepository getOneWithCommit(TemporaryFolder folder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = getOneJustInitialized(folder);
		
		GitNormalBranch master = repository.getCurrentBranch();
		GitWorkspace workspace = master.checkoutTo();
		GitWorkspaceTest.addACommit(workspace, "First commit message");
		
		return repository;
	}
	
	static GitNormalRepository getOneWithCleanWorkspace(TemporaryFolder folder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file = folder.newFile("file");
		file.createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add file");
		
		GitWorkspaceTest.deleteAll(workspace);
		
		return repository;
	}
	
	static File getOneFolderStructureOnly(TemporaryFolder folder) throws IOException, GitAPIException {
		
		File directory = getDirectory(folder);
		
		Git.init().setDirectory(directory).call();
		assertTrue(new File(directory, ".git").isDirectory());
		
		return directory;
	}

	@Test
	public void testInitOnNewFolder() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		GitNormalRepository.getInstance(directory);
		
		assertTrue(new File(directory, ".git").isDirectory());
	}
	
	@Test
	public void testGetInstanceOnExistingGitFolder() throws IOException, GitAPIException {
		
		File directory = getOneFolderStructureOnly(folder);
		
		/*
		 * TODO:
		 * Assert that the constructor is not being called.
		 */
		GitNormalRepository.getInstance(directory);
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderNotExist() throws IOException, GitAPIException {
		
		File directory = new File("/a/path/which/does/not/exist");
		GitNormalRepository.getInstance(directory);
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderReadOnly() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		directory.setReadOnly();
		
		GitNormalRepository.getInstance(directory);
	}
	
	public void testDirectoryRegisteredMultipleTimes() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		GitNormalRepository repository1 = GitNormalRepository.getInstance(directory);
		GitNormalRepository repository2 = GitNormalRepository.getInstance(directory);
		
		assertTrue(repository1 == repository2);
	}
	
	@Test(expected = IOException.class)
	public void testRegisteredByRepoOfTheOtherType() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo.git");
		
		GitBareRepository.getInstance(directory);
		GitNormalRepository.getInstance(directory);
	}
	
	@Test(expected = IOException.class)
	public void testExistingFolderIsRepoOfTheOtherType() throws IOException, GitAPIException {
	
		File directory = GitBareRepositoryTest.getOneFolderStructureOnly(folder);
		GitNormalRepository.getInstance(directory);
	}
	
	@Test
	public void testCreateAndUpdateAndGetRemote() throws IOException, GitAPIException {
	
		GitNormalRepository repository = getOneJustInitialized(folder);
		
		repository.createOrUpdateRemote("origin", "/fake/url");
		GitRemote origin = repository.getRemote("origin");
		assertEquals(origin.name, "origin");
		assertEquals(origin.url, "/fake/url");
		
		repository.createOrUpdateRemote("origin", "/another/fake/url");
		origin = repository.getRemote("origin");
		assertEquals(origin.url, "/another/fake/url");
	}
}
