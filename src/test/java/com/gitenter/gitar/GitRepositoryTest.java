package com.gitenter.gitar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class GitRepositoryTest {

	@Test
	public void testBuild(@TempDir File directory) throws IOException {
		
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository jGitRepository = builder.setGitDir(directory).readEnvironment().findGitDir().build();
		
		assertEquals(directory.listFiles().length, 0);
		
		// JGit return isBare() true value even if the folder is empty.
		// Bug reported: https://bugs.eclipse.org/bugs/show_bug.cgi?id=535333
		assertTrue(jGitRepository.isBare());
	}
}
