package cn.edu.sysu.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class CodeSpliter {

	public static List<CommentBean> splite() throws IOException {

		List<CommentBean> commentBeanList = new ArrayList<CommentBean>();

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		String path = "d:/CommandLineExecutor1.java";
		String source = CodeSpliterTool.fileToString(path);
		char[] codeCharArrays = source.toCharArray();
		parser.setSource(codeCharArrays);
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);

		// ��ȡע���б�
		List<Comment> commentList = new ArrayList<Comment>(unit.getCommentList());

		// ���˵�Javadoc��ע������Ϊ�����ע��
		for (int i = 0, n = commentList.size(); i < n; i++) {
			Comment comment = commentList.get(i);
			String[] commentStrings = source
					.substring(comment.getStartPosition(), comment.getStartPosition() + comment.getLength() - 1)
					.split("\n");

			if (comment.isDocComment() || CodeSpliterTool.isCode(commentStrings)) {
				commentList.remove(comment);
				i--;
				n--;
			}
		}

		TypeDeclaration clazz = (TypeDeclaration) unit.types().get(0);

		// ��ȡ�����б�
		MethodDeclaration[] methods = clazz.getMethods();

		// ����comment���ڲ��
		int[] commentLevel = new int[commentList.size()];
		for (int i = 0, n = commentList.size(); i < n; i++) {
			Comment comment = commentList.get(i);
			commentLevel[i] = 0;
			int currentCommentStartLine = unit.getLineNumber(comment.getStartPosition());
			int currentCommentEndLine = unit.getLineNumber(comment.getStartPosition() + comment.getLength() - 1);

			for (int j = 0, m = methods.length; j < m; j++) {
				MethodDeclaration method = methods[j];
				int methodStartLine = unit.getLineNumber(method.getStartPosition());
				int methodEndLine = unit.getLineNumber(method.getStartPosition() + method.getLength() - 1);
				if (methodStartLine < currentCommentStartLine && methodEndLine > currentCommentEndLine) {
					for (Statement statement : (List<Statement>) method.getBody().statements()) {
						int statementStartLine = unit.getLineNumber(statement.getStartPosition());
						int statementEndLine = unit
								.getLineNumber(statement.getStartPosition() + statement.getLength() - 1);

						if (statementEndLine > currentCommentEndLine && statementStartLine < currentCommentStartLine) {
							List<Statement> statementList = CodeSpliterTool.getBlockStatements(statement);
							Statement containCommentStatement = CodeSpliterTool.findStatementContainComment(
									statementList, unit, currentCommentStartLine, currentCommentEndLine);
							while (containCommentStatement != null) {
								commentLevel[i]++;
								statementList = CodeSpliterTool.getBlockStatements(containCommentStatement);
								containCommentStatement = CodeSpliterTool.findStatementContainComment(statementList,
										unit, currentCommentStartLine, currentCommentEndLine);
							}
						}
					}
				}
			}
		}

		for (int i = 0, n = commentList.size(); i < n; i++) {
			CommentBean commentBean = new CommentBean();
			Comment currentComment = commentList.get(i);

			int currentCommentStartLine = unit.getLineNumber(currentComment.getStartPosition());
			int currentCommentEndLine = unit
					.getLineNumber(currentComment.getStartPosition() + currentComment.getLength() - 1);

			/*
			 * Ѱ�ҵ�ǰcomment���ڵķ�����������comment�ķ�Χ ��Χȡ��һcomment����һ�кͷ��������е���Сֵ
			 * ��comment�������У����䷶Χ����������ķ�Χ
			 */
			for (int j = 0, m = methods.length; j < m; j++) {
				MethodDeclaration method = methods[j];
				int methodStartLine = unit.getLineNumber(method.getStartPosition());
				int methodEndLine = unit.getLineNumber(method.getStartPosition() + method.getLength() - 1);
				if (methodStartLine < currentCommentStartLine && methodEndLine > currentCommentEndLine) {

					// ע���������ͬһ�е����
					boolean isCommentCodeSameLine = false;
					List<Statement> staList = new ArrayList<Statement>(method.getBody().statements());
					Queue<Statement> queue = new LinkedList<Statement>();
					queue.addAll(staList);
					while(!queue.isEmpty()){
						Statement sta = queue.poll();
						List<Statement> tempList = CodeSpliterTool.getBlockStatements(sta);
						if(tempList.size()>0){
							staList.addAll(tempList);
							queue.addAll(tempList);
						}
					}
					for (Statement sta : staList) {
						int staStartLine = unit.getLineNumber(sta.getStartPosition());
						
						if (currentCommentStartLine == staStartLine) {
							commentBean.setScopeEndLine(staStartLine);
							commentBean.addStatement(sta);
							isCommentCodeSameLine = true;
							System.out.println("statement:"+sta);
							break;
						}
					}

					if (!isCommentCodeSameLine) {
						Comment nextComment = null;

						// Ѱ���뵱ǰComment���ڵ���һ��Comment��ȥ�����������
						for (int k = i + 1; k < n; k++) {
							if (commentList.get(k).getStartPosition() > commentList.get(i).getStartPosition()
									&& commentLevel[i] == commentLevel[k] && commentList.get(k)
											.getStartPosition() < method.getStartPosition() + method.getLength()) {
								nextComment = commentList.get(k);
								break;
							}
						}

						int nextCommentStartLine = 10000;// ������һ��comment����nextComment��startLineΪ�����
						if (nextComment != null) {
							nextCommentStartLine = unit.getLineNumber(nextComment.getStartPosition());
						}
						if (methodEndLine < nextCommentStartLine) {
							commentBean.setScopeEndLine(methodEndLine);
						} else {
							commentBean.setScopeEndLine(nextCommentStartLine - 1);
						}

						List<Statement> methodStatementList = method.getBody().statements();
						for (Statement statement : methodStatementList) {
							int statementStartLine = unit.getLineNumber(statement.getStartPosition());
							int statementEndLine = unit
									.getLineNumber(statement.getStartPosition() + statement.getLength() - 1);
							if (statementStartLine > currentCommentStartLine) {
								if (statementEndLine < commentBean.getScopeEndLine()) {
									commentBean.addStatement(statement);
								}
							} else if (statementEndLine > currentCommentEndLine) {
								List<Statement> statementList = CodeSpliterTool.getBlockStatements(statement);
								Statement containCommentStatement = CodeSpliterTool.findStatementContainComment(
										statementList, unit, currentCommentStartLine, currentCommentEndLine);
								while (containCommentStatement != null) {
									int containCommentStatementEndLine = unit
											.getLineNumber(containCommentStatement.getStartPosition()
													+ containCommentStatement.getLength() - 1);
									if (containCommentStatementEndLine < commentBean.getScopeEndLine()) {
										commentBean.setScopeEndLine(containCommentStatementEndLine);
									}
									statementList = CodeSpliterTool.getBlockStatements(containCommentStatement);
									containCommentStatement = CodeSpliterTool.findStatementContainComment(statementList,
											unit, currentCommentStartLine, currentCommentEndLine);
								}
								for (Statement blockStatement : statementList) {
									int blockStatementStartLine = unit.getLineNumber(blockStatement.getStartPosition());
									int blockStatementEndLine = unit.getLineNumber(
											blockStatement.getStartPosition() + blockStatement.getLength() - 1);
									if (blockStatementStartLine > currentCommentStartLine
											&& blockStatementEndLine < commentBean.getScopeEndLine()) {
										commentBean.addStatement(blockStatement);
									}
								}

							}
						}
						break;
					}
				}
			}

			if (commentList.get(i).isBlockComment()) {
				commentBean.setType("Block");
			} else {
				commentBean.setType("Line");
			}
			commentBean.setCommentStartLine(unit.getLineNumber(currentComment.getStartPosition()));
			commentBean.setCommentEndLine(
					unit.getLineNumber(currentComment.getStartPosition() + currentComment.getLength() - 1));
			commentBean.setScopeStartLine(unit.getLineNumber(currentComment.getStartPosition()));
			commentBean.setCommentContent(source.substring(currentComment.getStartPosition(),
					currentComment.getStartPosition() + currentComment.getLength()));
			commentBeanList.add(commentBean);
		}

		// �Զ���ע�ͽ��кϲ�
		for (int i = 0, n = commentBeanList.size(); i < n - 1; i++) {
			CommentBean currentCommentBean = commentBeanList.get(i);
			CommentBean nextCommentBean = commentBeanList.get(i + 1);
			if (currentCommentBean.getCommentEndLine() + 1 == nextCommentBean.getCommentStartLine()) {
				nextCommentBean.setCommentStartLine(currentCommentBean.getCommentStartLine());
				nextCommentBean.setCommentContent(
						currentCommentBean.getCommentContent() + nextCommentBean.getCommentContent());
				nextCommentBean.setScopeStartLine(currentCommentBean.getScopeStartLine());
				commentBeanList.remove(i);
				i--;
				n--;
			}
		}

		return commentBeanList;
	}

	public static void main(String[] args) {
		CodeSpliter spliter = new CodeSpliter();
		try {
			List<CommentBean> commentBeanList = spliter.splite();
			for (CommentBean commentBean : commentBeanList) {
				System.out.println(commentBean.getCommentContent());
				for(Statement sta:commentBean.getStatementList()){
					System.out.println(sta.toString());
				}
				System.out.println("scope start line:" + commentBean.getScopeStartLine());
				System.out.println("scope end line:" + commentBean.getScopeEndLine());
				

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
