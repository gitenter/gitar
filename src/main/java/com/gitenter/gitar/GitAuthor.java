package com.gitenter.gitar;

import org.eclipse.jgit.lib.PersonIdent;

public class GitAuthor {

	private final String name;
	private final String emailAddress;
	
	final GitCommit commit;
	
	public String getName() {
		return name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	GitAuthor(GitCommit commit, PersonIdent jGitPersonIdent) {

		this.name = jGitPersonIdent.getName();
		this.emailAddress = jGitPersonIdent.getEmailAddress();
		
		this.commit = commit;
	}	
}
