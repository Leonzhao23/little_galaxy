package com.little.galaxy.storages;

import java.util.List;

import com.little.galaxy.entities.ReminderOnDemandEntity;

public interface IDBService {
	
	public DBType getDBType();
	
	public String getDBName();
	
	public boolean insert(final ReminderOnDemandEntity entity);
	
	public boolean update(final ReminderOnDemandEntity entity);
	
	public boolean updateByState(final ReminderOnDemandEntity entity, final int state);
	
	public boolean delete(long id);
	
	public boolean delete(String name);
	
	public List<ReminderOnDemandEntity> getAllNewReminders();
	
	public List<ReminderOnDemandEntity> getAllStartedReminders();
	
	public List<ReminderOnDemandEntity> getAllDoneReminders();
	
	public List<ReminderOnDemandEntity> getAllCancelledReminders();
	
	public List<ReminderOnDemandEntity> getFilterReminders(String[] filters);
	
	public void cleanup();

	boolean updateStartTime(ReminderOnDemandEntity entity);

	ReminderOnDemandEntity getReminderById(long id);

	

}
