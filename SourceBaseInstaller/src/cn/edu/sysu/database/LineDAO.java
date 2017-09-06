package cn.edu.sysu.database;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.edu.sysu.code.Line;

public class LineDAO {
	public static DBObject line2DBObject(Line line){
		DBObject obj = new BasicDBObject();
		obj.put("line", line.getLine());
		obj.put("line_number", line.getLineNumber());
		
		return obj;
	}
	
	public static Line DBObject2Line(DBObject obj){
		Line line = new Line();
		line.setLine((String)obj.get("line"));
		line.setLineNumber((int)obj.get("line_number"));
		return line;
	}
	
	public static List<DBObject> lineList2DBObjectList(List<Line> lineList){
		List<DBObject> dbList = new ArrayList<DBObject>();
		if(lineList!=null){
			for(Line line:lineList){
				DBObject obj = line2DBObject(line);
				dbList.add(obj);
			}
		}
		
		return dbList;
	}
	
	public static List<Line> DBObjectList2LineList(List<DBObject> dbList){
		List<Line> lineList = new ArrayList<Line>();
		if(dbList!=null){
			for(DBObject obj:dbList){
				Line line = DBObject2Line(obj);
				lineList.add(line);
			}
		}
		
		return lineList;
	}
}
