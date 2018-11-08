package com.gitenter.gitar;

import org.eclipse.jgit.diff.DiffEntry;

class GitFileDiff {
	
	protected DiffEntry jGitDiffEntry;
	
	GitFileDiff (DiffEntry jGitDiffEntry) {
		this.jGitDiffEntry = jGitDiffEntry;
	}
	
	GitFileDiff downCasting() {
		
		switch (jGitDiffEntry.getChangeType()) {
		case ADD:
			return new GitFileAdd(this);
		case COPY:
			return new GitFileCopy(this);
		case DELETE:
			return new GitFileDelete(this);
		case MODIFY:
			return new GitFileModify(this);
		case RENAME:
			return new GitFileRename(this);
		default:
			return null;
		}
	}
	
	/*
	 * TODO:
	 * Consider return type of e.g. `java.io.FilePermission`
	 */
	public int getOriginalPermission() {
		/*
		 * TODO:
		 * Check if for modification the old and new path need to have the same
		 * permission. Otherwise only provide one method.
		 */
		return jGitDiffEntry.getOldMode().getBits();
	}
	
	public String getOriginalPermissionString() {
		return Integer.toOctalString(getOriginalPermission());
	}
	
	public int getNewPermission() {
		return jGitDiffEntry.getNewMode().getBits();
	}
	
	public String getNewPermissionString() {
		return Integer.toOctalString(getOriginalPermission());
	}
}
