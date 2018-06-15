package com.gitenter.gitar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitNormalBranch;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitWorkspace;

public class GitWorkspaceTest {

	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	static void add(GitWorkspace workspace, File file, String commitMessage) throws IOException, GitAPIException {
		
		if (file.isDirectory()) {
			FileUtils.copyDirectory(file, new File(workspace, file.getName()));
		}
		else {
			FileUtils.copyFile(file, new File(workspace, file.getName()));
		}
		
		workspace.add();
		workspace.commit(commitMessage);
	}
	
	static void deleteAll(GitWorkspace workspace) throws IOException, GitAPIException {
		
		for (File file : workspace.listFiles()) {
			if (file.getName().indexOf(".git") < 0) {
				workspace.remove(file.getName());
			}
		}
		workspace.commit("Delete all in workspace");
	}
	
	static void addACommit(GitWorkspace workspace, String commitMessage) throws IOException, GitAPIException {
		
		Random rand = new Random();
		String name = "file-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		new File(workspace, name).createNewFile();
		
		workspace.add();
		workspace.commit(commitMessage);
	}
	
	@Test
	public void testWorkspaceChangesBranchAfterCheckout() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneWithCommit(folder);
		GitNormalBranch master = repository.getCurrentBranch();
		GitWorkspace workspace = master.checkoutTo();
		assertEquals(workspace.getBranch().getName(), "master");
		
		repository.createBranch("a-different-branch");
		GitNormalBranch aDifferentBranch = repository.getBranch("a-different-branch");
		aDifferentBranch.checkoutTo();
		assertEquals(workspace.getBranch().getName(), "a-different-branch");
	}
	
	@Test
	public void testDifferentRepositoriesDontShareWorkspace() throws IOException, GitAPIException {
		
		GitNormalRepository repository1 = GitNormalRepositoryTest.getOneWithCommit(folder);
		GitNormalRepository repository2 = GitNormalRepositoryTest.getOneWithCommit(folder);
		
		repository1.createBranch("repository-1-branch");
		repository2.createBranch("repository-2-branch");
		
		GitWorkspace workspace1 = repository1.getBranch("repository-1-branch").checkoutTo();
		GitWorkspace workspace2 = repository2.getBranch("repository-2-branch").checkoutTo();
		
		assertNotEquals(workspace1.getBranch().getName(), workspace2.getBranch().getName());
	}

}
