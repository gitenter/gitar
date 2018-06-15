package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevTag;

public class GitAnnotatedTag extends GitTag {
	
	private final String message;
	
	public String getMessage() {
		return message;
	}

	GitAnnotatedTag(GitTag tag, RevTag jGitTag) throws IOException {
		super(tag.commit.repository, tag.name);
		this.message = jGitTag.getFullMessage();
	}

}
