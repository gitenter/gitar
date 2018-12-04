package com.gitenter.gitar.exception;

import java.io.File;

/**
 * Git directory should be either of the following cases based on
 * <a href="https://git-scm.com/docs/gitrepository-layout">gitrepository-layout</a>:
 * <p>
 * (1) A normal repository with a <code>.git</code> directory at the root of the working tree.<br>
 * (2) A bare repository for which the root has name <code>project-name.git</code>.
 * <p>
 * This exception will be raised when type mismatch happens.
 *
 */
public class WrongGitDirectoryTypeException extends GitSourceException {

	private static final long serialVersionUID = 1L;

	public WrongGitDirectoryTypeException(File directory, String type) {
		/*
		 * TODO:
		 * Change `type` to a enum. It seems a inner enum cannot be referred outside
		 * as `WrongGitDirectoryTypeException.GitRepositoryType`...
		 */
		super("The provided directory is a not a "+type+" git directory: "+directory);
	}
}
