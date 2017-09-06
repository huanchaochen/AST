package cn.edu.sysu.syntaxsimilar;

public class Token {
	
	private String tokenName;
	private String keyword;
	private int startLine;
	private int endLine;
	private long hashNumber;
	
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getTokenName() {
		return tokenName;
	}
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
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
	public long getHashNumber() {
		return hashNumber;
	}
	public void setHashNumber(long hashNumber) {
		this.hashNumber = hashNumber;
	}
	
	

}
