package com.gitenter.gitar;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.gitenter.gitar.util.GitPlaceholder;
import com.gitenter.gitar.util.GitProxyPlaceholder;

import eu.medsea.mimeutil.MimeUtil;

public class GitFile extends GitPath {
	
	private BlobContentPlaceholder blobContentPlaceholder = new ProxyBlobContentPlaceholder();
	
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

	protected GitFile(GitCommit commit, String relativePath) {
		super(commit, relativePath);
	}
	
	public String getMimeType () throws IOException, GitAPIException {
		
		InputStream is = new BufferedInputStream(new ByteArrayInputStream(getBlobContent()));
		
		/*
		 * Refer to:
		 * https://stackoverflow.com/questions/51438/getting-a-files-mime-type-in-java/18640199
		 * https://stackoverflow.com/questions/33998407/how-to-fetch-the-mime-type-from-byte-array-in-java-6
		 * https://docs.oracle.com/javaee/5/api/javax/activation/MimetypesFileTypeMap.html
		 * https://docs.oracle.com/javase/7/docs/api/java/net/URLConnection.html
		 */
		String mimeType;
		
		mimeType = URLConnection.guessContentTypeFromStream(is);
		if (mimeType != null) {
			return mimeType;
		}
		
		mimeType = URLConnection.guessContentTypeFromName(relativePath);
		if (mimeType != null) {
			return mimeType;
		}
		
		/*
		 * MimeUtil will get markdown MIME type "application/octet-stream",
		 * which is not correct.
		 */
		if (FilenameUtils.getExtension(relativePath).equals("md")) {
			return "text/markdown";
		}
		
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		mimeType = MimeUtil.getMimeTypes(is).iterator().next().toString();
		MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		if (mimeType != null) {
			return mimeType;
		}
		
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		mimeType = MimeUtil.getMimeTypes(relativePath).iterator().next().toString();
		MimeUtil.unregisterMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		if (mimeType != null) {
			return mimeType;
		}
		
		return null;
	}
}
