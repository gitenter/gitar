package com.gitenter.gitar.setup;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitBareRepository;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitRemote;

public class GitBareRepositorySetup {

	private static File getDirectory(TemporaryFolder folder) throws IOException {
		
		Random rand = new Random();
		String name = "repo-"+String.valueOf(rand.nextInt(Integer.MAX_VALUE));
		
		return folder.newFolder(name+".git");
	}
	
	public static GitBareRepository getOneJustInitialized(TemporaryFolder folder) throws IOException, GitAPIException {
		
		File directory = getDirectory(folder);
		return GitBareRepository.getInstance(directory);
	}

	public static GitBareRepository getOneWithCommit(TemporaryFolder folder) throws IOException, GitAPIException {
		
		GitBareRepository repository = getOneJustInitialized(folder);
		
		GitNormalRepository localRepository = GitNormalRepositorySetup.getOneWithCommit(folder);
		localRepository.createOrUpdateRemote("origin", repository.getDirectory().toString());
		GitRemote origin = localRepository.getRemote("origin");
		localRepository.getCurrentBranch().checkoutTo().push(origin);
		
		return repository;
	}
	
	public static File getOneFolderStructureOnly(TemporaryFolder folder) throws IOException, GitAPIException {
	
		File directory = getDirectory(folder);
		
		Git.init().setDirectory(directory).setBare(true).call();
		assertTrue(new File(directory, "branches").isDirectory());
		
		return directory;
	}
}
