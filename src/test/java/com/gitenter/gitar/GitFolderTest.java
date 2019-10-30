package com.gitenter.gitar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitFolderTest {
	
	@TempDir 
	File tmpFolder;

	private GitWorkspace workspaceWithEmptyFolderStructure;
	private GitWorkspace workspaceWithFileOnRoot;
	private GitWorkspace workspaceWithComplicatedFolderStructure;
	
	private GitCommit commitWithEmptyFolderStructure;
	private GitCommit commitWithFileOnRoot;
	private GitCommit commitWithComplicatedFolderStructure;
	
	@BeforeEach
	public void setupEmptyFolderStructure() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCleanWorkspace(tmpFolder);
		workspaceWithEmptyFolderStructure = repository.getCurrentBranch().checkoutTo();
		commitWithEmptyFolderStructure = repository.getCurrentBranch().getHead();
	}
	
	@BeforeEach
	public void setupFileOnRoot() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithFileOnRoot(tmpFolder);
		workspaceWithFileOnRoot = repository.getCurrentBranch().checkoutTo();
		commitWithFileOnRoot = repository.getCurrentBranch().getHead();
	}
	
	@BeforeEach
	public void setupComplicatedFolderStructure() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithComplicatedFolderStructure(tmpFolder);
		workspaceWithComplicatedFolderStructure = repository.getCurrentBranch().checkoutTo();
		commitWithComplicatedFolderStructure = repository.getCurrentBranch().getHead();
	}
	
	private void testEmptyFolderStructureHelper(GitFolder folder) {
		assertEquals(folder.ls().size(), 0);
	}
	
	@Test
	public void testEmptyLocalFolderStructure() throws FileNotFoundException {
		
		GitLocalFolder folder = workspaceWithEmptyFolderStructure.getRoot();
		testEmptyFolderStructureHelper(folder);
	}
	
	@Test
	public void testEmptyHistoricalFolderStructure() throws IOException {
		
		GitHistoricalFolder folder = commitWithEmptyFolderStructure.getRoot();
		testEmptyFolderStructureHelper(folder);
	}
	
	@Test
	public void testEmptyLocalFolderStructureFolderNotExist() throws FileNotFoundException {
		
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			workspaceWithEmptyFolderStructure.getFolder("folder-not-exist");
		});
		assertEquals(expectedEx.getMessage(), "Local folder path folder-not-exist not exist");
	}
	
	@Test
	public void testEmptyHistoricalFolderStructureFolderNotExist() throws IOException {

		IOException expectedEx = assertThrows(IOException.class, () -> {
			commitWithEmptyFolderStructure.getFolder("folder-not-exist");
		});
		assertEquals(expectedEx.getMessage(), "Git folder path folder-not-exist not exist");
	}
	
	@Test 
	public void testEmptyLocalFolderFileNotExist() throws FileNotFoundException {
		
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			workspaceWithEmptyFolderStructure.getRoot().getFile("file-not-exist");
		});
		assertEquals(expectedEx.getMessage(), "Local file path ./file-not-exist not exist");
	}
	
	@Test 
	public void testEmptyHistoricalFolderFileNotExist() throws IOException {
		
		/*
		 * Exceptional condition cannot distinguish if it is a folder or a file.
		 */
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			commitWithEmptyFolderStructure.getRoot().getFile("file-not-exist");
		});
		assertEquals(expectedEx.getMessage(), "Git path ./file-not-exist not exist");
	}
	
	private void testFilesOnRootHelper(GitFolder folder) throws FileNotFoundException {
		
		assertEquals(folder.ls().size(), 2);
		assertTrue(folder.hasSubpath("file-on-root-1"));
		assertTrue(folder.getSubpath("file-on-root-1") instanceof GitFile);
		assertTrue(folder.hasSubpath("file-on-root-2"));
		assertTrue(folder.getSubpath("file-on-root-2") instanceof GitFile);
	}
	
	@Test
	public void testLocalFilesOnRoot() throws IOException, GitAPIException {
		
		GitLocalFolder folder = workspaceWithFileOnRoot.getRoot();
		testFilesOnRootHelper(folder);
	}
	
	@Test
	public void testHistoricalFilesOnRoot() throws IOException, GitAPIException {
		
		GitHistoricalFolder folder = commitWithFileOnRoot.getRoot();
		testFilesOnRootHelper(folder);
	}
	
	@Test 
	public void testAccessLocalFileAsFolderOnRoot() throws FileNotFoundException {
		
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			workspaceWithFileOnRoot.getRoot().cd("file-on-root-1");
		});
		assertEquals(expectedEx.getMessage(), "Local folder path ./file-on-root-1 belongs to a file");
	}
	
	@Test 
	public void testAccessHistoricalFileAsFolderOnRoot() throws IOException {
		
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			commitWithFileOnRoot.getRoot().cd("file-on-root-1");
		});
		assertEquals(expectedEx.getMessage(), "Git folder path ./file-on-root-1 belongs to a file");
	}
	
	private void testComplicatedFolderStructureOnRootHelper(GitFolder folder) throws FileNotFoundException {
		
		assertEquals(folder.ls().size(), 3);
		assertTrue(folder.hasSubpath("1-file-on-root-along-with-folders"));
		assertTrue(folder.getSubpath("1-file-on-root-along-with-folders") instanceof GitFile);
		assertTrue(folder.hasSubpath("2-top-level-folder"));
		assertTrue(folder.getSubpath("2-top-level-folder") instanceof GitFolder);
		assertTrue(folder.hasSubpath("3-file-on-root-along-with-folders"));
		assertTrue(folder.getSubpath("3-file-on-root-along-with-folders") instanceof GitFile);
		
		GitFolder topLevelFolder = folder.cd("2-top-level-folder");
		testComplicatedFolderStructureNestedFolder(topLevelFolder);
	}
	
	private void testComplicatedFolderStructureNestedFolder(GitFolder topLevelFolder) throws FileNotFoundException {
		
		assertEquals(topLevelFolder.ls().size(), 3);
		assertTrue(topLevelFolder.hasSubpath("1-file-in-top-level-folder"));
		assertTrue(topLevelFolder.getSubpath("1-file-in-top-level-folder") instanceof GitFile);
		assertTrue(topLevelFolder.hasSubpath("2-second-level-folder"));
		assertTrue(topLevelFolder.getSubpath("2-second-level-folder") instanceof GitFolder);
		assertTrue(topLevelFolder.hasSubpath("3-file-in-top-level-folder"));
		assertTrue(topLevelFolder.getSubpath("3-file-in-top-level-folder") instanceof GitFile);
		
		GitFolder secondLevelFolder = topLevelFolder.cd("2-second-level-folder");
		assertEquals(secondLevelFolder.ls().size(), 1);
		assertTrue(secondLevelFolder.hasSubpath("file-in-second-level-folder"));
		assertTrue(secondLevelFolder.getSubpath("file-in-second-level-folder") instanceof GitFile);
	}
	
	@Test
	public void testComplicatedLocalFolderStructureOnRoot() throws IOException {	
		
		GitLocalFolder folder = workspaceWithComplicatedFolderStructure.getRoot();
		testComplicatedFolderStructureOnRootHelper(folder);
	}
	
	@Test
	public void testComplicatedHistoricalFolderStructureOnRoot() throws IOException {	
	
		GitHistoricalFolder folder = commitWithComplicatedFolderStructure.getRoot();
		testComplicatedFolderStructureOnRootHelper(folder);
	}
	
	@Test
	public void testComplicatedLocalFolderStructureNestedFolder() throws IOException {
		
		GitLocalFolder folder = workspaceWithComplicatedFolderStructure.getFolder("2-top-level-folder");
		testComplicatedFolderStructureNestedFolder(folder);
	}
	
	@Test
	public void testComplicatedHistoricalFolderStructureNestedFolder() throws IOException {
	
		GitHistoricalFolder folder = commitWithComplicatedFolderStructure.getFolder("2-top-level-folder");
		testComplicatedFolderStructureNestedFolder(folder);
	}

	@Test
	public void testComplicatedLocalFolderStructureGetFolderAsFile() throws FileNotFoundException {
		
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			workspaceWithComplicatedFolderStructure.getRoot().getFile("2-top-level-folder");
		});
		assertEquals(expectedEx.getMessage(), "Local file path ./2-top-level-folder belongs to a folder");
	}
	
	@Test
	public void testComplicatedHistoricalFolderStructureGetFolderAsFile() throws IOException {
		
		IOException expectedEx = assertThrows(IOException.class, () -> {
			commitWithComplicatedFolderStructure.getRoot().getFile("2-top-level-folder");
		});
		assertEquals(expectedEx.getMessage(), "Git file path ./2-top-level-folder belongs to a folder");
	}
	
	@Test
	public void testComplicatedLocalFolderStructureGetFileAsFolder() throws FileNotFoundException {
		
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			workspaceWithComplicatedFolderStructure.getFolder("2-top-level-folder/1-file-in-top-level-folder");
		});
		assertEquals(expectedEx.getMessage(), "Local folder path 2-top-level-folder/1-file-in-top-level-folder belongs to a file");
	}
	
	@Test
	public void testComplicatedHistoricalFolderStructureGetFileAsFolder() throws IOException {
		
		IOException expectedEx = assertThrows(IOException.class, () -> {
			commitWithComplicatedFolderStructure.getFolder("2-top-level-folder/1-file-in-top-level-folder");
		});
		assertEquals(expectedEx.getMessage(), "Git folder path 2-top-level-folder/1-file-in-top-level-folder belongs to a file");
	}
	
	@Test 
	public void testComplicatedLocalFolderStructureTopLevelFolderNotExist() throws FileNotFoundException {
		
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			workspaceWithComplicatedFolderStructure.getFolder("top-level-folder-not-exist");
		});
		assertEquals(expectedEx.getMessage(), "Local folder path top-level-folder-not-exist not exist");
	}
	
	@Test 
	public void testComplicatedHistoricalFolderStructureTopLevelFolderNotExist() throws IOException {
		
		IOException expectedEx = assertThrows(IOException.class, () -> {
			commitWithComplicatedFolderStructure.getFolder("top-level-folder-not-exist");
		});
		assertEquals(expectedEx.getMessage(), "Git folder path top-level-folder-not-exist not exist");
	}
	
	@Test 
	public void testComplicatedLocalFolderStructureSecondLevelFolderNotExist() throws FileNotFoundException {
		
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			workspaceWithComplicatedFolderStructure.getFolder("top-level-folder/second-level-folder-not-exist");
		});
		assertEquals(expectedEx.getMessage(), "Local folder path top-level-folder/second-level-folder-not-exist not exist");
	}
	
	@Test 
	public void testComplicatedHistoricalFolderStructureSecondLevelFolderNotExist() throws IOException {
		
		FileNotFoundException expectedEx = assertThrows(FileNotFoundException.class, () -> {
			commitWithComplicatedFolderStructure.getFolder("top-level-folder/second-level-folder-not-exist");
		});
		assertEquals(expectedEx.getMessage(), "Git folder path top-level-folder/second-level-folder-not-exist not exist");
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
