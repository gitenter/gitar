package com.gitenter.gitar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;

class GitFileDiff {

	private GitRepository repository;
	
	protected DiffEntry jGitDiffEntry;
	
	GitFileDiff (DiffEntry jGitDiffEntry, GitRepository repository) {
		this.jGitDiffEntry = jGitDiffEntry;
		this.repository = repository;
	}
	
	GitFileDiff downCasting() {
		
		switch (jGitDiffEntry.getChangeType()) {
		case ADD:
			return new GitFileAdd(this, repository);
		case COPY:
			return new GitFileCopy(this, repository);
		case DELETE:
			return new GitFileDelete(this, repository);
		case MODIFY:
			return new GitFileModify(this, repository);
		case RENAME:
			return new GitFileRename(this, repository);
		default:
			return null;
		}
	}
	
	/*
	 * TODO:
	 * Consider return type of e.g. `java.io.FilePermission`
	 * 
	 * TODO:
	 * Consider only have `get*File()` which lazily load the content 
	 * related part.
	 */
	public int getOldPermission() {
		/*
		 * TODO:
		 * Check if for modification the old and new path need to have the same
		 * permission. Otherwise only provide one method.
		 */
		return jGitDiffEntry.getOldMode().getBits();
	}
	
	public String getOldPermissionString() {
		return Integer.toOctalString(getOldPermission());
	}
	
	public int getNewPermission() {
		return jGitDiffEntry.getNewMode().getBits();
	}
	
	public String getNewPermissionString() {
		return Integer.toOctalString(getNewPermission());
	}
	
	/*
	 * TODO:
	 * 
	 * Consider change the output to a more user friendly format.
	 * Say, at least parse the return `String` so it can have meaning.
	 * Then have a subclass of `GitFileWithModification extends GitFile`
	 * which add the changes onto the file.
	 */
	public String getDiffContent() throws IOException {
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try (DiffFormatter formatter = new DiffFormatter(bout)) {
			formatter.setRepository(repository.getJGitRepository());
			formatter.format(jGitDiffEntry);
		}
		
		return bout.toString();
	}
}
