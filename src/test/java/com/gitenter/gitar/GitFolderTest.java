package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitFolderTest {
	
	@Rule public TemporaryFolder tempFolder = new TemporaryFolder();
	@Rule public ExpectedException thrown = ExpectedException.none();

	private GitWorkspace workspaceWithEmptyFolderStructure;
	private GitWorkspace workspaceWithFileOnRoot;
	private GitWorkspace workspaceWithComplicatedFolderStructure;
	
	private GitCommit commitWithEmptyFolderStructure;
	private GitCommit commitWithFileOnRoot;
	private GitCommit commitWithComplicatedFolderStructure;
	
	@Before
	public void setupEmptyFolderStructure() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCleanWorkspace(tempFolder);
		workspaceWithEmptyFolderStructure = repository.getCurrentBranch().checkoutTo();
		commitWithEmptyFolderStructure = repository.getCurrentBranch().getHead();
	}
	
	@Before 
	public void setupFileOnRoot() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithFileOnRoot(tempFolder);
		workspaceWithFileOnRoot = repository.getCurrentBranch().checkoutTo();
		commitWithFileOnRoot = repository.getCurrentBranch().getHead();
	}
	
	@Before 
	public void setupComplicatedFolderStructure() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithComplicatedFolderStructure(tempFolder);
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
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Local folder path folder-not-exist not exist");
	    workspaceWithEmptyFolderStructure.getFolder("folder-not-exist");
	}
	
	@Test
	public void testEmptyHistoricalFolderStructureFolderNotExist() throws IOException {
		
		thrown.expect(IOException.class);
	    thrown.expectMessage("Git folder path folder-not-exist not exist");
	    commitWithEmptyFolderStructure.getFolder("folder-not-exist");
	}
	
	@Test 
	public void testEmptyLocalFolderFileNotExist() throws FileNotFoundException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Local file path ./file-not-exist not exist");
	    workspaceWithEmptyFolderStructure.getRoot().getFile("file-not-exist");
	}
	
	@Test 
	public void testEmptyHistoricalFolderFileNotExist() throws IOException {
		
		/*
		 * Exceptional condition cannot distinguish if it is a folder or a file.
		 */
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Git path ./file-not-exist not exist");
	    commitWithEmptyFolderStructure.getRoot().getFile("file-not-exist");
	}
	
	private void testFilesOnRootHelper(GitFolder folder) throws FileNotFoundException {
		
		assertEquals(folder.ls().size(), 2);
		assertTrue(folder.hasSubpath("file-1"));
		assertTrue(folder.getSubpath("file-1") instanceof GitFile);
		assertTrue(folder.hasSubpath("file-2"));
		assertTrue(folder.getSubpath("file-2") instanceof GitFile);
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
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Local folder path ./file-1 belongs to a file");
	    workspaceWithFileOnRoot.getRoot().cd("file-1");
	}
	
	@Test 
	public void testAccessHistoricalFileAsFolderOnRoot() throws IOException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Git folder path ./file-1 belongs to a file");
	    commitWithFileOnRoot.getRoot().cd("file-1");
	}
	
	private void testComplicatedFolderStructureOnRootHelper(GitFolder folder) throws FileNotFoundException {
		
		assertEquals(folder.ls().size(), 1);
		assertTrue(folder.hasSubpath("top-level-folder"));
		assertTrue(folder.getSubpath("top-level-folder") instanceof GitFolder);
		
		GitFolder topLevelFolder = folder.cd("top-level-folder");
		assertEquals(topLevelFolder.ls().size(), 2);
		assertTrue(topLevelFolder.hasSubpath("file-in-top-level-folder"));
		assertTrue(topLevelFolder.getSubpath("file-in-top-level-folder") instanceof GitFile);
		assertTrue(topLevelFolder.hasSubpath("second-level-folder"));
		assertTrue(topLevelFolder.getSubpath("second-level-folder") instanceof GitFolder);
		
		GitFolder secondLevelFolder = topLevelFolder.cd("second-level-folder");
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
	
	private void testComplicatedFolderStructureNestedFolder(GitFolder folder) throws FileNotFoundException {
		
		assertEquals(folder.ls().size(), 2);
		assertTrue(folder.hasSubpath("file-in-top-level-folder"));
		assertTrue(folder.getSubpath("file-in-top-level-folder") instanceof GitFile);
		assertTrue(folder.hasSubpath("second-level-folder"));
		assertTrue(folder.getSubpath("second-level-folder") instanceof GitFolder);
		
		GitFolder secondLevelFolder = folder.cd("second-level-folder");
		assertEquals(secondLevelFolder.ls().size(), 1);
		assertTrue(secondLevelFolder.hasSubpath("file-in-second-level-folder"));
		assertTrue(secondLevelFolder.getSubpath("file-in-second-level-folder") instanceof GitFile);
	}
	
	@Test
	public void testComplicatedLocalFolderStructureNestedFolder() throws IOException {
		
		GitLocalFolder folder = workspaceWithComplicatedFolderStructure.getFolder("top-level-folder");
		testComplicatedFolderStructureNestedFolder(folder);
	}
	
	@Test
	public void testComplicatedHistoricalFolderStructureNestedFolder() throws IOException {
	
		GitHistoricalFolder folder = commitWithComplicatedFolderStructure.getFolder("top-level-folder");
		testComplicatedFolderStructureNestedFolder(folder);
	}

	@Test
	public void testComplicatedLocalFolderStructureGetFolderAsFile() throws FileNotFoundException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Local file path ./top-level-folder belongs to a folder");
	    workspaceWithComplicatedFolderStructure.getRoot().getFile("top-level-folder");
	}
	
	@Test
	public void testComplicatedHistoricalFolderStructureGetFolderAsFile() throws IOException {
		
		thrown.expect(IOException.class);
	    thrown.expectMessage("Git file path ./top-level-folder belongs to a folder");
	    commitWithComplicatedFolderStructure.getRoot().getFile("top-level-folder");
	}
	
	@Test
	public void testComplicatedLocalFolderStructureGetFileAsFolder() throws FileNotFoundException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Local folder path top-level-folder/file-in-top-level-folder belongs to a file");
	    workspaceWithComplicatedFolderStructure.getFolder("top-level-folder/file-in-top-level-folder");
	}
	
	@Test
	public void testComplicatedHistoricalFolderStructureGetFileAsFolder() throws IOException {
		
		thrown.expect(IOException.class);
	    thrown.expectMessage("Git folder path top-level-folder/file-in-top-level-folder belongs to a file");
	    commitWithComplicatedFolderStructure.getFolder("top-level-folder/file-in-top-level-folder");
	}
	
	@Test 
	public void testComplicatedLocalFolderStructureTopLevelFolderNotExist() throws FileNotFoundException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Local folder path top-level-folder-not-exist not exist");
	    workspaceWithComplicatedFolderStructure.getFolder("top-level-folder-not-exist");
	}
	
	@Test 
	public void testComplicatedHistoricalFolderStructureTopLevelFolderNotExist() throws IOException {
		
		thrown.expect(IOException.class);
	    thrown.expectMessage("Git folder path top-level-folder-not-exist not exist");
	    commitWithComplicatedFolderStructure.getFolder("top-level-folder-not-exist");
	}
	
	@Test 
	public void testComplicatedLocalFolderStructureSecondLevelFolderNotExist() throws FileNotFoundException {
		
		thrown.expect(FileNotFoundException.class);
	    thrown.expectMessage("Local folder path top-level-folder/second-level-folder-not-exist not exist");
	    workspaceWithComplicatedFolderStructure.getFolder("top-level-folder/second-level-folder-not-exist");
	}
	
	@Test 
	public void testComplicatedHistoricalFolderStructureSecondLevelFolderNotExist() throws IOException {
		
		thrown.expect(IOException.class);
	    thrown.expectMessage("Git folder path top-level-folder/second-level-folder-not-exist not exist");
	    commitWithComplicatedFolderStructure.getFolder("top-level-folder/second-level-folder-not-exist");
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
