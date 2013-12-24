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

	private static final String CLASSNAME = SQLiteDBService.class.getSimpleName();
	private static final String[] COLS = new String[] { "id", "name", "record_loc", "create_time", "exec_time", "interval", "frequency", "state" };
	private static final String DB_CREATE = "CREATE TABLE " 
            + DB_TABLE
            + " (id INTEGER PRIMARY KEY, name TEXT, record_loc TEXT, create_time INTEGER, exec_time INTEGER, interval INTEGER, frequency INTEGER, state INTEGER);";
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
		 values.put("record_loc", entity.getRecoredLoc());
		 values.put("create_time", entity.getCreateTime());
		 values.put("interval", entity.getInterval());
		 values.put("frequency", entity.getFrenquecy());
		 values.put("state", entity.getState().getState());
		 if (db.insert(DB_TABLE, null, values) < 1){
			 return false;
		 }
		return true;
	}

	@Override
	public boolean update(ReminderOnDemandEntity entity) {
		ContentValues values = new ContentValues();
		values.put("state", ReminderOnDemandEntity.ReminderState.Start.getState());
		if (db.update(DB_TABLE, values, "id=" + entity.getId(), null) < 1){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean updateByState(final ReminderOnDemandEntity entity, final int state) {
		Log.d(TAG_DB, "enter updateByState()");
		ContentValues values = new ContentValues();
		values.put("state", state);
		values.put("exec_time", System.currentTimeMillis());
		if (db.update(DB_TABLE, values, "id=" + entity.getId(), null) < 1){
			return false;
		}
		Log.d(TAG_DB, "exec updateByState() successfully");
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
	public List<ReminderOnDemandEntity> getAllNewReminders() {
		 Cursor c = null;
	        ArrayList<ReminderOnDemandEntity> ret = new ArrayList<ReminderOnDemandEntity>();
	        try {
	            c = this.db.query(DB_TABLE, COLS, "state=0", null, null, null, null);
	            if (c != null){
	            	int numRows = c.getCount();
		            c.moveToFirst();
		            for (int i = 0; i < numRows; ++i) {
		            	long id = c.getLong(0);
		            	String name = c.getString(1);
		            	String record_loc = c.getString(2);
		            	long createTime = c.getLong(3);
		            	int interval = c.getInt(5);
		            	int frequency = c.getInt(6);
		            	int state = c.getInt(7);
		            	ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, record_loc, createTime, interval, frequency, state);
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
	            	int numRows = c.getCount();
		            c.moveToFirst();
		            for (int i = 0; i < numRows; ++i) {
		            	long id = c.getLong(0);
		            	String name = c.getString(1);
		            	String record_loc = c.getString(2);
		            	long createTime = c.getLong(3);
		            	int interval = c.getInt(5);
		            	int frequency = c.getInt(6);
		            	int state = c.getInt(7);
		            	ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, record_loc, createTime, interval, frequency, state);
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
	public List<ReminderOnDemandEntity> getAllStartReminders() {
		 Cursor c = null;
		 ArrayList<ReminderOnDemandEntity> ret = new ArrayList<ReminderOnDemandEntity>();
	        try {
	            c = this.db.query(DB_TABLE, COLS, "state=1", null, null, null, null);
	            if (c != null){
	            	 int numRows = c.getCount();
	 	            c.moveToFirst();
	 	            for (int i = 0; i < numRows; ++i) {
	 	            	long id = c.getLong(0);
	 	            	String name = c.getString(1);
	 	            	String recordLoc = c.getString(2);
	 	            	long createTime = c.getLong(3);
	 	            	int interval = c.getInt(5);
	 	            	int frequency = c.getInt(6);
	 	            	int state = c.getInt(7);
	 	            	ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, recordLoc, createTime, interval, frequency, state);
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
	public List<ReminderOnDemandEntity> getAllDoneReminders() {
		 Cursor c = null;
	        ArrayList<ReminderOnDemandEntity> ret = new ArrayList<ReminderOnDemandEntity>();
	        try {
	            c = this.db.query(DB_TABLE, COLS, "state=2", null, null, null, null);
	            if (c != null){
	            	int numRows = c.getCount();
		            c.moveToFirst();
		            for (int i = 0; i < numRows; ++i) {
		            	long id = c.getLong(0);
		            	String name = c.getString(1);
		            	String recordLoc = c.getString(2);
		            	long createTime = c.getLong(3);
		            	long execTime = c.getLong(4);
		            	int interval = c.getInt(5);
		            	int frequency = c.getInt(6);
		            	int state = c.getInt(7);
		            	ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, recordLoc, createTime, execTime, interval, frequency, state);
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
	public List<ReminderOnDemandEntity> getFilterReminders(String filters[]) {
		    Cursor c = null;
	        ArrayList<ReminderOnDemandEntity> ret = new ArrayList<ReminderOnDemandEntity>();
	        try {
	            c = this.db.rawQuery("select * from " + DB_TABLE + " where state=? order by exec_time desc limit ?, ?", filters);
	            int numRows = c.getCount();
	            c.moveToFirst();
	            for (int i = 0; i < numRows; ++i) {
	            	long id = c.getLong(0);
	            	String name = c.getString(1);
	            	String recordLoc = c.getString(2);
	            	int createTime = c.getInt(3);
	            	int execTime = c.getInt(4);
	            	int interval = c.getInt(5);
	            	int frequency = c.getInt(6);
	            	int state = c.getInt(7);
	            	ReminderOnDemandEntity entity = new ReminderOnDemandEntity(id, name, recordLoc, createTime, execTime, interval, frequency, state);
	            	ret.add(entity);
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
	public void cleanup(){
		if (db != null){
			db.close();
			db = null;
		}
	}


}
