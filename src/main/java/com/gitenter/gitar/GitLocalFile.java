package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

public class GitLocalFile extends GitLocalPath implements GitFile {

	protected GitLocalFile(GitWorkspace workspace, String relativePath) {
		super(workspace, relativePath);
	}
	
	@Override
	public byte[] getBlobContent() throws IOException, GitAPIException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMimeType() throws IOException, GitAPIException {
		// TODO Auto-generated method stub
		return null;
	}

}
