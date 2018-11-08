package com.gitenter.gitar;

public class GitFileDelete extends GitFileDiff {

	GitFileDelete(GitFileDiff fileDiff) {
		super(fileDiff.jGitDiffEntry);
	}

	public String getOriginalPath() {
		return jGitDiffEntry.getOldPath();
	}
}
