package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.setup.GitNormalRepositorySetup;
import com.gitenter.gitar.setup.GitWorkspaceSetup;

public class GitFileDiffTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testBranchDiff() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCommit(folder);
		GitNormalBranch master = repository.getBranch("master");
		repository.createBranch("another-branch");
		GitNormalBranch anotherBranch = repository.getBranch("another-branch");
		
		String sharedFilePath = "shared-file";
		File sharedFile = folder.newFile(sharedFilePath);
		sharedFile.createNewFile();
		
		String inMasterFilePath = "in-master-file";
		File inMasterFile = folder.newFile(inMasterFilePath);
		inMasterFile.createNewFile();
		
		String inAnotherBranchFilePath = "in-another-branch-file";
		File inAnotherBranchFile = folder.newFile(inAnotherBranchFilePath);
		inAnotherBranchFile.createNewFile();
		FileWriter writer;
		
		GitWorkspace workspace = master.checkoutTo();
		writer = new FileWriter(sharedFile);
		writer.write("Same first line\nIn master content\nSame third line");
		writer.close();
		GitWorkspaceSetup.add(workspace, sharedFile, "Add shared-file");
		
		workspace = anotherBranch.checkoutTo();
		writer = new FileWriter(sharedFile); // So FileWriter truncate the data rather than append it.
		writer.flush();
		writer.write("Same first line\nIn another-branch content\nSame third line");
		writer.close();
		GitWorkspaceSetup.add(workspace, sharedFile, "Add shared-file");
		
		List<GitFileDiff> fileDiffs = master.diff(anotherBranch);
		assertEquals(fileDiffs.size(), 1);
		assertTrue(fileDiffs.get(0) instanceof GitFileModify);
		GitFileModify fileModify = (GitFileModify)fileDiffs.get(0);
		assertEquals(fileModify.getPath(), sharedFilePath);
		assertEquals(fileModify.getOriginalPermissionString(), "100644");
		
		workspace = master.checkoutTo();
		writer = new FileWriter(inMasterFile);
		writer.write("In master file content");
		writer.close();
		GitWorkspaceSetup.add(workspace, inMasterFile, "Add in-master-file");
		
		workspace = anotherBranch.checkoutTo();
		writer = new FileWriter(inAnotherBranchFile);
		writer.write("In another-branch file content");
		writer.close();
		GitWorkspaceSetup.add(workspace, inAnotherBranchFile, "Add in-another-branch-file");
		
		fileDiffs = master.diff(anotherBranch);
		assertEquals(fileDiffs.size(), 3);
		for (GitFileDiff fileDiff : fileDiffs) {
			if (fileDiff instanceof GitFileModify) {
				fileModify = (GitFileModify)fileDiff;
				assertEquals(fileModify.getPath(), sharedFilePath);
			}
			else if (fileDiff instanceof GitFileDelete) {
				GitFileDelete fileDelete = (GitFileDelete)fileDiff;
				assertEquals(fileDelete.getOriginalPath(), inAnotherBranchFilePath);
			}
			else if (fileDiff instanceof GitFileAdd) {
				GitFileAdd fileAdd = (GitFileAdd)fileDiff;
				assertEquals(fileAdd.getNewPath(), inMasterFilePath);
			}
		}

	}

}
