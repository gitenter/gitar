package com.gitenter.gitar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

public class GitLocalFolder extends GitLocalPath implements GitFolder {

	private GitLocalFolder(GitWorkspace workspace, String relativePath) {
		super(workspace, relativePath);
		// TODO Auto-generated constructor stub
	}

	@Override
	public GitPath getSubpath(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GitFolder cd(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GitFile getFile(String name) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends GitPath> list() {
		// TODO Auto-generated method stub
		return null;
	}

}
