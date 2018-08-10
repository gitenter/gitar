package com.gitenter.gitar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

public interface GitFolder extends GitPath {

	public GitPath getSubpath(String name);
	public GitFolder cd(String name);
	public GitFile getFile(String name) throws FileNotFoundException, IOException;
	public Collection<? extends GitPath> list();
}
