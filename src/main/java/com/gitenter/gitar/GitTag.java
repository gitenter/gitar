package com.gitenter.gitar;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;

import com.gitenter.gitar.util.GitPlaceholder;
import com.gitenter.gitar.util.GitProxyPlaceholder;

import lombok.Getter;

public class GitTag {

	@Getter
	protected final String name;
	
	protected final ObjectId objectId;
	protected final GitRepository repository;
	
	private CommitPlaceholder commitPlaceholder = new ProxyCommitPlaceholder();
	
	public GitCommit getCommit() throws IOException, GitAPIException {
		return commitPlaceholder.get();
	}
	
	/*
	 * The alternative choice is directly have "CommitBean" instance variable
	 * and set it as null. When "get()", check if it exists and if null, calculate
	 * its value. It should be quite save outside of this class, but in class the
	 * instance variable may be accidentally accessed. I am doing this to ensure that 
	 * it will not happen even internally.
	 */
	private interface CommitPlaceholder extends GitPlaceholder<GitCommit> {
		public GitCommit get() throws IOException, GitAPIException;
	}
	
	private class ProxyCommitPlaceholder extends GitProxyPlaceholder<GitCommit> implements CommitPlaceholder {
		
		@Override
		public GitCommit getReal() throws IOException, GitAPIException {
			return repository.getCommit(objectId.getName());
		}
	}

	GitTag(GitRepository repository, String name, ObjectId objectId) throws IOException {
		
		this.name = name;
		this.objectId = objectId;
		this.repository = repository;	
	}
	
	GitTag downCasting() throws IOException, GitAPIException {
		
		try (RevWalk revWalk = new RevWalk(repository.getJGitRepository())) {
			RevTag jGitTag = revWalk.parseTag(objectId);
			return new GitAnnotatedTag(this, jGitTag);
		}
		catch(IncorrectObjectTypeException notAnAnnotatedTag) {
			return new GitLightweightTag(this);
		}
	}
}
