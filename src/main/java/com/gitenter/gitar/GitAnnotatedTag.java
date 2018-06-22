package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevTag;

public class GitAnnotatedTag extends GitTag {
	
	private final String message;
	
	public String getMessage() {
		return message;
	}

	GitAnnotatedTag(GitTag tag, RevTag jGitTag) throws IOException, GitAPIException {
		super(tag.repository, tag.name, tag.objectId);
		this.message = jGitTag.getFullMessage();
	}

}
