package com.gitenter.gitar;

import java.io.File;

public abstract class GitPath {

	protected final String relativePath;
	final GitCommit commit;
	
	public String getRelativePath() {
		return relativePath;
	}
	
	public String getName() {
		/*
		 * Since what is provided is relative path rather than absolute
		 * path (but Java File is for absolute path), I don't know if
		 * there is a better way to do it.
		 */
		return new File(relativePath).getName();
	}

	protected GitPath(GitCommit commit, String relativePath) {
		this.commit = commit;
		this.relativePath = relativePath;
	}
}
