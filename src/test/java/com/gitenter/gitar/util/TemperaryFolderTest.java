package com.gitenter.gitar.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class TemperaryFolderTest {

	@Test
	public void testMakeNewFolder(@TempDir File tmpFolder) throws IOException {
		
		File directory = new File("/path/not/exist");
		assertFalse(directory.exists());
		directory = new File(tmpFolder, "subfolder");
		assertFalse(directory.exists());
		directory.mkdir();
		assertTrue(directory.exists());
		
		File file = new File(directory, "nested-file");
		assertFalse(file.exists());
		file.createNewFile();
		assertTrue(file.exists());
	}
}
