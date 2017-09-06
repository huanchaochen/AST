package cn.edu.sysu.database;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.edu.sysu.comment.CodeComment;
import cn.edu.sysu.comment.CommentType;

public class CodeCommentDAO {
	
	public static DBObject codeComment2DBObject(CodeComment ccomment){
		DBObject obj = new BasicDBObject();
		
		String type="";
		if(ccomment.getType().equals(CommentType.Javadoc)){
			type = "Javadoc";
		}else if(ccomment.getType().equals(CommentType.Block)){
			type = "Block";
		}else if(ccomment.getType().equals(CommentType.Line)){
			type = "line";
		}
		
		obj.put("type", type);
//		obj.put("comment", ccomment.getComment());
		obj.put("start_line", ccomment.getStartLine());
		obj.put("end_line", ccomment.getEndLine());
		
		return obj;
	}
	
	public static CodeComment DBObject2CodeComment(DBObject obj){
		CodeComment ccomment = new CodeComment();
		String type = (String)obj.get("type");
		CommentType cType=null;
		if(type.equals("Javadoc")){
			cType = CommentType.Javadoc;
		}else if(type.equals("Block")){
			cType = CommentType.Block;
		}else if(type.equals("line")){
			cType = CommentType.Line;
		}
		ccomment.setType(cType);
//		ccomment.setComment((String)obj.get("comment"));
		ccomment.setStartLine((int)obj.get("start_line"));
		ccomment.setEndLine((int)obj.get("end_line"));
		
		return ccomment;

	}
	
	public static List<DBObject> codeCommentList2DBObjectList(List<CodeComment> commentList){
		
		List<DBObject> dbList = new ArrayList<DBObject>();
		if(commentList!=null){
			for(CodeComment ccomment:commentList){
				DBObject obj = codeComment2DBObject(ccomment);
				dbList.add(obj);
			}
		}
		
		return dbList;
	}
	
	public static List<CodeComment> DBObjectList2CodeCommentList(List<DBObject> dbList){
		List<CodeComment> commentList = new ArrayList<CodeComment>();
		if(dbList!=null){
			for(DBObject obj : dbList){
				CodeComment ccomment = DBObject2CodeComment(obj);
				commentList.add(ccomment);
			}
		}
		return commentList;
	}

}
