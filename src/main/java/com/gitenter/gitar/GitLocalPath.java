package com.gitenter.gitar;

import java.io.File;

import lombok.Getter;

public abstract class GitLocalPath implements GitPath {

	@Getter
	protected final String relativePath;
	
	final GitWorkspace workspace;

	protected GitLocalPath(GitWorkspace workspace, String relativePath) {
		this.workspace = workspace;
		this.relativePath = relativePath;
	}
	
	@Override
	public String getName() {
		/*
		 * Since what is provided is relative path rather than absolute
		 * path (but Java File is for absolute path), I don't know if
		 * there is a better way to do it.
		 */
		return new File(relativePath).getName();
	}
}
