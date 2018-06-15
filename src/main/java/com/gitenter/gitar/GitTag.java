package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitTag {

	protected final String name;
	
	final GitCommit commit;
	
	public String getName() {
		return name;
	}

	GitTag(GitRepository repository, String name) throws IOException {
		
		this.name = name;
		
		this.commit = repository.getCommit(repository.getJGitRepository().exactRef("refs/tags/"+name).getObjectId().getName());
	}
	
	GitTag downCasting() throws IOException {
		
		try (RevWalk revWalk = new RevWalk(commit.repository.getJGitRepository())) {
			RevTag jGitTag = revWalk.parseTag(commit.getObjectId());
			return new GitAnnotatedTag(this, jGitTag);
		}
		catch(IncorrectObjectTypeException notAnAnnotatedTag) {
			return new GitLightweightTag(this);
		}
	}
	
	public GitCommit getCommit() {
		return commit;
	}
}
