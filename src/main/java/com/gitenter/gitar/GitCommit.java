package com.gitenter.gitar;

import java.io.IOException;
import java.util.Date;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class GitCommit {
	
	/*
	 * "update"/"post-receive" hooks to not correspond to any commit
	 * (the one before the first commit). 
	 * 
	 * This SHA value doesn't corresponding to any concrete commit, 
	 * because it doesn't have the associated time/message/author/... 
	 */
	static final String EMPTY_SHA = "0000000000000000000000000000000000000000";
	
	private final Date time;
	private final String message;
	private final String sha;
	
	final GitRepository repository;
	final GitAuthor author;
	
	/*
	 * TODO:
	 * 
	 * Currently GitFile and GitFolder need to use it. Later on to find out
	 * a way to keep it private.
	 */
	final RevCommit jGitCommit;
	
	public Date getTime() {
		return time;
	}

	public String getMessage() {
		return message;
	}
	
	public String getSha() {
		return sha;
	}
	
	public GitAuthor getAuthor() {
		return author;
	}
	
	/*
	 * Even if RevCommit is a subclass of ObjectId, we cannot remove objectId
	 * since they are raising different errors. 
	 * 
	 * For example, For GitTag.downCasting() to decide whether the tag is 
	 * annotated or not, by using jGitCommit annotated tags will raise the
	 * same error. Here we want to clear new ObjectId.
	 */
	ObjectId getObjectId() {
		return ObjectId.fromString(sha);
	}
	
	GitCommit(GitRepository repository, String sha) throws IOException {
		
		this.sha = sha;
		try (RevWalk revWalk = new RevWalk(repository.getJGitRepository())) {
			jGitCommit = revWalk.parseCommit(getObjectId());
			
			/*
			 * JGit getCommitTime(): time, expressed as seconds since the epoch.
			 * http://download.eclipse.org/jgit/docs/jgit-2.0.0.201206130900-r/apidocs/org/eclipse/jgit/revwalk/RevCommit.html#getCommitTime()
			 * 
			 * java.sql.Timestamp(long time): Constructs a Timestamp object using a milliseconds time value.
			 * https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html
			 * 
			 * So here need to do the transformation.
			 */
			this.time = new Date(jGitCommit.getCommitTime()*1000L);
			this.message = jGitCommit.getFullMessage();
			
			this.author = new GitAuthor(this, jGitCommit.getAuthorIdent());
		}
		
		this.repository = repository;
	}
	
	GitCommit (GitRepository repository, RevCommit jGitCommit) {
		
		this.time = new Date(jGitCommit.getCommitTime()*1000L);
		this.message = jGitCommit.getFullMessage();
		this.sha = jGitCommit.getName();
		
		this.repository = repository;
		this.author = new GitAuthor(this, jGitCommit.getAuthorIdent());
		
		this.jGitCommit = jGitCommit;
	}
	
	/*
	 * TODO:
	 * To get a universal one which can automatically tell whether the relativePath
	 * is a folder or a file.
	 * > public GitPath getPath(String relativePath) throws IOException;
	 */
	public GitFile getFile(String relativePath) throws IOException {
		return new GitFile(this, relativePath);
	}
	
	public GitFolder getFolder(String relativePath) throws IOException {
		return GitFolder.create(this, relativePath);
	}
	
	public GitFolder getRoot() throws IOException {
		return getFolder(".");
	}
}
