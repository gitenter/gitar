package com.gitenter.gitar;

import java.io.FileNotFoundException;
import java.io.IOException;

public class GitFilepath extends GitPath {

	protected GitFilepath(GitCommit commit, String relativePath) {
		super(commit, relativePath);
	}
	
	GitFile downCasting() throws FileNotFoundException, IOException {
		return new GitFile(this.commit, this.getRelativePath());
	}
}
