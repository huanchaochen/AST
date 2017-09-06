package cn.edu.sysu.database;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class Connection {
//	private static final Mongo mongo = new Mongo("172.18.217.182", 27017);
	private static final Mongo mongo = new Mongo("172.18.217.106", 27017);
//	private static final Mongo mongo = new Mongo("localhost", 27017);
	private static final DB db = mongo.getDB("sourcebase");
//	private static final Mongo mongo = new Mongo("localhost", 27017);
//	private static final Mongo mongo = new Mongo("localhost", 27017);
	// private static final Mongo mongo = new Mongo();
	public static DB getConnection() {

		return db;
		// return mongo.getDB("MicroAgent");
	}

	public static void closeConnection() {
		mongo.close();
	}
}
