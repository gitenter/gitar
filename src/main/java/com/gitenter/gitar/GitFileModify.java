package com.gitenter.gitar;

import java.io.FilePermission;

public class GitFileModify extends GitFileDiff {

	GitFileModify(GitFileDiff fileDiff) {
		super(fileDiff.jGitDiffEntry);
	}

	public String getPath() {
		String oldPath = jGitDiffEntry.getOldPath();
		String newPath = jGitDiffEntry.getNewPath();
		
		assert oldPath.equals(newPath);
		return oldPath;
	}
}
