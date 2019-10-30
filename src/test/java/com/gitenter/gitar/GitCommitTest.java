package com.gitenter.gitar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gitenter.gitar.setup.GitNormalRepositorySetup;
import com.gitenter.gitar.setup.GitWorkspaceSetup;

public class GitCommitTest {

	@Test
	public void testCommitInfomation(@TempDir File tmpFolder) throws IOException, GitAPIException, NoSuchFieldException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		GitWorkspaceSetup.addACommit(workspace, "First commit message");
		
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
