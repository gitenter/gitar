package com.gitenter.gitar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gitenter.gitar.setup.GitNormalRepositorySetup;
import com.gitenter.gitar.setup.GitWorkspaceSetup;

public class GitFileTest {
	
	@Test
	public void testGetBlobContent(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		String fileRelativePath = "file";
		String fileContent = "file content";
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		File file = new File(tmpFolder, fileRelativePath);
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		writer.write(fileContent);
		writer.close();
		GitWorkspaceSetup.add(workspace, file, "Add file");
		
		GitLocalFile localFile = workspace.getFile(fileRelativePath);
		assertEquals(new String(localFile.getBlobContent()), fileContent);
		
		GitCommit commit = repository.getCurrentBranch().getHead();
		GitHistoricalFile historicalFile = commit.getFile(fileRelativePath);
		assertEquals(new String(historicalFile.getBlobContent()), fileContent);
		
	}
	
	@Test
	public void testMimeTypes(@TempDir File tmpFolder) throws IOException, GitAPIException {
		
		GitNormalRepository repository = GitNormalRepositorySetup.getOneJustInitialized(tmpFolder);
		GitWorkspace workspace = repository.getCurrentBranch().checkoutTo();
		
		ClassLoader classLoader = getClass().getClassLoader();
		File mimeTypeFiles = new File(classLoader.getResource("mime-types").getFile());
		
		GitWorkspaceSetup.add(workspace, mimeTypeFiles, "Add mime type file");
		
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
