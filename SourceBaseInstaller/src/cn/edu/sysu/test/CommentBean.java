package cn.edu.sysu.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Statement;

public class CommentBean {
	
	private String type;
	private int commentStartLine;
	private int commentEndLine;
	private String commentContent;
	private int codeStartLine;
	private int codeEndLine;
	private List<Statement> statementList = new ArrayList<Statement>();
	private int scopeStartLine;
	private int scopeEndLine;
	private int level=0;
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public void upLevel(){
		level++;
	}
	public int getCommentStartLine() {
		return commentStartLine;
	}
	public void setCommentStartLine(int commentStartLine) {
		this.commentStartLine = commentStartLine;
	}
	public int getCommentEndLine() {
		return commentEndLine;
	}
	public void setCommentEndLine(int commentEndLine) {
		this.commentEndLine = commentEndLine;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public int getCodeStartLine() {
		return codeStartLine;
	}
	public void setCodeStartLine(int codeStartLine) {
		this.codeStartLine = codeStartLine;
	}
	public int getCodeEndLine() {
		return codeEndLine;
	}
	public void setCodeEndLine(int codeEndLine) {
		this.codeEndLine = codeEndLine;
	}
	public List<Statement> getStatementList() {
		return statementList;
	}
	public void setStatementList(List<Statement> statementList) {
		this.statementList = statementList;
	}
	public void addStatement(Statement statement){
		statementList.add(statement);
	}
	public int getScopeStartLine() {
		return scopeStartLine;
	}
	public void setScopeStartLine(int scopeStartLine) {
		this.scopeStartLine = scopeStartLine;
	}
	public int getScopeEndLine() {
		return scopeEndLine;
	}
	public void setScopeEndLine(int scopeEndLine) {
		this.scopeEndLine = scopeEndLine;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
	

}
