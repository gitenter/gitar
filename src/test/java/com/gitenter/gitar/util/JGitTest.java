package com.gitenter.gitar.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class JGitTest {

	@Test
	public void testAddAndCommit(@TempDir File tmpFolder) throws IOException, IllegalStateException, GitAPIException {

		File directory = new File(tmpFolder, "repo");
		directory.mkdir();
		Git.init().setDirectory(directory).setBare(false).call();

		new File(directory, "a-file").createNewFile();
		
		/*
		 * Can't use repository build by "FileRepositoryBuilder".
		 * That's for a bare repository.
		 */
		try (Git git = Git.open(directory)) {
			git.add().addFilepattern(".").call();
		}
	}

	@Test
	public void testBuilder(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo");
		directory.mkdir();
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(directory).readEnvironment().findGitDir().build();
		
		assertTrue(repository.isBare());
	}
	
	@Test
	public void testJGitRepository(@TempDir File tmpFolder)  throws IOException, GitAPIException {
		
		File directory = new File(tmpFolder, "repo");
		directory.mkdir();
		Git.init().setDirectory(directory).setBare(false).call();
		
		new File(directory, "a-file").createNewFile();
		
		try (Git git = Git.open(directory)) {
			
			Repository repository = git.getRepository();
			
			assertEquals(git.branchList().call().size(), 0);
			assertEquals("master", repository.getBranch());
			
			git.add().addFilepattern(".").call();
			git.commit().setMessage("a message").call();
			
			assertEquals(git.branchList().call().size(), 1);
			git.checkout().setName("master").call();
			
			assertEquals("master", repository.getBranch());
		}
	}
}