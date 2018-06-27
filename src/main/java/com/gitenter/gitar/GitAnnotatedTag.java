package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevTag;

import lombok.Getter;

public class GitAnnotatedTag extends GitTag {
	
	@Getter
	private final String message;

	GitAnnotatedTag(GitTag tag, RevTag jGitTag) throws IOException, GitAPIException {
		super(tag.repository, tag.name, tag.objectId);
		this.message = jGitTag.getFullMessage();
	}

}
