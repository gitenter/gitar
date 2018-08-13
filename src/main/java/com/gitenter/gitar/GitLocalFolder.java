package com.gitenter.gitar;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class GitLocalFolder extends GitLocalPath implements GitFolder {

	private static final long serialVersionUID = 1L;

	GitLocalFolder(GitWorkspace workspace, String relativePath) throws FileNotFoundException {
		super(workspace, relativePath);
		if (!exists()) {
			throw new FileNotFoundException(String.format("Local folder path %s not exist", relativePath));
		}
		if (isFile()) {
			throw new FileNotFoundException(String.format("Local folder path %s belongs to a file", relativePath));
		}
	}
	
	@Override
	public boolean hasSubpath(String name) {
		for (String subpathName : list()) {
			if (subpathName.equals(name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public GitLocalPath getSubpath(String name) {
		
		File file = new File(this, name);
		try {
			if (file.isDirectory()) {
				return new GitLocalFolder(this.workspace, Paths.get(relativePath, name).toString());
			}
			else {
				assert file.isFile();
				return new GitLocalFile(this.workspace, Paths.get(relativePath, name).toString());
			}
		}
		catch (FileNotFoundException e) {
			/*
			 * Exception is raised if the file/folder type doesn't match. It cannot happen in
			 * here so this is a dead end code which will never happen.
			 * 
			 * Therefore, we can swallow the exception and return null.
			 */
			return null;
		}
	}

	@Override
	public GitLocalFolder cd(String name) throws FileNotFoundException {
		return new GitLocalFolder(this.workspace, Paths.get(relativePath, name).toString());
	}

	@Override
	public GitLocalFile getFile(String name) throws FileNotFoundException {
		return new GitLocalFile(this.workspace, Paths.get(relativePath, name).toString());
	}

	@Override
	public Collection<? extends GitPath> ls() {
		Collection<GitLocalPath> subpaths = new ArrayList<GitLocalPath>();
		for (String subpathName : list()) {
			if (!subpathName.equals(".git")) {
				subpaths.add(getSubpath(subpathName));
			}
		}
		return subpaths;
	}
}
