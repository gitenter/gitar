package com.gitenter.gitar.setup;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitRemote;

public class GitBareRepositorySetup {

	private static File getRepositoryDirectory(File tmpFolder) throws IOException {
		
		Random rand = new Random();
		String name = "repo-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		File repositoryDirectory = new File(tmpFolder, name+".git");
		repositoryDirectory.mkdir();
		
		return repositoryDirectory;
	}
	
	public static GitBareRepository getOneJustInitialized(File tmpFolder) throws IOException, GitAPIException {
		
		File repositoryDirectory = getRepositoryDirectory(tmpFolder);
		return GitBareRepository.getInstance(repositoryDirectory);
	}

	public static GitBareRepository getOneWithCommit(File tmpFolder) throws IOException, GitAPIException {
		
		GitBareRepository repository = getOneJustInitialized(tmpFolder);
		
		GitNormalRepository localRepository = GitNormalRepositorySetup.getOneWithCommit(tmpFolder);
		localRepository.createOrUpdateRemote("origin", repository.getDirectory().toString());
		GitRemote origin = localRepository.getRemote("origin");
		localRepository.getCurrentBranch().checkoutTo().push(origin);
		
		return repository;
	}
	
	public static File getOneFolderStructureOnly(File tmpFolder) throws IOException, GitAPIException {
	
		File repositoryDirectory = getRepositoryDirectory(tmpFolder);
		
		Git.init().setDirectory(repositoryDirectory).setBare(true).call();
		assertTrue(new File(repositoryDirectory, "branches").isDirectory());
		
		return repositoryDirectory;
	}
}
