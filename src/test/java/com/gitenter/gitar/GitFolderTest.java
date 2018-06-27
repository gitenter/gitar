package com.gitenter.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitFile;
import com.gitenter.gitar.GitFolder;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitWorkspace;

public class GitFolderTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	@Rule public ExpectedException thrown = ExpectedException.none();
	
	private GitCommit commitWithFileOnRoot;
	private GitCommit commitWithComplicatedFolderStructure;
	private GitCommit commitWithEmptyFolderStructure;
	
	@Before 
	public void setupFileOnRoot() throws IOException, GitAPIException {
	
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file;
		
		file = folder.newFile("file-1");
		file.createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add file-1");
		
		file = folder.newFile("file-2");
		file.createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add file-2");
		
		commitWithFileOnRoot = repository.getCurrentBranch().getHead();
	}
	
	@Before 
	public void setupComplicatedFolderStructure() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file;

		file = folder.newFolder("top-level-folder");
		new File(file, "file-in-top-level-folder").createNewFile();
		new File(file, "second-level-folder").mkdir();
		new File(new File(file, "second-level-folder"), "file-in-second-level-folder").createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add folder structure");
		
		commitWithComplicatedFolderStructure = repository.getCurrentBranch().getHead();
	}
	
	@Before
	public void setupEmptyFolderStructure() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneWithCleanWorkspace(folder);	
		commitWithEmptyFolderStructure = repository.getCurrentBranch().getHead();
	}
	
	@Test
	public void testFilesOnRoot() throws IOException, GitAPIException {
		
		GitFolder folder = commitWithFileOnRoot.getRoot();
		
		assertEquals(folder.list().size(), 2);
		assertTrue(folder.hasSubpath("file-1"));
		assertTrue(folder.getSubpath("file-1") instanceof GitFile);
		assertTrue(folder.hasSubpath("file-2"));
		assertTrue(folder.getSubpath("file-2") instanceof GitFile);
	}
	
	@Test
	public void testComplicatedFolderStructureOnRoot() throws IOException {	
		
		GitFolder folder = commitWithComplicatedFolderStructure.getRoot();
		
		assertEquals(folder.list().size(), 1);
		assertTrue(folder.hasSubpath("top-level-folder"));
		assertTrue(folder.getSubpath("top-level-folder") instanceof GitFolder);
		
		GitFolder topLevelFolder = folder.cd("top-level-folder");
		assertEquals(topLevelFolder.list().size(), 2);
		assertTrue(topLevelFolder.hasSubpath("file-in-top-level-folder"));
		assertTrue(topLevelFolder.getSubpath("file-in-top-level-folder") instanceof GitFile);
		assertTrue(topLevelFolder.hasSubpath("second-level-folder"));
		assertTrue(topLevelFolder.getSubpath("second-level-folder") instanceof GitFolder);
		
		GitFolder secondLevelFolder = topLevelFolder.cd("second-level-folder");
		assertEquals(secondLevelFolder.list().size(), 1);
		assertTrue(secondLevelFolder.hasSubpath("file-in-second-level-folder"));
		assertTrue(secondLevelFolder.getSubpath("file-in-second-level-folder") instanceof GitFile);
	}
	
	@Test
	public void testComplicatedFolderStructureNestedFolder() throws IOException {
		
		GitFolder folder = commitWithComplicatedFolderStructure.getFolder("top-level-folder");
		
		assertEquals(folder.list().size(), 2);
		assertTrue(folder.hasSubpath("file-in-top-level-folder"));
		assertTrue(folder.getSubpath("file-in-top-level-folder") instanceof GitFile);
		assertTrue(folder.hasSubpath("second-level-folder"));
		assertTrue(folder.getSubpath("second-level-folder") instanceof GitFolder);
		
		GitFolder secondLevelFolder = folder.cd("second-level-folder");
		assertEquals(secondLevelFolder.list().size(), 1);
		assertTrue(secondLevelFolder.hasSubpath("file-in-second-level-folder"));
		assertTrue(secondLevelFolder.getSubpath("file-in-second-level-folder") instanceof GitFile);
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
		
		GitFolder folder = commitWithEmptyFolderStructure.getRoot();
		assertEquals(folder.list().size(), 0);
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
