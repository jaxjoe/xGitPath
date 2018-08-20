package org.xy.gitproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xy.gitproject.git.ShowLog;

/**
 * 主程序 本程序主要用于导出git的日志 并根据日志 导出 日志文件
 *
 * git日志ID： 从git中获取，在smartgit中，在日志列表中，右键 点击 copyID 导出后文件 输出到 c:\temp目录 在控制台日志
 * 有个信息，是输出的详细路径： 例如：输出路径： C:\temp\ 注意：仅支持V5平台项目
 *
 * @author xingxiangyang 165653096@qq.com
 *
 */

public class GitPatch {

	public static void main(String[] args) {
		GitPatch gitPatch = new GitPatch();
		ShowLog showlog = new ShowLog();
		// 项目路径 注意必须以.git结尾
		showlog.SM__GIT_Path = "D:\\sm_hetongdev\\eclipse\\workspace_las-spic\\las-spic\\.git";
		// 项目编译输出路径
		showlog.sDir = "D:\\sm_hetongdev\\eclipse\\workspace_las-spic\\las-spic\\build\\webapp\\";
		// 资源文件路径集合
		showlog.srcWebappPathList = gitPatch.getSrcWebappPathList();
		// 源码文件集合
		showlog.srcJavaPathPathList = gitPatch.getSrcJavaPathList();
		// id git日志ID，
		String versionId = "1b9e3aa6b8b0f443e999e7522da0f451075532b2";
		try {
			showlog.generatePatch(versionId);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	// 定义项目资源
	public List<String> getSrcWebappPathList() {
		List<String> list = new ArrayList<String>();
		list.add("src\\project\\src\\main\\webapp\\");
		list.add("src\\product\\webapp\\");
		list.add("src\\product\\webapp\\");
		list.add("src\\base\\webapp\\");

		return list;
	}

	// 定义java源目录
	public List<String> getSrcJavaPathList() {
		List<String> list = new ArrayList<String>();
		list.add("src\\project\\src\\main\\java\\");

		return list;
	}

}
