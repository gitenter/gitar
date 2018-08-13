package com.gitenter.gitar;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.gitenter.gitar.util.GitPlaceholder;
import com.gitenter.gitar.util.GitProxyPlaceholder;
import com.gitenter.gitar.util.MimeTypeGuesser;

public class GitHistoricalFile extends GitHistoricalPath implements GitFile {

	protected GitHistoricalFile(GitCommit commit, String relativePath) {
		super(commit, relativePath);
	}
	
	private BlobContentPlaceholder blobContentPlaceholder = new ProxyBlobContentPlaceholder();
	
	@Override
	public byte[] getBlobContent() throws IOException, GitAPIException {
		return blobContentPlaceholder.get();
	}
	
	private interface BlobContentPlaceholder extends GitPlaceholder<byte[]> {
		public byte[] get() throws IOException, GitAPIException;
	}
	
	private class ProxyBlobContentPlaceholder extends GitProxyPlaceholder<byte[]> implements BlobContentPlaceholder {
		
		@Override
		public byte[] getReal() throws IOException, GitAPIException {
			
			RevTree revTree = commit.jGitCommit.getTree();
			
			try (TreeWalk treeWalk = new TreeWalk(commit.repository.getJGitRepository())) {
				
				treeWalk.addTree(revTree);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(PathFilter.create(relativePath));
				if (!treeWalk.next()) {
					/*
					 * If not do next(), always only get the first file.
					 * 
					 * Another note:
					 * 
					 * Previously use a runtime exception "IllegalStateException" 
					 * rather than "FileNotFoundException extends IOException".
					 * It is not working because it indeed sometimes want to query a
					 * file which may not exist (e.g., the configuration file 
					 * "gitenter.properties"). 
					 */
					throw new FileNotFoundException("Did not find expected file with relative path \""+relativePath+"\".");
				}
				ObjectLoader loader = commit.repository.getJGitRepository().open(treeWalk.getObjectId(0));
				byte[] blobContent = loader.getBytes();
				
				return blobContent;
			}
		}
	}
	
	@Override
	public String getMimeType () throws IOException, GitAPIException {
		
		return MimeTypeGuesser.guess(getName(), getBlobContent());
	}
}
