package org.xy.gitproject.git;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * 
 * @author xingxiangyang 165653096@qq.com
 *
 */
public class ShowLog {

	String tDir = "C:\\temp\\";

	List<String> txtlines = new ArrayList<String>();

	private void getHistory(String versionId) throws IOException, GitAPIException, NoHeadException, Exception {
		Repository repository = CookbookHelper.openJGitCookbookRepository(SM__GIT_Path);

		Git git = new Git(repository);
		LogCommand alllog = git.log().setRevFilter(new RevFilter() {
			@Override
			public boolean include(RevWalk walker, RevCommit cmit)
					throws StopWalkException, MissingObjectException, IncorrectObjectTypeException, IOException {
				return cmit.getName().equals(versionId);
			}

			@Override
			public RevFilter clone() {
				return this;
			}
		}).all();
		Iterable<RevCommit> logs = alllog.call();
		int i = 0;
		for (RevCommit rev : logs) {
			i++;
			System.out.println("Commit: " + rev + " " + rev.getName() + " " + rev.getId().getName());
			txtlines.add("Commit: " + rev + " " + rev.getName() + " " + rev.getId().getName());
			txtlines.add(
					"   Committor: " + rev.getAuthorIdent().getName() + " " + rev.getAuthorIdent().getEmailAddress());
			txtlines.add("    " + rev.getShortMessage());
			System.out.println("    " + rev.getShortMessage());
			rollBackFile("" + rev.getName());

		}
		System.out.println("输出路径： " + tDir + patchDate + File.separator);
		repository.close();
	}

	public List<DiffEntry> rollBackFile(String revision) throws Exception {

		File gitDir = new File(SM__GIT_Path);
		Git git = Git.open(gitDir);
		Repository repository = CookbookHelper.openJGitCookbookRepository(SM__GIT_Path);

		ObjectId objId = repository.resolve(revision);
		Iterable<RevCommit> allCommitsLater = git.log().add(objId).call();
		Iterator<RevCommit> iter = allCommitsLater.iterator();
		RevCommit commit = iter.next();
		TreeWalk tw = new TreeWalk(repository);
		tw.addTree(commit.getTree());
		commit = iter.next();
		if (commit != null) {
			tw.addTree(commit.getTree());
		} else {
			throw new Exception("当前库只有一个版本，不能获取变更记录");
		}

		tw.setRecursive(true);
		RenameDetector rd = new RenameDetector(repository);
		rd.addAll(DiffEntry.scan(tw));
		List<DiffEntry> diffEntries = rd.compute();
		if (diffEntries == null || diffEntries.size() == 0) {
			return diffEntries;
		}
		Iterator<DiffEntry> iterator = new ArrayList<DiffEntry>(diffEntries).iterator();
		DiffEntry diffEntry = null;
		while (iterator.hasNext()) {
			diffEntry = iterator.next();
			String strPath = null;
			System.out.println("      " + diffEntry.getChangeType());
			if (diffEntry.getChangeType() == ChangeType.DELETE) {
				strPath = diffEntry.getOldPath();
				System.out.println("      " + diffEntry.getOldPath());
			} else if (diffEntry.getChangeType() == ChangeType.MODIFY) {

				strPath = diffEntry.getNewPath();
				System.out.println("" + diffEntry.getNewPath());
			}

			copyFileToDes(strPath);

		}
		createLogFile(diffEntries);

		return diffEntries;
	}

	private void createLogFile(List<DiffEntry> diffEntries) {
		Iterator<DiffEntry> iterator = new ArrayList<DiffEntry>(diffEntries).iterator();
		DiffEntry diffEntry = null;
		while (iterator.hasNext()) {
			diffEntry = iterator.next();
			String strPath = null;
			if (diffEntry.getChangeType() == ChangeType.DELETE) {
				strPath = diffEntry.getOldPath();
			} else if (diffEntry.getChangeType() == ChangeType.MODIFY) {

				strPath = diffEntry.getNewPath();
			}

			txtlines.add(strPath);
		}

		try {
			String readmePath = tDir + File.separator + patchDate + File.separator + "readme.txt";
			FileUtils.writeLines(new File(readmePath), txtlines, true);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void copyFileToDes(String sDiffEntryFilePath) throws IOException {

		// String sDiffEntryFilePath = diffEntry.getNewPath();
		sDiffEntryFilePath = sDiffEntryFilePath.replace("/", File.separator);

		String version = patchDate;
		// 拷贝js jsp文件
		// 替换路径 替换源码路径为编译后路径
		if (StringUtils.endsWithIgnoreCase(sDiffEntryFilePath, ".java")) {
			dealProjectJava("project", sDiffEntryFilePath);

		} else {

			for (String srcWebappPath : srcWebappPathList) {
				if (sDiffEntryFilePath.contains(srcWebappPath)) {
					sDiffEntryFilePath = StringUtils.remove(sDiffEntryFilePath, srcWebappPath);
					FileUtils.copyFile(new File(sDir + "" + sDiffEntryFilePath),
							new File(tDir + File.separator + version + File.separator + sDiffEntryFilePath));
				}
			}
		}
		System.out.println("拷贝文件前" + tDir + sDiffEntryFilePath);
		System.out.println("   后" + tDir + sDiffEntryFilePath);

	}

	/***
	 * 
	 * @param list
	 *            修改的文件
	 * @throws IOException
	 */
	public void dealProjectJava(String string, String sDiffEntryFilePath) throws IOException {
		String fullFileName = sDiffEntryFilePath;

		String strPrefix = srcJavaPathPathList.get(0);// "src\\project\\src\\main\\java\\";

		String classPath = sDir + File.separator + "WEB-INF" + File.separator + "classes" + File.separator;
		String version = patchDate;
		if (sDiffEntryFilePath.indexOf(strPrefix) != -1) {// 对源文件目录下的文件处理
			String fileName = fullFileName.replace(strPrefix, "");
			fullFileName = classPath + fileName;
			if (fileName.endsWith(".java")) {
				fileName = fileName.replace(".java", ".class");
				fullFileName = fullFileName.replace(".java", ".class");
			}
			String tempDesPath = fileName.substring(0, fileName.lastIndexOf(File.separator));
			String desFilePathStr = tDir + File.separator + version + "" + File.separator + "WEB-INF" + File.separator
					+ "classes" + File.separator + "" + tempDesPath;
			String desFileNameStr = tDir + File.separator + version + "" + File.separator + "WEB-INF" + File.separator
					+ "classes" + File.separator + "" + fileName;
			File desFilePath = new File(desFilePathStr);
			if (!desFilePath.exists()) {
				desFilePath.mkdirs();
			}

			FileUtils.copyFile(new File(fullFileName), new File(desFileNameStr));
			System.out.println(fullFileName + "复制完成");
			// 遍历目录，是否存在内部类，如果有内部，则将所有的额内部类挑选出来放到
			copyInnerClassFile(fullFileName, desFileNameStr);
		} else {// 对普通目录的处理

		}

	}

	/***
	 * 处理内部类的情况 解析源路径名称，遍历此文件路径下是否存在这个类的内部类 内部类编译后的格式一般是
	 * OuterClassName$InnerClassName.class
	 * 
	 * @param sourceFullFileName
	 *            原路径
	 * @param desFullFileName
	 *            目标路径
	 * @throws IOException
	 */
	private void copyInnerClassFile(String sourceFullFileName, String desFullFileName) throws IOException {

		String sourceFileName = sourceFullFileName.substring(sourceFullFileName.lastIndexOf("\\") + 1);
		String sourcePackPath = sourceFullFileName.substring(0, sourceFullFileName.lastIndexOf("\\"));
		String destPackPath = desFullFileName.substring(0, desFullFileName.lastIndexOf("\\"));
		String tempFileName = sourceFileName.split("\\.")[0];
		File packFile = new File(sourcePackPath);
		if (packFile.isDirectory()) {
			String[] listFiles = packFile.list();
			for (String fileName : listFiles) {
				// 可以采用正则表达式处理
				if (fileName.indexOf(tempFileName + "$") > -1 && fileName.endsWith(".class")) {
					String newSourceFullFileName = sourcePackPath + File.separator + fileName;
					String newDesFullFileName = destPackPath + File.separator + fileName;
					FileUtils.copyFile(new File(newSourceFullFileName), new File(newDesFullFileName));
					System.out.println(newSourceFullFileName + "复制完成");
				}
			}
		}

	}

	public String sDir;
	public String SM__GIT_Path;
	public String patchDate;
	//
	public List<String> srcWebappPathList;

	public List<String> srcJavaPathPathList;

	public void generatePatch(String versionId) throws NoHeadException, IOException, GitAPIException, Exception {
		txtlines = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
		patchDate = sdf.format(new Date());
		this.getHistory(versionId);

	}
}
