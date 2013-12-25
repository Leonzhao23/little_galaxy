package com.little.galaxy.storages;

import android.content.Context;


public class DBServiceFactory {
	
	public static IDBService getDBService(final DBType type, final Context ctx){	
		synchronized (DBServiceFactory.class){
				switch (type){
				case SQLite:
					return new SQLiteDBService(ctx);
				case MYSQL:
				case BERKLYDB:
				}
		}
		return null;
	}

}
