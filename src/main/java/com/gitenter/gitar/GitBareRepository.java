package com.gitenter.gitar;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitBareRepository extends GitRepository {
	
	private Repository jGitRepository;
	
	/*
	 * TODO:
	 * Further wrap JGit exceptions.
	 */
	private GitBareRepository(File directory) throws IOException, GitAPIException {
		super(directory);
		
		if (isNormalRepository()) {
			/*
			 * TODO:
			 * Consider throw a customized exception.
			 */
			throw new IOException("The provided directory is a normal git directory: "+directory);
		}
		else if (!isBareRepository()) {
			Git.init().setDirectory(directory).setBare(true).call();
		}
		
		buildJGitRepository();
	}
	
	public static GitBareRepository getInstance(File directory) throws IOException, GitAPIException {
		
		if (instances.containsKey(directory)) {
			GitRepository repository = instances.get(directory);
			if (repository instanceof GitBareRepository) {
				return (GitBareRepository)repository;
			}
			else {
				/*
				 * TODO:
				 * Consider throw a customized exception.
				 */
				throw new IOException("The provided directory is a normal git directory: "+directory);
			}
		}
		else {
			GitBareRepository repository = new GitBareRepository(directory);
			instances.put(directory, repository);
			return repository;
		}
	}
	
	private void buildJGitRepository() throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		jGitRepository = builder.setGitDir(directory).readEnvironment().findGitDir().build();
	}
	
	@Override
	Git getJGitGit() {
		return new Git(jGitRepository);
	}
	
	@Override
	Repository getJGitRepository() {
		return jGitRepository;
	}
	
	@Override
	protected File getHooksDirectory() {
		return new File(directory, "hooks");
	}
}
