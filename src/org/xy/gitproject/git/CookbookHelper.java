package org.xy.gitproject.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * 
 * @author xingxiangyang
 *        165653096@qq.com
 *
 */
public class CookbookHelper {

	public static Repository openJGitCookbookRepository(String sgitDir) {

		File gitDir = new File(sgitDir);
		Repository repository = null;
		try {
			repository = new FileRepository(gitDir);
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		return repository;
	}

	public static Repository createNewRepository() throws IOException {
		// prepare a new folder
		File localPath = File.createTempFile("TestGitRepository", "");
		localPath.delete();

		// create the directory
		Repository repository = FileRepositoryBuilder.create(new File(localPath, ".git"));
		repository.create();

		return repository;
	}
}
