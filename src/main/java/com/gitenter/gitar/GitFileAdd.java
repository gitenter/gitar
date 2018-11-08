package com.gitenter.gitar;

public class GitFileAdd extends GitFileDiff {

	GitFileAdd(GitFileDiff fileDiff, GitRepository repository) {
		super(fileDiff.jGitDiffEntry, repository);
	}

	public String getNewPath() {
		return jGitDiffEntry.getNewPath();
	}
}
