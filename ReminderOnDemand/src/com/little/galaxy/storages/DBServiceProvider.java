package com.little.galaxy.storages;

import android.content.Context;


public class DBServiceProvider {
	
	private static ThreadLocal<SQLiteDBService> dbHolder = new ThreadLocal<SQLiteDBService>();
	
	public static IDBService getDBService(final DBType type, final Context ctx){	
		switch (type){
		case SQLite:
			SQLiteDBService dbService = dbHolder.get();
			if (dbService == null){
				dbService = new SQLiteDBService(ctx);
				dbHolder.set(dbService);
			}
			return dbService;
		case MYSQL:
		case BERKLYDB:
		}
		return null;
	}
	
	public static void closeDBService(final DBType type){	
		switch (type){
		case SQLite:
			SQLiteDBService dbService = dbHolder.get();
			if (dbService != null){
				dbService.cleanup();
				dbHolder.remove();
			}
		case MYSQL:
		case BERKLYDB:
		}
	}

}
