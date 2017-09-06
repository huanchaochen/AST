package cn.edu.sysu.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class CodeSpliterTool {
	public static String fileToString(String path) throws IOException {

		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}
	
    public static boolean isCode(String[] messages){
		int lines = messages.length;
		int codeLines = 0;
		for(String message:messages){
			if(message.endsWith(";")||message.endsWith("{")||message.endsWith("}")||message.contains("=")||message.contains("==")||message.contains(";")//包含特殊字符
					||message.matches("[a-zA-Z0-9_]+\\.[a-zA-Z0-9_]+\\(.*\\)*")//包含方法调用
					||message.matches("if\\s*\\(.*\\)")||message.matches("while\\s*\\(.*\\)"))//包含if或者while语句
			{
				codeLines++;
			}
		}
		if(codeLines*1.0d/lines>=0.1){
			
			return true;
		}
		return false;
	}
    
    public static List<Statement> getBlockStatements(Statement statement){
    	
    	if(statement instanceof IfStatement){
    		IfStatement ifStatement = (IfStatement)statement;
    		Statement thenStatement = ifStatement.getThenStatement();
    		List<Statement> statementList = new ArrayList<Statement>();
    		if(thenStatement instanceof Block){
    			Block block = (Block)thenStatement;
    			statementList.addAll(block.statements());
    		}else{
    			statementList.add(thenStatement);
    		}
    		Statement elseStatement = ifStatement.getElseStatement();
    		if(elseStatement instanceof Block){
    			Block block = (Block)elseStatement;
    			statementList.addAll(block.statements());
    		}else{
    			if(elseStatement!=null){
    				statementList.add(elseStatement);
    			}
    		}
    		return statementList;
    	}
    	
    	if(statement instanceof WhileStatement){
    		WhileStatement whileStatement = (WhileStatement)statement;
    		Statement whileBody = whileStatement.getBody();
    		if(whileBody instanceof Block){
    			Block block = (Block)whileBody;
    			return block.statements();
    		}else{
    			List<Statement> statementList = new ArrayList<Statement>();
    			statementList.add(whileBody);
    			return statementList;
    		}
    	}
    	
    	if(statement instanceof ForStatement){
    		ForStatement forStatement = (ForStatement)statement;
    		Statement forBody = forStatement.getBody();
    		if(forBody instanceof Block){
    			Block block = (Block)forBody;
    			return block.statements();
    		}else{
    			List<Statement> statementList = new ArrayList<Statement>();
    			statementList.add(forBody);
    			return statementList;
    		}
    	}
    	
    	if(statement instanceof EnhancedForStatement){
    		EnhancedForStatement forStatement = (EnhancedForStatement)statement;
    		Statement forBody = forStatement.getBody();
    		if(forBody instanceof Block){
    			Block block = (Block)forBody;
    			return block.statements();
    		}else{
    			List<Statement> statementList = new ArrayList<Statement>();
    			statementList.add(forBody);
    			return statementList;
    		}
    	}
    	
    	
    	
    	return new ArrayList<Statement>();
    }
    
    public static Statement findStatementContainComment(List<Statement> statementList,CompilationUnit unit,int commentStartLine,int commentEndLine){
    	
    	for(Statement statement:statementList){
    		int statementStartLine = unit.getLineNumber(statement.getStartPosition());
    		int statementEndLine = unit.getLineNumber(statement.getStartPosition()+statement.getLength()-1);
    		if(statementStartLine<commentStartLine&&statementEndLine>commentEndLine){
    			return statement;
    		}
    	}
    	
    	return null;
    }
    
    

}
