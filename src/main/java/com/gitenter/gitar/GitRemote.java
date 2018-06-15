package com.gitenter.gitar;

public class GitRemote {

	final String name;
	final String url;

	final GitNormalRepository repository;
	
	public GitRemote(GitNormalRepository repository, String name, String url) {
		this.repository = repository;
		this.name = name;
		this.url = url;
	}
	
	
}
