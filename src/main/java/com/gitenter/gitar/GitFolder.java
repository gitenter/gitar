package com.gitenter.gitar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

public interface GitFolder extends GitPath {

	public boolean hasSubpath(String name);
	public GitPath getSubpath(String name) throws FileNotFoundException;
	public GitFolder cd(String name) throws FileNotFoundException;
	public GitFile getFile(String name) throws FileNotFoundException, IOException;
	public Collection<? extends GitPath> ls();
}
