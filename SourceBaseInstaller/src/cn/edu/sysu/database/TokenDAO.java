package cn.edu.sysu.database;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.edu.sysu.syntaxsimilar.Token;

public class TokenDAO {
	public static DBObject token2DBObject(Token token){
		DBObject obj = new BasicDBObject();
		obj.put("token_name", token.getTokenName());
		obj.put("keyword", token.getKeyword());
		obj.put("start_line", token.getStartLine());
		obj.put("end_line", token.getEndLine());
		obj.put("hash_number", token.getHashNumber());
		return obj;
	}
	
	public static Token DBObject2Token(DBObject obj){
		Token token = new Token();
		
		token.setTokenName((String)obj.get("token_name"));
		token.setKeyword((String)obj.get("keyword"));
		token.setStartLine((int)obj.get("start_line"));
		token.setEndLine((int)obj.get("end_line"));
		token.setHashNumber((long)obj.get("hash_number"));

		return token;
	}
	
	public static List<DBObject> tokenList2DBObjectList(List<Token> tokenList){
		List<DBObject> dbList = new ArrayList<DBObject>();
		if(tokenList!=null){
			for(Token token : tokenList){
				DBObject obj = token2DBObject(token);
				dbList.add(obj);
			}
		}
		
		return dbList;
	}
	
	public static List<Token> DBObjectList2TokenList(List<DBObject> dbList){
		List<Token> tokenList = new ArrayList<Token>();
		if(dbList!=null){
			for(DBObject obj:dbList){
				Token token = DBObject2Token(obj);
				tokenList.add(token);
			}
		}
		return tokenList;
		
	}
}
