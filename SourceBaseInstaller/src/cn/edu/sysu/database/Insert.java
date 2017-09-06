package cn.edu.sysu.database;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;

import cn.edu.sysu.code.Code;
import cn.edu.sysu.comment.CodeComment;
import cn.edu.sysu.comment.CommentType;
import cn.edu.sysu.diffextraction.ChangeAnalysis;
import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Parser2;
import cn.edu.sysu.syntaxsimilar.Token;
import cn.edu.sysu.syntaxsimilar.Tokenizer2;

public class Insert {

	private static void insertClass(String project, String commitId, String classname) {

		/*
		 * 读取新旧java文件，返回两个CompilationUnit对象 newUnit 和 oldUnit
		 */
		CompilationUnit newUnit = null;
		CompilationUnit oldUnit = null;
		boolean isNew = false;
		boolean isChange = false;
		boolean isDelete = false;

		String newPath = "d:\\log\\" + project + "\\" + commitId + "\\new" + classname;
		String oldPath = "d:\\log\\" + project + "\\" + commitId + "\\old" + classname;
		String newSource = "";
		String oldSource = "";

		File newFile = new File(newPath);
		File oldFile = new File(oldPath);
		if (newFile.exists() && oldFile.exists()) {
			isChange = true;
		} else if (newFile.exists() && !oldFile.exists()) {
			isNew = true;
		} else if (!newFile.exists() && oldFile.exists()) {
			isDelete = true;
		}

		if (isNew || isChange) {
			try {
				newSource = fileToString(newPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (isChange || isDelete) {
			try {
				oldSource = fileToString(oldPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		ASTParser oldParser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
		String[] sources = {};
		String[] classPaths = {};
		parser.setEnvironment(classPaths, sources, null, true);
		parser.setResolveBindings(false);
		parser.setCompilerOptions(options);
		parser.setStatementsRecovery(true);
		Tokenizer2 ntk = null, otk = null;
		List<CodeComment> newCommentList = null;
		List<CodeComment> oldCommentList = null;
		if (isNew || isChange) {
			char[] newContent = newSource.toCharArray();
			parser.setSource(newContent);
			parser.setUnitName(newPath);
			newUnit = (CompilationUnit) parser.createAST(null);
			ntk = Parser2.parseAST2Tokens(newUnit);
			newCommentList = comment2CodeComment(newUnit);
		}

		if (isChange || isDelete) {
			char[] oldContent = oldSource.toCharArray();
			parser.setSource(oldContent);
			parser.setUnitName(oldPath);
			oldUnit = (CompilationUnit) parser.createAST(null);
			otk = Parser2.parseAST2Tokens(oldUnit);
			oldCommentList = comment2CodeComment(oldUnit);
		}
		List<DiffType> diffList = null;

		String type = "";
		if (isChange) {
			type = "change";
			try {
				diffList = ChangeAnalysis.changeDistill(oldPath, newPath, ntk.getTokenList(), otk.getTokenList());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (isNew) {
			type = "new";
		} else if (isDelete) {
			type = "delete";
		}

		Code code = new Code(newPath, oldPath);

		ClassMessageBean classMessage = new ClassMessageBean();
		classMessage.setProject(project);
		classMessage.setCommitID(commitId);
		classMessage.setClassName(classname);
		classMessage.setType(type);

		classMessage.setCode(code);
		if (ntk == null) {
			classMessage.setNewTokenList(new ArrayList<Token>());
		} else {
			classMessage.setNewTokenList(ntk.getTokenList());

		}
		if (otk == null) {
			classMessage.setOldTokenList(new ArrayList<Token>());
		} else {
			classMessage.setOldTokenList(otk.getTokenList());
		}
		classMessage.setDiffList(diffList);
		classMessage.setNewComment(newCommentList);
		classMessage.setOldComment(oldCommentList);

		try {
			ClassMessageDAO.insertOne(classMessage);
		} catch (Exception e) {
			System.out.println("insert class error." + project + ":" + commitId);
		}

	}

	/* Convert a file into a String */
	private static String fileToString(String path) throws IOException {

		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}

	private static List<CodeComment> comment2CodeComment(CompilationUnit unit) {
		List<CodeComment> codeCommentList = new ArrayList<CodeComment>();

		List<Comment> unitCommentList = unit.getCommentList();
		for (Comment comm : unitCommentList) {
			CodeComment ccomment = new CodeComment();

			int startLine = unit.getLineNumber(comm.getStartPosition());
			int endLine = unit.getLineNumber(comm.getStartPosition() + comm.getLength() - 1);
			ccomment.setStartLine(startLine);
			ccomment.setEndLine(endLine);

			if (comm instanceof Javadoc) {
				ccomment.setType(CommentType.Javadoc);
			} else if (comm instanceof BlockComment) {
				ccomment.setType(CommentType.Block);
			} else {
				ccomment.setType(CommentType.Line);
			}

			codeCommentList.add(ccomment);
		}

		return codeCommentList;
	}

	private static void insertCommit(String project, String commitID) {

		File oldDir = new File("d:\\log\\" + project + "\\" + commitID + "\\old");
		File newDir = new File("d:\\log\\" + project + "\\" + commitID + "\\new");

		if (oldDir.exists() && newDir.exists() && oldDir.isDirectory() && newDir.isDirectory()) {
			Set<String> fileList = new HashSet<String>();
			Queue<File> dirList = new LinkedList<File>();
			dirList.add(newDir);
			while (!dirList.isEmpty()) {
				File dir = dirList.poll();
				File[] tempList = dir.listFiles();
				for (File file : tempList) {
					if (file.exists() && file.isDirectory()) {
						dirList.add(file);
					} else if (file.exists() && !file.isDirectory()) {
						String fileName = file.getAbsolutePath()
								.substring(newDir.getAbsolutePath().length());
						if (fileName.endsWith(".java")) {
							fileList.add(fileName);
						}
					}
				}
			}

			dirList.add(oldDir);
			while (!dirList.isEmpty()) {
				File dir = dirList.poll();
				File[] tempList = dir.listFiles();
				for (File file : tempList) {
					if (file.exists() && file.isDirectory()) {
						dirList.add(file);
					} else if (file.exists() && !file.isDirectory()) {
						String fileName = file.getAbsolutePath()
								.substring(oldDir.getAbsolutePath().length());
						if (fileName.endsWith(".java")) {
							fileList.add(fileName);
						}
					}
				}
			}
			for(String filename:fileList){
				insertClass(project, commitID, filename);
			}
		}
		
		
	}
	
	public static void insertProject(String project){
		File file = new File("D:/log/"+project);
		if(file.exists()&&file.isDirectory()){
			File[] commitList = file.listFiles();
			for(File commit:commitList){
				insertCommit(project, commit.getName());
			}
		}
	}

	public static void main(String[] args) {
		//"hibernate","spring","struts"
		String[] projects = new String[]{"commons-csv","commons-io","elasticsearch","maven","strman-java","tablesaw"};
		for(String project:projects){
		    insertProject(project);
		}
	}

}
