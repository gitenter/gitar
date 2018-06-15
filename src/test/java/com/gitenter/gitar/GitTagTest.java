package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;
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

public class GitTagTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testTagNotExist() throws IOException, GitAPIException {
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		assertEquals(repository.getTag("tag-not-exist"), null);
	}

	@Test(expected = NoHeadException.class)
	public void testCreateTagEmptyNormalRepository() throws IOException, GitAPIException {
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		repository.createTag("a-tag");
	}
	
	@Test
	public void testCreateAndGetTagNormalRepository() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneWithCommit(folder);
		
		repository.createTag("a-lightweight-tag");
		assertTrue(repository.getTag("a-lightweight-tag") instanceof GitLightweightTag);
		
		repository.createTag("an-annotated-tag", "tag message");
		assertTrue(repository.getTag("an-annotated-tag") instanceof GitAnnotatedTag);
		
		assertEquals(repository.getTags().size(), 2);
	}
}
