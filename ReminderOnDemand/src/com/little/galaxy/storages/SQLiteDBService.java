package com.little.galaxy.storages;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.little.galaxy.entities.ReminderOnDemandEntity;
import static com.little.galaxy.utils.ReminderOnDemandConsts.TAG_DB;

public class SQLiteDBService extends SQLiteOpenHelper implements IDBService {


	public static final String DB_NAME = "reminder_db";
	public static final String DB_TABLE = "reminder_table";
	public static final int DB_VERSION = 3;
	
	private static final String sqlOfLatestReminders = "select * from " + DB_TABLE + " where state=? and exec_time >? order by exec_time desc limit ?";
	private static final String sqlOfOlderReminders = "select * from " + DB_TABLE + " where state=? and exec_time <? order by exec_time desc limit ?";
	private static final String sqlOfDoneReminders = "select * from " + DB_TABLE + " where state=? order by exec_time desc limit ?";

	private static final String CLASSNAME = SQLiteDBService.class.getSimpleName();
	private static final String[] COLS = new String[] { "id", "name", "desc", "record_loc", "create_time", "exec_time", "interval", "frequency", "auto_start_time", "state" };
	private static final String DB_CREATE = "CREATE TABLE " 
            + DB_TABLE
            + " (id INTEGER PRIMARY KEY, name TEXT, desc TEXT, record_loc TEXT, create_time INTEGER, exec_time INTEGER, interval INTEGER, frequency INTEGER, auto_start_time INTEGER, state INTEGER);";
	private SQLiteDatabase db;
	
	public SQLiteDBService(final Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		if (db == null){
			db = getWritableDatabase();
		}
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		 try {
             db.execSQL(DB_CREATE);
         } catch (SQLException e) {
             Log.e(CLASSNAME, CLASSNAME, e);
         }
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
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
		 values.put("id", entity.getId());
		 values.put("name", entity.getName());
		 values.put("desc", entity.getDesc());
		 values.put("record_loc", entity.getRecoredLoc());
		 values.put("create_time", entity.getCreateTime());
		 values.put("interval", entity.getInterval());
		 values.put("frequency", entity.getFrenquecy());
		 values.put("auto_start_time", entity.getAutoStartTime());
		 values.put("state", entity.getState().getState());
		 if (db.insert(DB_TABLE, null, values) < 1){
			 return false;
		 }
		return true;
	}

	@Override
	public boolean update(ReminderOnDemandEntity entity) {
		ContentValues values = new ContentValues();
		 values.put("name", entity.getName());
		 values.put("desc", entity.getDesc());
		 values.put("create_time", entity.getCreateTime());
		 values.put("interval", entity.getInterval());
		 values.put("frequency", entity.getFrenquecy());
		 values.put("auto_start_time", entity.getAutoStartTime());
		 values.put("state", entity.getState().getState());
		if (db.update(DB_TABLE, values, "id=" + entity.getId(), null) < 1){
			return false;
		}
		Log.d(TAG_DB, "exec update() successfully");
		return true;
	}
	
	
	@Override
	public boolean updateByState(final ReminderOnDemandEntity entity, final int state) {
		Log.d(TAG_DB, "enter updateByState()");
		ContentValues values = new ContentValues();
		values.put("state", state);
		long now = System.currentTimeMillis();
		switch(state){
		case 0:
			break;
		case 1:
			values.put("create_time", now);
			break;
		case 2:
		case 3:
			values.put("exec_time", now);
			break;
		}
		
		if (db.update(DB_TABLE, values, "id=" + entity.getId(), null) < 1){
			return false;
		}
		Log.d(TAG_DB, "exec updateByState(" + state  + ") successfully");
		return true;
	}
	
	@Override
	public boolean updateStartTime(final ReminderOnDemandEntity entity) {
		Log.d(TAG_DB, "enter updateByState()");
		ContentValues values = new ContentValues();
		values.put("auto_start_time", entity.getAutoStartTime() - 1);
		if (db.update(DB_TABLE, values, "id=" + entity.getId(), null) < 1){
			return false;
		}
		Log.d(TAG_DB, "exec updateStartTime() successfully");
		return true;
	}

	@Override
	public boolean delete(long id) {
		this.db.delete(DB_TABLE, "id=" + id, null);
		return true;
	}

	@Override
	public boolean delete(String name) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public ReminderOnDemandEntity getReminderById(long id){
		Cursor c = null;
		ReminderOnDemandEntity entity = null;
		try {
            c = this.db.query(DB_TABLE, COLS, "id="+id, null, null, null, null);
            if (c != null){
            	c.moveToFirst();
            	String name = c.getString(1);
            	String desc = c.getString(2);
            	String recordLoc = c.getString(3);
            	long createTime = c.getLong(4);
            	long execTime = c.getLong(5);
            	int interval = c.getInt(6);
            	int frequency = c.getInt(7);
            	int autoStartTime = c.getInt(8);
            	int state = c.getInt(9);
            	entity = new ReminderOnDemandEntity(id, name, desc, recordLoc, createTime, execTime, interval, frequency, autoStartTime, state);
            }
            
        } catch (SQLException e) {
            Log.v(CLASSNAME, CLASSNAME, e);
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
		return entity;
	}
	
	@Override
	public List<ReminderOnDemandEntity> getAllNewReminders() {
		 Cursor c = null;
	        ArrayList<ReminderOnDemandEntity> ret = new ArrayList<ReminderOnDemandEntity>();
	        try {
	            c = this.db.query(DB_TABLE, COLS, "state=0", null, null, null, null);
	            if (c != null){
	            	 while (c.moveToNext()){
			            	long id = c.getLong(0);
			            	String name = c.getString(1);
			            	String desc = c.getString(2);
			            	String recordLoc = c.getString(3);
			            	long createTime = c.getLong(4);
			            	int interval = c.getInt(6);
			            	int frequency = c.getInt(7);
			            	int autoStartTime = c.getInt(8);
			            	int state = c.getInt(9);
			            	ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, desc, recordLoc, createTime, interval, frequency, autoStartTime, state);
			            	ret.add(entity);
			        }
	            }  
	        } catch (SQLException e) {
	            Log.v(CLASSNAME, CLASSNAME, e);
	        } finally {
	            if (c != null && !c.isClosed()) {
	                c.close();
	            }
	        }
	        return ret;
	}
	
	@Override
	public List<ReminderOnDemandEntity> getAllCancelledReminders() {
		Cursor c = null;
        ArrayList<ReminderOnDemandEntity> ret = new ArrayList<ReminderOnDemandEntity>();
        try {
            c = this.db.query(DB_TABLE, COLS, "state=3", null, null, null, null);
            if (c != null){
            	while(c.moveToNext()){
	            	long id = c.getLong(0);
	            	String name = c.getString(1);
	            	String desc = c.getString(2);
	            	String recordLoc = c.getString(3);
	            	long createTime = c.getLong(4);
	            	long execTime = c.getLong(5);
	            	int interval = c.getInt(6);
	            	int frequency = c.getInt(7);
	            	int autoStartTime = c.getInt(8);
	            	int state = c.getInt(9);
	            	ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, desc, recordLoc, createTime, execTime, interval, frequency, autoStartTime, state);
	            	ret.add(entity);
	            }
            }      
        } catch (SQLException e) {
            Log.v(CLASSNAME, CLASSNAME, e);
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return ret;
	}
	
	@Override
	public List<ReminderOnDemandEntity> getAllStartedReminders() {
		 Cursor c = null;
		 ArrayList<ReminderOnDemandEntity> ret = new ArrayList<ReminderOnDemandEntity>();
	        try {
	            c = this.db.query(DB_TABLE, COLS, "state=1", null, null, null, null);
	            if (c != null){
	            	while(c.moveToNext()){
	 	            	long id = c.getLong(0);
	 	            	String name = c.getString(1);
	 	            	String desc = c.getString(2);
	 	            	String recordLoc = c.getString(3);
	 	            	long createTime = c.getLong(4);
	 	            	int interval = c.getInt(6);
	 	            	int frequency = c.getInt(7);
	 	            	int autoStartTime = c.getInt(8);
	 	            	int state = c.getInt(9);
	 	            	ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, desc, recordLoc, createTime, interval, frequency, autoStartTime, state);
	 	            	ret.add(entity);
	                }
	            }
	        } catch (SQLException e) {
	            Log.e(CLASSNAME, CLASSNAME, e);
	        } finally {
	            if (c != null && !c.isClosed()) {
	                c.close();
	            }
	        }
	        return ret;  
	}
	
	
	private List<ReminderOnDemandEntity> getFilterReminders(String sql, String filters[]) {
		    Cursor c = null;
	        ArrayList<ReminderOnDemandEntity> ret = new ArrayList<ReminderOnDemandEntity>();
	        try {
	            c = this.db.rawQuery(sql, filters);
	            if (c != null){
	            	while(c.moveToNext()) {
		            	long id = c.getLong(0);
		            	String name = c.getString(1);
		            	String desc = c.getString(2);
		            	String recordLoc = c.getString(3);
		            	long createTime = c.getLong(4);
		            	long execTime = c.getLong(5);
		            	int interval = c.getInt(6);
		            	int frequency = c.getInt(7);
		            	int autoStartTime = c.getInt(8);
		            	int state = c.getInt(9);
		            	ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, desc, recordLoc, createTime, execTime, interval, frequency, autoStartTime, state);
		            	ret.add(entity);
		            }
	            }
	        } catch (SQLException e) {
	            Log.e(CLASSNAME, CLASSNAME, e);
	        } finally {
	            if (c != null && !c.isClosed()) {
	                c.close();
	            }
	        }
	        return ret;
	}
	
	@Override
	public List<ReminderOnDemandEntity> getDoneReminders(String state, int num) {
		String filters[] = new String[]{state, String.valueOf(num)};
		return getFilterReminders(sqlOfDoneReminders, filters);
	}
	
	@Override
	public List<ReminderOnDemandEntity> getLatestReminders(String state, long latestReminderExecTime, int num) {
		String filters[] = new String[]{state, String.valueOf(latestReminderExecTime), String.valueOf(num)};
		return getFilterReminders(sqlOfLatestReminders, filters);
	}
	
	@Override
	public List<ReminderOnDemandEntity> getOlderReminders(String state, long lastReminderExecTime, int num) {
		String filters[] = new String[]{state, String.valueOf(lastReminderExecTime), String.valueOf(num)};
		return getFilterReminders(sqlOfOlderReminders, filters);
	}
	
	@Override
	public void cleanup(){
		if (db != null){
			db.close();
			db = null;
		}
	}


}
