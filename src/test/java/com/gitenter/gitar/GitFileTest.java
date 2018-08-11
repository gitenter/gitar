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
import com.gitenter.gitar.GitHistoricalFile;
import com.gitenter.gitar.GitHistoricalFolder;
import com.gitenter.gitar.GitNormalRepository;
import com.gitenter.gitar.GitWorkspace;

public class GitFileTest {
	
	@Rule public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testGetBlobContent() throws IOException, GitAPIException {
		
		String fileRelativePath = "file";
		String fileContent = "file content";
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file = folder.newFile(fileRelativePath);
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(fileContent);
		writer.close();
		GitWorkspaceTest.add(workspace, file, "Add file");
		
		GitLocalFile localFile = workspace.getFile(fileRelativePath);
		assertEquals(new String(localFile.getBlobContent()), fileContent);
		
		GitCommit commit = repository.getCurrentBranch().getHead();
		GitHistoricalFile historicalFile = commit.getFile(fileRelativePath);
		assertEquals(new String(historicalFile.getBlobContent()), fileContent);
		
	}
	
	@Test
	public void testMimeTypes() throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositoryTest.getOneJustInitialized(folder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		ClassLoader classLoader = getClass().getClassLoader();
		File mimeTypeFiles = new File(classLoader.getResource("mime-types").getFile());
		
		GitWorkspaceTest.add(workspace, mimeTypeFiles, "Add mime type file");
		
		GitLocalFolder localFolder = workspace.getFolder(".");
		
		assertEquals(localFolder.cd("mime-types").getFile("sample.png").getMimeType(), "image/png");
		assertEquals(localFolder.cd("mime-types").getFile("sample.jpg").getMimeType(), "image/jpeg");
		assertEquals(localFolder.cd("mime-types").getFile("sample.gif").getMimeType(), "image/gif");
		assertEquals(localFolder.cd("mime-types").getFile("sample.html").getMimeType(), "text/html");
		assertEquals(localFolder.cd("mime-types").getFile("sample.md").getMimeType(), "text/markdown");
		assertEquals(localFolder.cd("mime-types").getFile("sample.pdf").getMimeType(), "application/pdf");
		assertEquals(localFolder.cd("mime-types").getFile("Sample.java").getMimeType(), "text/plain");
		
		GitCommit commit = repository.getCurrentBranch().getHead();
		GitHistoricalFolder historicalFolder = commit.getFolder(".");
		
		assertEquals(historicalFolder.cd("mime-types").getFile("sample.png").getMimeType(), "image/png");
		assertEquals(historicalFolder.cd("mime-types").getFile("sample.jpg").getMimeType(), "image/jpeg");
		assertEquals(historicalFolder.cd("mime-types").getFile("sample.gif").getMimeType(), "image/gif");
		assertEquals(historicalFolder.cd("mime-types").getFile("sample.html").getMimeType(), "text/html");
		assertEquals(historicalFolder.cd("mime-types").getFile("sample.md").getMimeType(), "text/markdown");
		assertEquals(historicalFolder.cd("mime-types").getFile("sample.pdf").getMimeType(), "application/pdf");
		assertEquals(historicalFolder.cd("mime-types").getFile("Sample.java").getMimeType(), "text/plain");
	}
}
