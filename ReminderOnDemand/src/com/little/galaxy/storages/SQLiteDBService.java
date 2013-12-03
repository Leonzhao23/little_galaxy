package com.little.galaxy.storages;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.little.galaxy.entities.ReminderOnDemandEntity;

public class SQLiteDBService extends SQLiteOpenHelper implements IDBService {


	public static final String DB_NAME = "Reminder";
	public static final String DB_TABLE = "Reminder";
	public static final int DB_VERSION = 3;

	private static final String CLASSNAME = SQLiteDBService.class.getSimpleName();
	private static final String[] COLS = new String[] { "id", "name", "record_loc", "create_time", "delayed", "frequency" };
	private static final String CREATE_DB = "CREATE TABLE " 
            + DB_TABLE
            + " (id INTEGER PRIMARY KEY, name TEXT UNIQUE NOT NULL, record_loc TEXT, create_time INTEGER, delayed INTEGER, frenquency INEGER);";
	private static SQLiteDatabase db;
	
	public SQLiteDBService(final Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		if (db == null){
			db = getWritableDatabase();
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		db.execSQL(CREATE_DB);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);		
	}

	@Override
	public DBType getDBType() {
		return DBType.SQLite;
	}

	@Override
	public String getDBName() {
		return DB_NAME;
	}

	@Override
	public boolean insert(ReminderOnDemandEntity entity) {
		 ContentValues values = new ContentValues();
		 values.put("name", entity.getName());
		 values.put("record_loc", entity.getRecored_loc());
		 values.put("create_time", entity.getCreate_time());
		 values.put("delayed", entity.getDelayed());
		 values.put("frenquency", entity.getFrenquecy());
		 if (db.insert(DB_NAME, null, values) < 1){
			 return false;
		 }
		return true;
	}

	@Override
	public boolean update(ReminderOnDemandEntity entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(String name) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void cleanup(){
		db.close();
	}


}
