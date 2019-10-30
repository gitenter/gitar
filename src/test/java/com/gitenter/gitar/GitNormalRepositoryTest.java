package com.gitenter.gitar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gitenter.gitar.exception.WrongGitDirectoryTypeException;
import com.gitenter.gitar.setup.GitBareRepositorySetup;
import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitNormalRepositoryTest {
	
	@Test
	public void testInitAndDeleteOnNewFolderWhichDoesnotExistYet(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo");
		assertFalse(directory.exists());
		
		GitNormalRepository repository = GitNormalRepository.getInstance(directory);
		assertTrue(GitRepository.instances.containsKey(directory));
		assertTrue(directory.exists());
		assertTrue(new File(directory, ".git").isDirectory());
		
		GitRepository.delete(repository);
		assertFalse(GitRepository.instances.containsKey(directory));
		assertFalse(directory.exists());
	}
	
	@Test
	public void testInitOnExistingEmptyFolder(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo");
		directory.mkdir();
		assertTrue(directory.exists());
		
		GitNormalRepository.getInstance(directory);
		assertTrue(new File(directory, ".git").isDirectory());
	}
	
	@Test
	public void testGetInstanceOnExistingGitFolder(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = GitNormalRepositorySetup.getOneFolderStructureOnly(tmpFolder);
		
		/*
		 * TODO:
		 * Assert that the constructor is not being called.
		 */
		GitNormalRepository.getInstance(directory);
	}
	
	@Test
	public void testInitFolderNotExist() throws IOException, GitAPIException {
		
		File directory = new File("/a/path/which/does/not/exist");
		
		assertThrows(JGitInternalException.class, () -> {
			GitNormalRepository.getInstance(directory);
		});
	}
	
	@Test
	public void testInitFolderReadOnly(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo");
		directory.mkdir();
		directory.setReadOnly();
		
		assertThrows(JGitInternalException.class, () -> {
			GitNormalRepository.getInstance(directory);
		});
	}
	
	public void testDirectoryRegisteredMultipleTimes(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo");
		directory.mkdir();
		GitNormalRepository repository1 = GitNormalRepository.getInstance(directory);
		GitNormalRepository repository2 = GitNormalRepository.getInstance(directory);
		
		assertTrue(repository1 == repository2);
	}
	
	@Test
	public void testRegisteredByRepoOfTheOtherType(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo.git");
		directory.mkdir();
		
		GitBareRepository.getInstance(directory);
		
		assertThrows(WrongGitDirectoryTypeException.class, () -> {
			GitNormalRepository.getInstance(directory);
		});
	}
	
	@Test
	public void testExistingFolderIsRepoOfTheOtherType(@TempDir File tmpFolder) throws IOException, GitAPIException {
	
		File directory = GitBareRepositorySetup.getOneFolderStructureOnly(tmpFolder);
		
		assertThrows(WrongGitDirectoryTypeException.class, () -> {
			GitNormalRepository.getInstance(directory);
		});
	}
	
	@Test
	public void testCreateAndUpdateAndGetRemote(@TempDir File tmpFolder) throws IOException, GitAPIException {
	
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		
		repository.createOrUpdateRemote("origin", "/fake/url");
		GitRemote origin = repository.getRemote("origin");
		assertEquals(origin.name, "origin");
		assertEquals(origin.url, "/fake/url");
		
		repository.createOrUpdateRemote("origin", "/another/fake/url");
		origin = repository.getRemote("origin");
		assertEquals(origin.url, "/another/fake/url");
	}
}
