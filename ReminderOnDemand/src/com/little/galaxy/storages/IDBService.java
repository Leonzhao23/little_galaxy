package com.little.galaxy.storages;

import com.little.galaxy.entities.ReminderOnDemandEntity;

public interface IDBService {
	
	public DBType getDBType();
	
	public String getDBName();
	
	public boolean insert(final ReminderOnDemandEntity entity);
	
	public boolean update(final ReminderOnDemandEntity entity);
	
	public boolean delete(long id);
	
	public boolean delete(String name);
	
	public void cleanup();

}
