package com.gitenter.gitar;

public class GitFileCopy extends GitFileDiff {

	GitFileCopy(GitFileDiff fileDiff, GitRepository repository) {
		super(fileDiff.jGitDiffEntry, repository);
	}
	
	public String getOldPath() {
		return jGitDiffEntry.getOldPath();
	}
	
	public String getNewPath() {
		return jGitDiffEntry.getNewPath();
	}
}
