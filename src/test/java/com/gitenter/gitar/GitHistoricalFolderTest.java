package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitHistoricalFolderTest {

	@Rule public TemporaryFolder folder = new TemporaryFolder();
	@Rule public ExpectedException thrown = ExpectedException.none();
	
	private GitCommit commitWithEmptyFolderStructure;
	private GitCommit commitWithFileOnRoot;
	private GitCommit commitWithComplicatedFolderStructure;
	
	@Before
	public void setupEmptyFolderStructure() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCleanWorkspace(folder);
		commitWithEmptyFolderStructure = repository.getCurrentBranch().getHead();
	}
	
	@Before 
	public void setupFileOnRoot() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithFileOnRoot(folder);
		commitWithFileOnRoot = repository.getCurrentBranch().getHead();
	}
	
	@Before 
	public void setupComplicatedFolderStructure() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithComplicatedFolderStructure(folder);
		commitWithComplicatedFolderStructure = repository.getCurrentBranch().getHead();
	}
	
	@Test
	public void testFilesOnRoot() throws IOException, GitAPIException {
		
		GitHistoricalFolder folder = commitWithFileOnRoot.getRoot();
		
		assertEquals(folder.ls().size(), 2);
		assertTrue(folder.hasSubpath("file-1"));
		assertTrue(folder.getSubpath("file-1") instanceof GitHistoricalFile);
		assertTrue(folder.hasSubpath("file-2"));
		assertTrue(folder.getSubpath("file-2") instanceof GitHistoricalFile);
	}
	
	@Test
	public void testComplicatedFolderStructureOnRoot() throws IOException {	
		
		GitHistoricalFolder folder = commitWithComplicatedFolderStructure.getRoot();
		
		assertEquals(folder.ls().size(), 1);
		assertTrue(folder.hasSubpath("top-level-folder"));
		assertTrue(folder.getSubpath("top-level-folder") instanceof GitHistoricalFolder);
		
		GitHistoricalFolder topLevelFolder = folder.cd("top-level-folder");
		assertEquals(topLevelFolder.ls().size(), 2);
		assertTrue(topLevelFolder.hasSubpath("file-in-top-level-folder"));
		assertTrue(topLevelFolder.getSubpath("file-in-top-level-folder") instanceof GitHistoricalFile);
		assertTrue(topLevelFolder.hasSubpath("second-level-folder"));
		assertTrue(topLevelFolder.getSubpath("second-level-folder") instanceof GitHistoricalFolder);
		
		GitHistoricalFolder secondLevelFolder = topLevelFolder.cd("second-level-folder");
		assertEquals(secondLevelFolder.ls().size(), 1);
		assertTrue(secondLevelFolder.hasSubpath("file-in-second-level-folder"));
		assertTrue(secondLevelFolder.getSubpath("file-in-second-level-folder") instanceof GitHistoricalFile);
	}
	
	@Test
	public void testComplicatedFolderStructureNestedFolder() throws IOException {
		
		GitHistoricalFolder folder = commitWithComplicatedFolderStructure.getFolder("top-level-folder");
		
		assertEquals(folder.ls().size(), 2);
		assertTrue(folder.hasSubpath("file-in-top-level-folder"));
		assertTrue(folder.getSubpath("file-in-top-level-folder") instanceof GitHistoricalFile);
		assertTrue(folder.hasSubpath("second-level-folder"));
		assertTrue(folder.getSubpath("second-level-folder") instanceof GitHistoricalFolder);
		
		GitHistoricalFolder secondLevelFolder = folder.cd("second-level-folder");
		assertEquals(secondLevelFolder.ls().size(), 1);
		assertTrue(secondLevelFolder.hasSubpath("file-in-second-level-folder"));
		assertTrue(secondLevelFolder.getSubpath("file-in-second-level-folder") instanceof GitHistoricalFile);
	}
	
	@Test
	public void testComplicatedFolderStructureGetFile() throws IOException {
		
		thrown.expect(IOException.class);
	    thrown.expectMessage("Navigate in git folder: the provide relativePath belongs to a file");
	    commitWithComplicatedFolderStructure.getFolder("top-level-folder/file-in-top-level-folder");
	}
	
	@Test 
	public void testComplicatedFolderStructureTopLevelFolderNotExist() throws IOException {
		
		thrown.expect(IOException.class);
	    thrown.expectMessage("Navigate in git folder: folder not exist");
	    commitWithComplicatedFolderStructure.getFolder("top-level-folder-not-exist");
	}
	
	@Test 
	public void testComplicatedFolderStructureSecondLevelFolderNotExist() throws IOException {
		
		thrown.expect(IOException.class);
	    thrown.expectMessage("Navigate in git folder: folder not exist");
	    commitWithComplicatedFolderStructure.getFolder("top-level-folder/second-level-folder-not-exist");
	}
	
	@Test
	public void testEmptyFolderStructure() throws IOException {
		
		GitHistoricalFolder folder = commitWithEmptyFolderStructure.getRoot();
		assertEquals(folder.ls().size(), 0);
	}
	
	@Test
	public void testEmptyFolderStructureFolderNotExist() throws IOException {
		
		thrown.expect(IOException.class);
	    thrown.expectMessage("Navigate in git folder: folder not exist");
	    commitWithEmptyFolderStructure.getFolder("folder-not-exist");
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
