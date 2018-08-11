package com.gitenter.gitar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import com.gitenter.gitar.util.MimeTypeGuesser;

public class GitLocalFile extends GitLocalPath implements GitFile {

	private static final long serialVersionUID = 1L;

	GitLocalFile(GitWorkspace workspace, String relativePath) throws FileNotFoundException {
		super(workspace, relativePath);
		if (!this.exists()) {
			throw new FileNotFoundException("Navigate in local file: file not exist");
		}
		if (this.isDirectory()) {
			throw new FileNotFoundException("Navigate in local file: the provide relativePath belongs to a folder");
		}
	}
	
	@Override
	public byte[] getBlobContent() throws IOException {
		/*
		 * Here we are not using the proxy pattern, but to reload the data again
		 * every time, is because unlike historical commit, file may change in the
		 * current workspace.
		 */
		File file = new File(workspace, relativePath);
		byte[] blobContent = Files.readAllBytes(file.toPath());
		return blobContent;
	}

	@Override
	public String getMimeType() throws IOException {
		
		return MimeTypeGuesser.guess(getName(), getBlobContent());
	}

}
