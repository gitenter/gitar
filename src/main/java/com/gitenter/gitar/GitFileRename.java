package com.gitenter.gitar;

public class GitFileRename extends GitFileDiff {

	GitFileRename(GitFileDiff fileDiff) {
		super(fileDiff.jGitDiffEntry);
	}

	public String getOriginalPath() {
		return jGitDiffEntry.getOldPath();
	}
	
	public String getNewPath() {
		return jGitDiffEntry.getNewPath();
	}
}
