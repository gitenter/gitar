package com.gitenter.gitar.util;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

public interface GitPlaceholder<T> {
	public T get() throws IOException, GitAPIException;
}
