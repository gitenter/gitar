package com.gitenter.gitar;

import java.io.File;

import lombok.Getter;

public abstract class GitHistoricalPath implements GitPath {

	@Getter
	protected final String relativePath;
	
	final GitCommit commit;

	protected GitHistoricalPath(GitCommit commit, String relativePath) {
		this.commit = commit;
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
