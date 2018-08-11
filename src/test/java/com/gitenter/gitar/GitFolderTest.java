package com.gitenter.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class GitFolderTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	@Rule public ExpectedException thrown = ExpectedException.none();
	
	protected GitNormalRepository repository;
	
	@Before 
	public void setupFileOnRoot() throws IOException, GitAPIException {
	
		repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file;
		
		file = folder.newFile("file-1");
		file.createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add file-1");
		
		file = folder.newFile("file-2");
		file.createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add file-2");
	}
	
	@Before 
	public void setupComplicatedFolderStructure() throws IOException, GitAPIException {
		
		repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file;

		file = folder.newFolder("top-level-folder");
		new File(file, "file-in-top-level-folder").createNewFile();
		new File(file, "second-level-folder").mkdir();
		new File(new File(file, "second-level-folder"), "file-in-second-level-folder").createNewFile();
		GitWorkspaceTest.add(workspace, file, "Add folder structure");
	}
	
	@Before
	public void setupEmptyFolderStructure() throws IOException, GitAPIException {
		repository = GitNormalRepositoryTest.getOneWithCleanWorkspace(folder);	
	}
}
