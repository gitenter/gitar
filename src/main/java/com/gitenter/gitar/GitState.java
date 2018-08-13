package com.gitenter.gitar;

import java.io.IOException;

public interface GitState {

	/*
	 * TODO:
	 * To get a universal one which can automatically tell whether the relativePath
	 * is a folder or a file.
	 * > public GitPath getPath(String relativePath) throws IOException;
	 */
	public GitFile getFile(String relativePath) throws IOException;
	public GitFolder getFolder(String relativePath) throws IOException;
	public GitFolder getRoot() throws IOException;
}
