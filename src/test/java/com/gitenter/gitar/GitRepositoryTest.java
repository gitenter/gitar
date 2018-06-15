package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GitRepositoryTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testBuild() throws IOException {
		
		File directory = folder.newFolder("repo");
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository jGitRepository = builder.setGitDir(directory).readEnvironment().findGitDir().build();
		
		assertEquals(directory.listFiles().length, 0);
		
		// JGit return isBare() true value even if the folder is empty.
		// Bug reported: https://bugs.eclipse.org/bugs/show_bug.cgi?id=535333
		assertTrue(jGitRepository.isBare());
	}
}
