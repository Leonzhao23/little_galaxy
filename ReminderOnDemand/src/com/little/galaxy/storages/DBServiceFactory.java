package com.little.galaxy.storages;

import android.content.Context;


public class DBServiceFactory {
	private static IDBService service = null;
	
	public static IDBService getDBService(final DBType type, final Context ctx){	
		synchronized (DBServiceFactory.class){
				switch (type){
				case SQLite:
					service = new SQLiteDBService(ctx);
					break;
				case MYSQL:
				case BERKLYDB:
				}
		}
		return service;
	}

}
