package com.gitenter.gitar;

import java.io.IOException;

public class GitLightweightTag extends GitTag {

	GitLightweightTag(GitTag tag) throws IOException {
		super(tag.commit.repository, tag.name);
	}
}
