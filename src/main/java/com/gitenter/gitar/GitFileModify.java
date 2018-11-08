package com.gitenter.gitar;

public class GitFileModify extends GitFileDiff {

	GitFileModify(GitFileDiff fileDiff, GitRepository repository) {
		super(fileDiff.jGitDiffEntry, repository);
	}

	public String getPath() {
		String oldPath = jGitDiffEntry.getOldPath();
		String newPath = jGitDiffEntry.getNewPath();
		
		assert oldPath.equals(newPath);
		return oldPath;
	}
}
