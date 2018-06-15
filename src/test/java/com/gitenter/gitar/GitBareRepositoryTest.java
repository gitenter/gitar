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
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitRemote;
import com.gitenter.gitar.GitRepository;

public class GitBareRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	private static File getDirectory(TemporaryFolder folder) throws IOException {
		
		Random rand = new Random();
		String name = "repo-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		return folder.newFolder(name+".git");
	}
	
	static GitBareRepository getOneJustInitialized(TemporaryFolder folder) throws IOException, GitAPIException {
		
		File directory = getDirectory(folder);
		return GitBareRepository.getInstance(directory);
	}

	static GitBareRepository getOneWithCommit(TemporaryFolder folder) throws IOException, GitAPIException {
		
		GitBareRepository repository = getOneJustInitialized(folder);
		
		GitNormalRepository localRepository = GitNormalRepositoryTest.getOneWithCommit(folder);
		localRepository.createOrUpdateRemote("origin", repository.directory.toString());
		GitRemote origin = localRepository.getRemote("origin");
		localRepository.getCurrentBranch().checkoutTo().push(origin);
		
		return repository;
	}
	
	static File getOneFolderStructureOnly(TemporaryFolder folder) throws IOException, GitAPIException {
	
		File directory = getDirectory(folder);
		
		Git.init().setDirectory(directory).setBare(true).call();
		assertTrue(new File(directory, "branches").isDirectory());
		
		return directory;
	}
	
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
		
		File directory = getOneFolderStructureOnly(folder);

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
	
		File directory = GitNormalRepositoryTest.getOneFolderStructureOnly(folder);
		GitBareRepository.getInstance(directory);
	}
	
	@Test
	public void testAddAHook() throws IOException, GitAPIException {
		
		GitRepository repository = getOneJustInitialized(folder);
		
		File hook = folder.newFile("whatever-name-for-the-hook-file");
		repository.addAHook(hook, "pre-receive");
		
		File targetHook = new File(new File(repository.directory, "hooks"), "pre-receive");
		assertTrue(targetHook.isFile());
		assertTrue(targetHook.canExecute());
	}
	
	@Test
	public void testAddHooks() throws IOException, GitAPIException {
		
		GitRepository repository = getOneJustInitialized(folder);
		
		File hooks = folder.newFolder("hooks");
		new File(hooks, "pre-receive").createNewFile();
		repository.addHooks(hooks);
		
		File targetHook = new File(new File(repository.directory, "hooks"), "pre-receive");
		assertTrue(targetHook.isFile());
		assertTrue(targetHook.canExecute());
	}
}
