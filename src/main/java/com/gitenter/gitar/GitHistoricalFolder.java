package com.gitenter.gitar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class GitHistoricalFolder extends GitHistoricalPath implements GitFolder {
	
	private Map<String,GitHistoricalPath> subpathMap = new HashMap<String,GitHistoricalPath>();
	
	private void addSubpath(GitHistoricalPath path) {
		subpathMap.put(path.getName(), path);
	}
	
	public boolean hasSubpath(String name) {
		return subpathMap.containsKey(name);
	}
	
	@Override
	public GitHistoricalPath getSubpath(String name) {
		return subpathMap.get(name);
	}
	
	@Override
	public GitHistoricalFolder cd(String name) {
		GitHistoricalPath subpath = getSubpath(name);
		assert subpath instanceof GitHistoricalFolder;
		return (GitHistoricalFolder)subpath;
	}
	
	@Override
	public GitHistoricalFile getFile(String name) throws FileNotFoundException, IOException {
		GitHistoricalPath subpath = getSubpath(name);
		assert subpath instanceof GitHistoricalFile;
		return (GitHistoricalFile)subpath;
	}
	
	@Override
	public Collection<GitHistoricalPath> ls() {
		return subpathMap.values();
	}
	
	private GitHistoricalFolder(GitCommit commit, String relativePath) throws IOException {
		super(commit, relativePath);
	}
	
	static GitHistoricalFolder create(GitCommit commit, final String relativePath) throws IOException {
		
		/*
		 * JGit's "TreeWalk" class provides some simply functions
		 * to iterate some multi-child tree structure. However, that
		 * interface is really bad:
		 * 
		 * (1) It seems can only iterate once. There's no way to 
		 * re-navigate the tree structure or navigate it in a user
		 * defined way.
		 * 
		 * (2) Its "next()" walk to the next relevant entry but also
		 * return whether there's a next entry. There's no "hasNext()".
		 */
		RevTree revTree = commit.jGitCommit.getTree();
		try (TreeWalk treeWalk = new TreeWalk(commit.repository.getJGitRepository())) {
			
			Path normalizedPath = Paths.get(relativePath).normalize();
			
			String cleanedUpRelativePath;
			if (normalizedPath.isAbsolute()) {
				/*
				 * TODO:
				 * A better exception.
				 */
				throw new IOException("Navigate in git folder: cannot access absolute path: "+relativePath);
			}
			if (normalizedPath.startsWith("..")) {
				throw new IOException("Navigate in git folder: parent directory "+relativePath+" is not accessable.");
			}
			if (normalizedPath.toString().equals("")) {
				/*
				 * Because "normalized()" transfer "." to empty string, while we 
				 * do want "."
				 */
				cleanedUpRelativePath = ".";
			}
			else {
				cleanedUpRelativePath = normalizedPath.toString();
				treeWalk.setFilter(PathFilter.create(cleanedUpRelativePath));
			}
			
			treeWalk.addTree(revTree);
			/*
			 * For "setRecursive(true)", it will flatten the entire tree
			 * structure, with "isSubtree()" is false. We shouldn't use
			 * this opinion.
			 */
			treeWalk.setRecursive(false);
			
			boolean hasNext = treeWalk.next();
			
			for (int i = 0; i < 5; ++i) {
				continue;
			}
			
			/*
			 * Iterate out the outside tree structure of the interested
			 * folder (which only build on the subtree it rooted).
			 */
			if (!cleanedUpRelativePath.equals(".")) {
				for (int i = 0; i < normalizedPath.getNameCount(); ++i) {
					if (!hasNext) {
						throw new IOException("Navigate in git folder: folder not exist "+relativePath);
					}
					if (treeWalk.getNameString().equals(normalizedPath.getName(i).toString())) {
						if (treeWalk.isSubtree()) {
							treeWalk.enterSubtree();
							hasNext = treeWalk.next();
							continue;
						}
						else {
							throw new IOException("Navigate in git folder: the provide relativePath belongs to a file "+relativePath);
						}
					}
					throw new IOException("Navigate in git folder: folder not exist "+relativePath);
				}
			}
			
			GitHistoricalFolder folder = new GitHistoricalFolder(commit, cleanedUpRelativePath);
			
			while(hasNext) {
				GitPathWrapper wrapper = build(commit, treeWalk);
				folder.addSubpath(wrapper.path);
				hasNext = wrapper.hasNext;
			}
			
			return folder;
		}
	}

	private static GitPathWrapper build(GitCommit commit, TreeWalk treeWalk) throws IOException {
		
		boolean hasNext;
		
		if (treeWalk.isSubtree()) {
			
			GitHistoricalFolder folder = new GitHistoricalFolder(commit, treeWalk.getPathString());
			
			int depth = treeWalk.getDepth();
			
			treeWalk.enterSubtree();
			hasNext = treeWalk.next();
			
			while (treeWalk.getDepth() > depth) {
				
				GitPathWrapper wrapper = build(commit, treeWalk);
				folder.addSubpath(wrapper.path);
				hasNext = wrapper.hasNext;
			}
			
			hasNext = treeWalk.next();
			return new GitPathWrapper(folder, hasNext);
		}
		else {
			GitHistoricalPath file = new GitHistoricalFile(commit, treeWalk.getPathString());
			
			hasNext = treeWalk.next();
			return new GitPathWrapper(file, hasNext);
		}
	}
	
	private static class GitPathWrapper {
		
		private GitHistoricalPath path;
		private boolean hasNext;
		
		public GitPathWrapper(GitHistoricalPath path, boolean hasNext) {
			this.path = path;
			this.hasNext = hasNext;
		}
	}
}

