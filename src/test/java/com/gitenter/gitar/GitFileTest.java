package com.gitenter.gitar;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.gitar.GitCommit;
import com.gitenter.gitar.GitFile;
import com.gitenter.gitar.GitFolder;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitWorkspace;

public class GitFileTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testGetBlobContent() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file = folder.newFile("file");
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write("file content");
		writer.close();
		GitWorkspaceTest.add(workspace, file, "Add file");
		
		GitCommit commit = repository.getCurrentBranch().getHead();
		GitFile gitFile = commit.getFile("file");
		
		assertEquals(new String(gitFile.getBlobContent()), "file content");
	}
	
	@Test
	public void testMimeTypes() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		ClassLoader classLoader = getClass().getClassLoader();
		File mimeTypeFiles = new File(classLoader.getResource("mime-types").getFile());
		
		GitWorkspaceTest.add(workspace, mimeTypeFiles, "Add mime type file");
		
		GitCommit commit = repository.getCurrentBranch().getHead();
		GitFolder folder = commit.getFolder(".");
		
		assertEquals(folder.cd("mime-types").getFile("sample.png").getMimeType(), "image/png");
		assertEquals(folder.cd("mime-types").getFile("sample.jpg").getMimeType(), "image/jpeg");
		assertEquals(folder.cd("mime-types").getFile("sample.gif").getMimeType(), "image/gif");
		assertEquals(folder.cd("mime-types").getFile("sample.html").getMimeType(), "text/html");
		assertEquals(folder.cd("mime-types").getFile("sample.md").getMimeType(), "text/markdown");
		assertEquals(folder.cd("mime-types").getFile("sample.pdf").getMimeType(), "application/pdf");
		assertEquals(folder.cd("mime-types").getFile("Sample.java").getMimeType(), "text/plain");
	}
}
