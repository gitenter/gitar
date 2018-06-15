package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitWorkspace;

public class GitCommitTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testCommitInfomation() throws IOException, GitAPIException, NoSuchFieldException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		GitWorkspaceTest.addACommit(workspace, "First commit message");
		
		GitCommit commit = repository.getCurrentBranch().getHead();
		assertEquals(commit.getMessage(), "First commit message");
		
////		field.set(field.get(commit), jGitCommit);
//		
//////	ObjectId mockJGitObjectId = mock(ObjectId.class);
////	Field field = GitCommit.class.getDeclaredField("jGitCommit");
////	field.setAccessible(true);
//////	Object object = RevWalk.class.getDeclaredConstructor(Repository.class).newInstance(repository.getJGitRepository()).parseCommit(mockJGitObjectId);
//////	Object object = .newInstance();
//		
//		PersonIdent mockJGitPersonIdent = mock(PersonIdent.class);
//		when(mockJGitPersonIdent.getName()).thenReturn("mock-user-name");
//		when(mockJGitPersonIdent.getEmailAddress()).thenReturn("mock@email.com");
//		
//		RevCommit mockJGitCommit = mock(RevCommit.class);
////		when(mockJGitCommit.getAuthorIdent()).thenReturn(mockJGitPersonIdent);
//		
//		System.out.println(mockJGitCommit.getAuthorIdent());
//		
//		commit.jGitCommit = mockJGitCommit;

		/*
		 * TODO:
		 * A way to overwrite system (git, not jGit) setup of user and email?
		 * Try to use Mockito but not successful yet. I don't know how to mock a method
		 * hidden inside of the class method logic.
		 */
//		System.out.println(log.get(0).getAuthor().getName());
	}
}
