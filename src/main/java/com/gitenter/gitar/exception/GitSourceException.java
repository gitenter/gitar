package com.gitenter.gitar.exception;

import java.io.IOException;

public abstract class GitSourceException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public GitSourceException(String message) {
		super(message);
	}
}
