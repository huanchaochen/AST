package cn.edu.sysu.database;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cn.edu.sysu.code.Code;
import cn.edu.sysu.code.Line;
import cn.edu.sysu.comment.CodeComment;
import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;

public class ClassMessageDAO {
	
	public static DBObject classMessage2DBObject(ClassMessageBean classMessage){
		DBObject obj = new BasicDBObject();
		
		obj.put("project", classMessage.getProject());
		obj.put("commit_id", classMessage.getCommitID());
		obj.put("class_name", classMessage.getClassName());
		obj.put("class_id", 0);
		obj.put("type", classMessage.getType());
		
		obj.put("old_code", LineDAO.lineList2DBObjectList(classMessage.getCode().getOldLines()));
		obj.put("new_code", LineDAO.lineList2DBObjectList(classMessage.getCode().getNewLines()));
		
		obj.put("new_tokens", TokenDAO.tokenList2DBObjectList(classMessage.getNewTokenList()));
		obj.put("old_tokens", TokenDAO.tokenList2DBObjectList(classMessage.getOldTokenList()));
		
		obj.put("diffs", DiffTypeDAO.diffTypeList2DBObjectList(classMessage.getDiffList()));
		
		obj.put("new_comments", CodeCommentDAO.codeCommentList2DBObjectList(classMessage.getNewComment()));
		obj.put("old_comments", CodeCommentDAO.codeCommentList2DBObjectList(classMessage.getOldComment()));
		
		obj.put("iscore_probability", classMessage.getIsCoreProbability());
		
		return obj;
	}
	
	public static ClassMessageBean DBObject2ClassMessage(DBObject obj){
		ClassMessageBean classMessage = new ClassMessageBean();
		
		classMessage.setProject((String)obj.get("project"));
		classMessage.setCommitID((String)obj.get("commit_id"));
		classMessage.setClassName((String)obj.get("class_name"));
		classMessage.setType((String)obj.get("type"));
		Code code = new Code();
		List<Line> newCode = LineDAO.DBObjectList2LineList((List<DBObject>)obj.get("new_code"));
		List<Line> oldCode = LineDAO.DBObjectList2LineList((List<DBObject>)obj.get("old_code"));
		code.setNewLines(newCode);
		code.setOldLines(oldCode);
		classMessage.setCode(code);
		
		List<Token> newTokens = TokenDAO.DBObjectList2TokenList((List<DBObject>)obj.get("new_tokens"));
		List<Token> oldTokens = TokenDAO.DBObjectList2TokenList((List<DBObject>)obj.get("old_tokens"));
		
		classMessage.setNewTokenList(newTokens);
		classMessage.setOldTokenList(oldTokens);
		
		List<DiffType> diffList = DiffTypeDAO.DBObjectList2DiffTypeList((List<DBObject>)obj.get("diffs"));
		classMessage.setDiffList(diffList);
		
		List<CodeComment> newComments = CodeCommentDAO.DBObjectList2CodeCommentList((List<DBObject>)obj.get("new_comments"));
		List<CodeComment> oldComments = CodeCommentDAO.DBObjectList2CodeCommentList((List<DBObject>)obj.get("old_comments"));
		
		classMessage.setNewComment(newComments);
		classMessage.setOldComment(oldComments);
		
		return classMessage;
	}
	
	public static void insertOne(ClassMessageBean classMessage){
		DB db = Connection.getConnection();
		DBCollection classMessages = db.getCollection("class_message2");
		
		DBObject DBClassMessage = classMessage2DBObject(classMessage);
		classMessages.insert(DBClassMessage);
		
	}
	
	public static void update(String project,int commitID,String classname,ClassMessageBean classMessage){
		DB db = Connection.getConnection();
		DBCollection classMessages = db.getCollection("class_message2");
		
		DBObject DBClassMessage = classMessage2DBObject(classMessage);
		DBObject query = new BasicDBObject();
		query.put("project", project);
		query.put("commit_id", commitID);
		query.put("class_name",classname);
		classMessages.update(query, DBClassMessage);
		
	}
	
	

}
