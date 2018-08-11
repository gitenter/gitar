package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Test;

public class GitLocalFolderTest extends GitFolderTest {

	private GitWorkspace workspaceWithFileOnRoot;
	private GitWorkspace workspaceWithComplicatedFolderStructure;
	private GitWorkspace workspaceWithEmptyFolderStructure;
	
	@Before 
	public void setupFileOnRoot() throws IOException, GitAPIException {
		
		super.setupFileOnRoot();
		workspaceWithFileOnRoot = repository.getCurrentBranch().checkoutTo();
	}
	
	@Before 
	public void setupComplicatedFolderStructure() throws IOException, GitAPIException {
		
		super.setupComplicatedFolderStructure();
		workspaceWithComplicatedFolderStructure = repository.getCurrentBranch().checkoutTo();
	}
	
	@Before
	public void setupEmptyFolderStructure() throws IOException, GitAPIException {
		
		super.setupEmptyFolderStructure();
		workspaceWithEmptyFolderStructure = repository.getCurrentBranch().checkoutTo();
	}
	
	@Test
	public void testFilesOnRoot() throws IOException, GitAPIException {
		
		GitLocalFolder folder = workspaceWithFileOnRoot.getRoot();
		
		assertEquals(folder.ls().size(), 2);
		assertTrue(folder.hasSubpath("file-1"));
		assertTrue(folder.getSubpath("file-1") instanceof GitLocalFile);
		assertTrue(folder.hasSubpath("file-2"));
		assertTrue(folder.getSubpath("file-2") instanceof GitLocalFile);
	}
	
	@Test
	public void testComplicatedFolderStructureOnRoot() throws IOException {	
		
		GitLocalFolder folder = workspaceWithComplicatedFolderStructure.getRoot();
		
		assertEquals(folder.ls().size(), 1);
		assertTrue(folder.hasSubpath("top-level-folder"));
		assertTrue(folder.getSubpath("top-level-folder") instanceof GitLocalFolder);
		
		GitLocalFolder topLevelFolder = folder.cd("top-level-folder");
		assertEquals(topLevelFolder.ls().size(), 2);
		assertTrue(topLevelFolder.hasSubpath("file-in-top-level-folder"));
		assertTrue(topLevelFolder.getSubpath("file-in-top-level-folder") instanceof GitLocalFile);
		assertTrue(topLevelFolder.hasSubpath("second-level-folder"));
		assertTrue(topLevelFolder.getSubpath("second-level-folder") instanceof GitLocalFolder);
		
		GitLocalFolder secondLevelFolder = topLevelFolder.cd("second-level-folder");
		assertEquals(secondLevelFolder.ls().size(), 1);
		assertTrue(secondLevelFolder.hasSubpath("file-in-second-level-folder"));
		assertTrue(secondLevelFolder.getSubpath("file-in-second-level-folder") instanceof GitLocalFile);
	}
	
	@Test
	public void testComplicatedFolderStructureNestedFolder() throws IOException {
		
		GitLocalFolder folder = workspaceWithComplicatedFolderStructure.getFolder("top-level-folder");
		
		assertEquals(folder.ls().size(), 2);
		assertTrue(folder.hasSubpath("file-in-top-level-folder"));
		assertTrue(folder.getSubpath("file-in-top-level-folder") instanceof GitLocalFile);
		assertTrue(folder.hasSubpath("second-level-folder"));
		assertTrue(folder.getSubpath("second-level-folder") instanceof GitLocalFolder);
		
		GitLocalFolder secondLevelFolder = folder.cd("second-level-folder");
		assertEquals(secondLevelFolder.ls().size(), 1);
		assertTrue(secondLevelFolder.hasSubpath("file-in-second-level-folder"));
		assertTrue(secondLevelFolder.getSubpath("file-in-second-level-folder") instanceof GitLocalFile);
	}
	
	@Test
	public void testComplicatedFolderStructureGetFile() throws FileNotFoundException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Navigate in local folder: the provide relativePath belongs to a file");
	    workspaceWithComplicatedFolderStructure.getFolder("top-level-folder/file-in-top-level-folder");
	}
	
	@Test 
	public void testComplicatedFolderStructureTopLevelFolderNotExist() throws FileNotFoundException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Navigate in local folder: folder not exist");
	    workspaceWithComplicatedFolderStructure.getFolder("top-level-folder-not-exist");
	}
	
	@Test 
	public void testComplicatedFolderStructureSecondLevelFolderNotExist() throws FileNotFoundException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Navigate in local folder: folder not exist");
	    workspaceWithComplicatedFolderStructure.getFolder("top-level-folder/second-level-folder-not-exist");
	}
	
	@Test
	public void testEmptyFolderStructure() throws FileNotFoundException {
		
		GitLocalFolder folder = workspaceWithEmptyFolderStructure.getRoot();
		assertEquals(folder.ls().size(), 0);
	}
	
	@Test
	public void testEmptyFolderStructureFolderNotExist() throws FileNotFoundException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Navigate in local folder: folder not exist");
	    workspaceWithEmptyFolderStructure.getFolder("folder-not-exist");
	}
	
//	private static void showHierarchy (GitPath gitPath, int level) {
//		
//		for (int i = 0; i < level; ++i) {
//			System.out.print("\t");
//		}
//		System.out.println(gitPath.getRelativePath());
//		
//		if (gitPath instanceof GitFolder) {
//			for(GitPath subpath : ((GitFolder)gitPath).list()) {
//				showHierarchy(subpath, level+1);
//			}
//		}
//	}
}
