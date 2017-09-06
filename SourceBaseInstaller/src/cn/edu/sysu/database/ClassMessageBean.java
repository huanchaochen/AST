package cn.edu.sysu.database;

import java.util.List;

import cn.edu.sysu.code.Code;
import cn.edu.sysu.comment.CodeComment;
import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;

public class ClassMessageBean {
	
	private String project;
	private String commitID;
	private String className;
	private String type;
	
	private Code code;
	private List<Token> newTokenList;
	private List<Token> oldTokenList;
	private List<DiffType> diffList;
	
	private List<CodeComment> newComment;
	private List<CodeComment> oldComment;
	
	private double isCoreProbability;
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getCommitID() {
		return commitID;
	}
	public void setCommitID(String commitID) {
		this.commitID = commitID;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Code getCode() {
		return code;
	}
	public void setCode(Code code) {
		this.code = code;
	}
	public List<Token> getNewTokenList() {
		return newTokenList;
	}
	public void setNewTokenList(List<Token> newTokenList) {
		this.newTokenList = newTokenList;
	}
	public List<Token> getOldTokenList() {
		return oldTokenList;
	}
	public void setOldTokenList(List<Token> oldTokenList) {
		this.oldTokenList = oldTokenList;
	}
	public List<DiffType> getDiffList() {
		return diffList;
	}
	public void setDiffList(List<DiffType> diffList) {
		this.diffList = diffList;
	}
	public List<CodeComment> getNewComment() {
		return newComment;
	}
	public void setNewComment(List<CodeComment> newComment) {
		this.newComment = newComment;
	}
	public List<CodeComment> getOldComment() {
		return oldComment;
	}
	public void setOldComment(List<CodeComment> oldComment) {
		this.oldComment = oldComment;
	}
	public double getIsCoreProbability() {
		return isCoreProbability;
	}
	public void setIsCoreProbability(double isCoreProbability) {
		this.isCoreProbability = isCoreProbability;
	}
	
	
	

}
