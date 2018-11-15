package com.gitenter.gitar.setup;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitNormalBranch;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitWorkspace;

public class GitNormalRepositorySetup {
	
	private static File getDistinguishableDirectory(TemporaryFolder folder) throws IOException {
		
		/*
		 * Although temporary folders on different test will not mix together,
		 * there is possibility that one test will need to initialize multiple
		 * repositories (e.g. GitWorkspaceTest.testDifferentRepositoriesDontShareWorkspace),
		 * so we use this random number to make sure folders are not crashing.
		 */
		Random rand = new Random();
		String name = "repo-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		return folder.newFolder(name);
	}
	
	public static GitNormalRepository getOneJustInitialized(TemporaryFolder folder) throws IOException, GitAPIException {
		
		File directory = getDistinguishableDirectory(folder);
		return GitNormalRepository.getInstance(directory);
	}
	
	public static GitNormalRepository getOneWithCommit(TemporaryFolder folder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = getOneJustInitialized(folder);
		
		GitNormalBranch master = repository.getCurrentBranch();
		GitWorkspace workspace = master.checkoutTo();
		GitWorkspaceSetup.addACommit(workspace, "First commit message");
		
		return repository;
	}
	
	public static GitNormalRepository getOneWithCleanWorkspace(TemporaryFolder folder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file = folder.newFile("only-file-on-root");
		file.createNewFile();
		GitWorkspaceSetup.add(workspace, file, "Add file");
		
		GitWorkspaceSetup.deleteAll(workspace);
		
		return repository;
	}
	
	public static File getOneFolderStructureOnly(TemporaryFolder folder) throws IOException, GitAPIException {
		
		File directory = getDistinguishableDirectory(folder);
		
		Git.init().setDirectory(directory).call();
		assertTrue(new File(directory, ".git").isDirectory());
		
		return directory;
	}
	
	/*
	 * This classes are only used by Git*FolderTest. We use static method rather than
	 * test case class inheritance to reduce confusing while eliminating duplicated code.
	 */
	public static GitNormalRepository getOneWithFileOnRoot(TemporaryFolder folder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file;
		
		file = folder.newFile("file-on-root-1");
		file.createNewFile();
		GitWorkspaceSetup.add(workspace, file, "Add file-1");
		
		file = folder.newFile("file-on-root-2");
		file.createNewFile();
		GitWorkspaceSetup.add(workspace, file, "Add file-2");
		
		return repository;
	}
	
	public static GitNormalRepository getOneWithComplicatedFolderStructure(TemporaryFolder folder) throws IOException, GitAPIException {

		GitNormalRepository repository = getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		/*
		 * Need complicated prefix of 1,2,3 because the iteration depends
		 * on the order of the file/folders.
		 */
		File file = folder.newFile("1-file-on-root-along-with-folders");
		file.createNewFile();
		GitWorkspaceSetup.add(workspace, file, "Add file");

		File topLevelFolder = folder.newFolder("2-top-level-folder");
		new File(topLevelFolder, "1-file-in-top-level-folder").createNewFile();
		new File(topLevelFolder, "2-second-level-folder").mkdir();
		new File(new File(topLevelFolder, "2-second-level-folder"), "file-in-second-level-folder").createNewFile();
		new File(topLevelFolder, "3-file-in-top-level-folder").createNewFile();
		GitWorkspaceSetup.add(workspace, topLevelFolder, "Add folder structure");
		
		file = folder.newFile("3-file-on-root-along-with-folders");
		file.createNewFile();
		GitWorkspaceSetup.add(workspace, file, "Add file");
		
		return repository;
	}
}
