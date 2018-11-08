package com.gitenter.gitar;

public class GitFileDelete extends GitFileDiff {

	GitFileDelete(GitFileDiff fileDiff, GitRepository repository) {
		super(fileDiff.jGitDiffEntry, repository);
	}

	public String getOldPath() {
		return jGitDiffEntry.getOldPath();
	}
}
