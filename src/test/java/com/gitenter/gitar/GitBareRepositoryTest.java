package com.gitenter.gitar;

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

public class GitBareRepositoryTest {

	@Test
	public void testAndDeleteOnNewFolderWhichDoesnotExistYet(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo.git");
		assertFalse(directory.exists());
		
		GitBareRepository repository = GitBareRepository.getInstance(directory);
		assertTrue(GitRepository.instances.containsKey(directory));
		assertTrue(directory.exists());
		assertTrue(new File(directory, "branches").isDirectory());
		assertTrue(new File(directory, "hooks").isDirectory());
		assertTrue(new File(directory, "logs").isDirectory());
		assertTrue(new File(directory, "objects").isDirectory());
		assertTrue(new File(directory, "refs").isDirectory());
		assertTrue(new File(directory, "config").isFile());
		assertTrue(new File(directory, "HEAD").isFile());
		
		GitRepository.delete(repository);
		assertFalse(GitRepository.instances.containsKey(directory));
		assertFalse(directory.exists());
	}
	
	@Test
	public void testInitOnExistingEmptyFolder(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo.git");
		directory.mkdir();
		assertTrue(directory.exists());
		
		GitBareRepository.getInstance(directory);
		assertTrue(new File(directory, "branches").isDirectory());
		assertTrue(new File(directory, "hooks").isDirectory());
		assertTrue(new File(directory, "logs").isDirectory());
		assertTrue(new File(directory, "objects").isDirectory());
		assertTrue(new File(directory, "refs").isDirectory());
		assertTrue(new File(directory, "config").isFile());
		assertTrue(new File(directory, "HEAD").isFile());
	}
	
	@Test
	public void testGetInstanceOnExistingGitFolder(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = GitBareRepositorySetup.getOneFolderStructureOnly(tmpFolder);

		/*
		 * TODO:
		 * Assert that the constructor is not being called.
		 */
		GitBareRepository.getInstance(directory);
	}
	
	@Test
	public void testInitFolderNotExist() throws IOException, GitAPIException {
		
		File directory = new File("/a/path/which/does/not/exist");
		
		assertThrows(JGitInternalException.class, () -> {
			GitBareRepository.getInstance(directory);
		});
	}
	
	@Test
	public void testInitFolderReadOnly(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo.git");
		directory.mkdir();
		directory.setReadOnly();
		
		assertThrows(JGitInternalException.class, () -> {
			GitBareRepository.getInstance(directory);
		});
	}
	
	@Test
	public void testRegisteredByRepoOfTheOtherType(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo");
		directory.mkdir();

		GitNormalRepository.getInstance(directory);
		
		assertThrows(WrongGitDirectoryTypeException.class, () -> {
			GitBareRepository.getInstance(directory);
		});
	}
	
	@Test
	public void testExistingFolderIsRepoOfTheOtherType(@TempDir File tmpFolder) throws IOException, GitAPIException {
	
		File directory = GitNormalRepositorySetup.getOneFolderStructureOnly(tmpFolder);
		
		assertThrows(WrongGitDirectoryTypeException.class, () -> {
			GitBareRepository.getInstance(directory);
		});
	}
	
	@Test
	public void testAddAHook(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitRepository repository = GitBareRepositorySetup.getOneJustInitialized(tmpFolder);
		
		File hook = new File(tmpFolder, "whatever-name-for-the-hook-file");
		hook.createNewFile();
		repository.addAHook(hook, "pre-receive");
		
		File targetHook = new File(new File(repository.directory, "hooks"), "pre-receive");
		assertTrue(targetHook.isFile());
		assertTrue(targetHook.canExecute());
	}
	
	@Test
	public void testAddHooks(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitRepository repository = GitBareRepositorySetup.getOneJustInitialized(tmpFolder);
		
		File hooks = new File(tmpFolder, "hooks");
		hooks.mkdir();
		new File(hooks, "pre-receive").createNewFile();
		repository.addHooks(hooks);
		
		File targetHook = new File(new File(repository.directory, "hooks"), "pre-receive");
		assertTrue(targetHook.isFile());
		assertTrue(targetHook.canExecute());
	}
}
