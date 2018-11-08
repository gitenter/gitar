package com.gitenter.gitar;

public class GitFileAdd extends GitFileDiff {

	GitFileAdd(GitFileDiff fileDiff) {
		super(fileDiff.jGitDiffEntry);
	}

	public String getNewPath() {
		return jGitDiffEntry.getNewPath();
	}
}
