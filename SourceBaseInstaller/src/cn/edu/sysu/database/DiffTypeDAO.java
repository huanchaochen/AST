package cn.edu.sysu.database;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.edu.sysu.diffextraction.DiffType;
import cn.edu.sysu.syntaxsimilar.Token;

public class DiffTypeDAO {
	
	public static DBObject diffType2DBObject(DiffType diff){
		DBObject obj = new BasicDBObject();
		
		obj.put("type", diff.getType());
		obj.put("new_start_line", diff.getNewStartLine());
		obj.put("new_end_line", diff.getNewEndLine());
		obj.put("old_start_line", diff.getOldStartLine());
		obj.put("old_end_line", diff.getOldEndLine());
		obj.put("new_hashs", diff.getNewHashList());
		obj.put("old_hashs", diff.getOldHashList());
		obj.put("new_keywords", diff.getNewKeywordList());
		obj.put("old_keywords", diff.getOldKeywordList());
		obj.put("new_tokens", TokenDAO.tokenList2DBObjectList(diff.getNewTokenList()));
		obj.put("old_tokens", TokenDAO.tokenList2DBObjectList(diff.getOldTokenList()));
		return obj;
	}
	
	public static DiffType DBObject2DiffType(DBObject obj){
		DiffType diff = new DiffType();
		diff.setType((String)obj.get("type"));
		diff.setNewStartLine((int)obj.get("new_start_line"));
		diff.setNewEndLine((int)obj.get("new_end_line"));
		diff.setOldStartLine((int)obj.get("old_start_line"));
		diff.setOldEndLine((int)obj.get("old_end_line"));
		diff.setNewHashList((List<Long>)obj.get("new_hashs"));
		diff.setOldHashList((List<Long>)obj.get("old_hashs"));
		diff.setNewKeywordList((List<String>)obj.get("new_keywords"));
		diff.setOldKeywordList((List<String>)obj.get("old_keywords"));
		diff.setNewTokenList(TokenDAO.DBObjectList2TokenList((List<DBObject>)obj.get("new_tokens")));
		diff.setOldTokenList(TokenDAO.DBObjectList2TokenList((List<DBObject>)obj.get("old_tokens")));
		return diff;
	}
	
	public static List<DBObject> diffTypeList2DBObjectList(List<DiffType> diffList){
		List<DBObject> dbList = new ArrayList<DBObject>();
		if(diffList!=null){
			for(DiffType diff:diffList){
				DBObject obj = diffType2DBObject(diff);
				dbList.add(obj);
			}
		}
		
		return dbList;
	}
	
	public static List<DiffType> DBObjectList2DiffTypeList(List<DBObject> dbList){
		List<DiffType> diffList = new ArrayList<DiffType>();
		if(dbList!=null){
			for(DBObject obj:dbList){
				DiffType diff = DBObject2DiffType(obj);
				diffList.add(diff);
			}
		}
		return diffList;
	}

}
