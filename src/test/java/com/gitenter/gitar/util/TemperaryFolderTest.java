package com.gitenter.gitar.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TemperaryFolderTest {

	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testMakeNewFolder() throws IOException {
		
		File directory = new File("/path/not/exist");
		assertFalse(directory.exists());
		directory = folder.newFolder("folder-name");
		assertTrue(directory.exists());
		
		File file = new File(directory, "nested-file");
		assertFalse(file.exists());
		file.createNewFile();
		assertTrue(file.exists());
	}

	@Test
	public void testFilesInTemperaryFolderWillNotMixTogether() throws IOException {
		
		File directory = new File("/path/not/exist");
		directory = folder.newFolder("folder-name");
		
		File file = new File(directory, "nested-file");
		assertFalse(file.exists());
	}
}
