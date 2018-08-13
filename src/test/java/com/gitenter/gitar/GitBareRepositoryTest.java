package com.gitenter.gitar;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.setup.GitBareRepositorySetup;
import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitBareRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testInitOnNewFolder() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo.git");
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
	public void testGetInstanceOnExistingGitFolder() throws IOException, GitAPIException {
		
		File directory = GitBareRepositorySetup.getOneFolderStructureOnly(folder);

		/*
		 * TODO:
		 * Assert that the constructor is not being called.
		 */
		GitBareRepository.getInstance(directory);
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderNotExist() throws IOException, GitAPIException {
		
		File directory = new File("/a/path/which/does/not/exist");
		GitBareRepository.getInstance(directory);
	}
	
	@Test(expected = JGitInternalException.class)
	public void testInitFolderReadOnly() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo.git");
		directory.setReadOnly();
		
		GitBareRepository.getInstance(directory);
	}
	
	@Test(expected = IOException.class)
	public void testRegisteredByRepoOfTheOtherType() throws IOException, GitAPIException {
		
		File directory = folder.newFolder("repo");

		GitNormalRepository.getInstance(directory);
		GitBareRepository.getInstance(directory);
	}
	
	@Test(expected = IOException.class)
	public void testExistingFolderIsRepoOfTheOtherType() throws IOException, GitAPIException {
	
		File directory = GitNormalRepositorySetup.getOneFolderStructureOnly(folder);
		GitBareRepository.getInstance(directory);
	}
	
	@Test
	public void testAddAHook() throws IOException, GitAPIException {
		
		GitRepository repository = GitBareRepositorySetup.getOneJustInitialized(folder);
		
		File hook = folder.newFile("whatever-name-for-the-hook-file");
		repository.addAHook(hook, "pre-receive");
		
		File targetHook = new File(new File(repository.directory, "hooks"), "pre-receive");
		assertTrue(targetHook.isFile());
		assertTrue(targetHook.canExecute());
	}
	
	@Test
	public void testAddHooks() throws IOException, GitAPIException {
		
		GitRepository repository = GitBareRepositorySetup.getOneJustInitialized(folder);
		
		File hooks = folder.newFolder("hooks");
		new File(hooks, "pre-receive").createNewFile();
		repository.addHooks(hooks);
		
		File targetHook = new File(new File(repository.directory, "hooks"), "pre-receive");
		assertTrue(targetHook.isFile());
		assertTrue(targetHook.canExecute());
	}
}
