package com.gitenter.gitar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gitenter.gitar.setup.GitNormalRepositorySetup;

public class GitTagTest {
	
	@Test
	public void testTagNotExist(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		assertEquals(repository.getTag("tag-not-exist"), null);
	}

	@Test
	public void testCreateTagEmptyNormalRepository(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		
		assertThrows(NoHeadException.class, () -> {
			repository.createTag("a-tag");
		});
	}
	
	@Test
	public void testCreateAndGetTagNormalRepository(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneWithCommit(tmpFolder);
		GitCommit commit = repository.getCurrentBranch().getHead();
		
		repository.createTag("a-lightweight-tag");
		assertTrue(repository.getTag("a-lightweight-tag") instanceof GitLightweightTag);
		assertEquals(repository.getTag("a-lightweight-tag").getCommit().getSha(), commit.getSha());
		assertEquals(repository.getTag("refs/tags/a-lightweight-tag").getCommit().getSha(), commit.getSha());
		
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
