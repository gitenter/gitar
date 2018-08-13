package com.gitenter.gitar.setup;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.gitar.GitWorkspace;

public class GitWorkspaceSetup {

	public static void add(GitWorkspace workspace, File file, String commitMessage) throws IOException, GitAPIException {
		
		if (file.isDirectory()) {
			FileUtils.copyDirectory(file, new File(workspace, file.getName()));
		}
		else {
			FileUtils.copyFile(file, new File(workspace, file.getName()));
		}
		
		workspace.add();
		workspace.commit(commitMessage);
	}
	
	public static void deleteAll(GitWorkspace workspace) throws IOException, GitAPIException {
		
		for (File file : workspace.listFiles()) {
			if (file.getName().indexOf(".git") < 0) {
				workspace.remove(file.getName());
			}
		}
		workspace.commit("Delete all in workspace");
	}
	
	public static void addACommit(GitWorkspace workspace, String commitMessage) throws IOException, GitAPIException {
		
		Random rand = new Random();
		String name = "file-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		new File(workspace, name).createNewFile();
		
		workspace.add();
		workspace.commit(commitMessage);
	}
}
