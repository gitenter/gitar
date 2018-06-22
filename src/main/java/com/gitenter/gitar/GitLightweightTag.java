package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;

public class GitLightweightTag extends GitTag {

	GitLightweightTag(GitTag tag) throws IOException, GitAPIException {
		super(tag.repository, tag.name, tag.objectId);
	}
}
