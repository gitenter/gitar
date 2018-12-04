package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.exception.WrongGitDirectoryTypeException;
import com.gitenter.gitar.setup.GitBareRepositorySetup;
import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitNormalRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testInitOnNewFolder() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");
		GitNormalRepository.getInstance(directory);
		
		assertTrue(new File(directory, ".git").isDirectory());
	}
	
	@Test
	public void testGetInstanceOnExistingGitFolder() throws IOException, GitAPIException {
		
		File directory = GitNormalRepositorySetup.getOneFolderStructureOnly(folder);
		
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
	
	@Test(expected = WrongGitDirectoryTypeException.class)
	public void testRegisteredByRepoOfTheOtherType() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo.git");
		
		GitBareRepository.getInstance(directory);
		GitNormalRepository.getInstance(directory);
	}
	
	@Test(expected = WrongGitDirectoryTypeException.class)
	public void testExistingFolderIsRepoOfTheOtherType() throws IOException, GitAPIException {
	
		File directory = GitBareRepositorySetup.getOneFolderStructureOnly(folder);
		GitNormalRepository.getInstance(directory);
	}
	
	@Test
	public void testCreateAndUpdateAndGetRemote() throws IOException, GitAPIException {
	
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(folder);
		
		repository.createOrUpdateRemote("origin", "/fake/url");
		GitRemote origin = repository.getRemote("origin");
		assertEquals(origin.name, "origin");
		assertEquals(origin.url, "/fake/url");
		
		repository.createOrUpdateRemote("origin", "/another/fake/url");
		origin = repository.getRemote("origin");
		assertEquals(origin.url, "/another/fake/url");
	}
}
