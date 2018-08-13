package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitAnnotatedTag;
import com.gitenter.gitar.GitLightweightTag;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitTagTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testTagNotExist() throws IOException, GitAPIException {
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(folder);
		assertEquals(repository.getTag("tag-not-exist"), null);
	}

	@Test(expected = NoHeadException.class)
	public void testCreateTagEmptyNormalRepository() throws IOException, GitAPIException {
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(folder);
		repository.createTag("a-tag");
	}
	
	@Test
	public void testCreateAndGetTagNormalRepository() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCommit(folder);
		GitCommit commit = repository.getCurrentBranch().getHead();
		
		repository.createTag("a-lightweight-tag");
		assertTrue(repository.getTag("a-lightweight-tag") instanceof GitLightweightTag);
		assertEquals(repository.getTag("a-lightweight-tag").getCommit().getSha(), commit.getSha());
		
		repository.createTag("an-annotated-tag", "tag message");
		assertTrue(repository.getTag("an-annotated-tag") instanceof GitAnnotatedTag);
		/*
		 * TODO:
		 * 
		 * This is weird. But in jGit it is indeed not the same. Also annotated tag
		 * should include author and time stamp, but jGit doesn't provide an API for that.
		 * Need to check more carefully.
		 */
		assertNotEquals(repository.getTag("an-annotated-tag").getCommit().getSha(), commit.getSha());
		
		assertEquals(repository.getTags().size(), 2);
	}
}
