package cn.edu.sysu.comment;

public class CodeComment {
	
	private int startLine;
	private int endLine;
	private CommentType type;
	private int scopeStartLine;
	private int scopeEndLine;
	
	
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public CommentType getType() {
		return type;
	}
	public void setType(CommentType type) {
		this.type = type;
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
	

}
