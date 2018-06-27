package com.gitenter.gitar;

import org.eclipse.jgit.lib.PersonIdent;

import lombok.Getter;

public class GitAuthor {

	@Getter
	private final String name;
	
	@Getter
	private final String emailAddress;
	
	final GitCommit commit;

	GitAuthor(GitCommit commit, PersonIdent jGitPersonIdent) {

		this.name = jGitPersonIdent.getName();
		this.emailAddress = jGitPersonIdent.getEmailAddress();
		
		this.commit = commit;
	}	
}
