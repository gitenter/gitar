package com.gitenter.gitar;

public class GitFileCopy extends GitFileDiff {

	GitFileCopy(GitFileDiff fileDiff) {
		super(fileDiff.jGitDiffEntry);
	}
	
	public String getOriginalPath() {
		return jGitDiffEntry.getOldPath();
	}
	
	public String getNewPath() {
		return jGitDiffEntry.getNewPath();
	}
}
