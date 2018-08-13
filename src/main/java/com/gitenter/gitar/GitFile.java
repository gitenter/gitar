package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

public interface GitFile extends GitPath {

	public byte[] getBlobContent() throws IOException, GitAPIException;
	public String getMimeType () throws IOException, GitAPIException;
}
