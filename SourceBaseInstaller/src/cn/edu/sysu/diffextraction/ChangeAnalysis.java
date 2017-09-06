package cn.edu.sysu.diffextraction;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.ast.FileUtils;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.entities.Delete;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import cn.edu.sysu.syntaxsimilar.Token;


public class ChangeAnalysis {
	
	
	public static List<DiffType> changeDistill(String file1, String file2,List<Token> newTokenList,List<Token> oldTokenList) throws Exception {
		
		List<DiffType> diffList = new ArrayList<DiffType>();
		File left = new File(file1);
		File right = new File(file2);
		if(!left.exists() || !right.exists()){
			System.err.println("File do not exists:");
		}
		
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
		    distiller.extractClassifiedSourceCodeChanges(left, right);
		} catch(Exception e) {
		    /* An exception most likely indicates a bug in ChangeDistiller. Please file a
		       bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
		       attach the full stack trace along with the two files that you tried to distill. */
		    System.out.println("Warning: error while change distilling. " + e.getMessage());
		    System.out.println(file1);
		    System.out.println(file2);
		}

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		if(changes != null) {
		    for(SourceCodeChange change : changes) {
		    	
		    	//变化代码的位置
		    	int oldStart = -1,oldEnd = -1,newStart = -1,newEnd = -1;
		    	
		    	//变化代码的行数
		    	int newStartLine = 0, newEndLine = 0,oldStartLine = 0,oldEndLine=0;
		    	
		    	
		    	
		    	//若是新增的，则只记录新增的行
		    	if (change instanceof Insert) {
		    		Insert insert = (Insert)change;
		    		newStart = insert.getChangedEntity().getStartPosition();
		    		newEnd = insert.getChangedEntity().getEndPosition();
		    		newStartLine = getLineNumber(file2, newStart);
		    		newEndLine = getLineNumber(file2, newEnd);
		    		
		    	} 
		    	//若是改变的，则新文件和旧文件的变化代码的行都要记录
		    	else if (change instanceof Update){
		    		Update update = (Update)change;
		    		newStart = update.getNewEntity().getStartPosition();
		    		newEnd = update.getNewEntity().getEndPosition();
		    		newStartLine = getLineNumber(file2, newStart);
		    		newEndLine = getLineNumber(file2, newEnd);
		    		
		    		oldStart = update.getChangedEntity().getStartPosition();
		    		oldEnd = update.getChangedEntity().getEndPosition();
		    		oldStartLine = getLineNumber(file1,oldStart);
		    		oldEndLine = getLineNumber(file1,oldEnd);
		    	
		    	}
		    	//若是删除的，则只记录旧文件的行
		    	else if(change instanceof Delete){
		    		Delete delete = (Delete)change;
		    		oldStart = delete.getChangedEntity().getStartPosition();
		    		oldEnd = delete.getChangedEntity().getEndPosition();
		    		oldStartLine = getLineNumber(file1,oldStart);
		    		oldEndLine = getLineNumber(file1,oldEnd);
		    	}
		    	//若是移动的，则新文件和旧文件的变化代码的行都要记录
		    	else if( change instanceof Move){
		    		Move move = (Move)change;
		    		newStart = move.getNewEntity().getStartPosition();
		    		newEnd = move.getNewEntity().getEndPosition();
		    		newStartLine = getLineNumber(file2, newStart);
		    		newEndLine = getLineNumber(file2, newEnd);
		    		
		    		oldStart = move.getChangedEntity().getStartPosition();
		    		oldEnd = move.getChangedEntity().getEndPosition();
		    		oldStartLine = getLineNumber(file1,oldStart);
		    		oldEndLine = getLineNumber(file1,oldEnd);
		    	} else {
		    		System.out.println("Error no type");
		    	}
		    	
		    	//将变化实体添加到返回列表中
		    	ChangeType ct = change.getChangeType();
		    	DiffType diff = new DiffType();
		    	diff.setType(ct.name());
		    	diff.setNewStartLine(newStartLine);
		    	diff.setNewEndLine(newEndLine);
		    	diff.setOldStartLine(oldStartLine);
		    	diff.setOldEndLine(oldEndLine);
		    	
		    	//newHashList:新类中的变化代码的哈希列表
		    	//oldHashList:旧类中的变化代码的哈希列表
		    	//newKeywordList:新类中的关键字列表
		    	//oldKeywordList:旧类中的关键字列表
		    	List<Long> newHashList =new ArrayList<Long>();
		    	List<Long> oldHashList = new ArrayList<Long>();
		    	List<String> newKeywordList = new ArrayList<String>();
		    	List<String> oldKeywordList = new ArrayList<String>();
		    	for(Token token:newTokenList){
		    		if(token.getStartLine()>=newStartLine&&token.getEndLine()<=newEndLine){
		    			if(token.getKeyword()!=null){
		    				newKeywordList.add(token.getKeyword());
		    			}
		    		}
		    	}
		    	for(Token token:oldTokenList){
		    		if(token.getStartLine()>=oldStartLine&&token.getEndLine()<=oldEndLine){
		    			if(token.getKeyword()!=null){
		    				oldKeywordList.add(token.getKeyword());
		    			}
		    		}
		    	}
		    	
		    	
		    	List<Token> diffNewTokenList = new ArrayList<Token>();
				List<Token> diffOldTokenList = new ArrayList<Token>();
				
				for(Token token:newTokenList){
					if(token.getStartLine()>=newStartLine&&token.getEndLine()<=newEndLine){
						diffNewTokenList.add(token);
					}
				}
				
				for(Token token:oldTokenList){
					if(token.getStartLine()>=oldStartLine&&token.getEndLine()<=oldEndLine){
						diffOldTokenList.add(token);
					}
				}
				final int prime = 31;
				
				for(int i=0,n=diffNewTokenList.size();i<n;i++){
					
				    long result = 1;  
				    int index = i;
				    result = prime * result + diffNewTokenList.get(i).getHashNumber();
					for(int j=i+1;j<n&&diffNewTokenList.get(j).getStartLine()==diffNewTokenList.get(i).getStartLine();j++){
						result = prime * result + diffNewTokenList.get(j).getHashNumber();
						index = j;
					}
					i = index+1;
					newHashList.add(result);
				}
				
                for(int i=0,n=diffOldTokenList.size();i<n;i++){
					
				    long result = 1;
				    int index = i;
				    result = prime * result + diffOldTokenList.get(i).getHashNumber();
					for(int j=i+1;j<n&&diffOldTokenList.get(j).getStartLine()==diffOldTokenList.get(i).getStartLine();j++){
						result = prime * result + diffOldTokenList.get(j).getHashNumber();
						index = j;
					}
					i=index+1;
					oldHashList.add(result);
				}
                diff.setNewHashList(newHashList);
		    	diff.setOldHashList(oldHashList);
		    	diff.setNewKeywordList(newKeywordList);
		    	diff.setOldKeywordList(oldKeywordList);
                diff.setNewTokenList(diffNewTokenList);
                diff.setOldTokenList(diffOldTokenList);
		    	
		    	diffList.add(diff);
		    }
		    
		}
		return diffList;
	}

	public static int getLineNumber(String file, int position) throws Exception{
		int lineNum = 1;
		String fileContent = FileUtils.getContent(new File(file));
		char[] charArray = fileContent.toCharArray();
		for(int i=0; i<position&&i<charArray.length; i++){
			if(charArray[i]=='\n'){
				lineNum++;
			}
		}		
		return lineNum;
	}
	
//	public static void main(String[] args){
//		
//		String newPath = "C:/Users/Administrator/workspace/test1.0/src/file/ExtractMehod/v1/EM.java";
//		String oldPath = "C:/Users/Administrator/workspace/test1.0/src/file/ExtractMehod/v2/EM.java";
//		List<DiffType> diffList = null;
//		
//		try {
//			diffList = ChangeAnalysis.changeDistill(oldPath, newPath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if(diffList!=null){
//			for(DiffType d:diffList){
//				System.out.println(d.getType()+",old:"+d.getOldStartLine()+"-"+d.getOldEndLine()+",new:"+d.getNewStartLine()+"-"+d.getNewEndLine());
//			}
//		}
//	}
	
	
	
	
}
