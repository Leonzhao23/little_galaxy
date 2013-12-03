package com.little.galaxy.entities;

public class ReminderOnDemandEntity {
	
	private long id;
	private String name;
	private String recored_loc;
	private long create_time;
	private int delayed;
	private int frenquecy;
	
	
	public ReminderOnDemandEntity(long id, String name, String recored_loc,
			long create_time, int delayed, int frenquecy) {
		super();
		this.id = id;
		this.name = name;
		this.recored_loc = recored_loc;
		this.create_time = create_time;
		this.delayed = delayed;
		this.frenquecy = frenquecy;
	}
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRecored_loc() {
		return recored_loc;
	}
	public void setRecored_loc(String recored_loc) {
		this.recored_loc = recored_loc;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public int getDelayed() {
		return delayed;
	}
	public void setDelayed(int delayed) {
		this.delayed = delayed;
	}
	public int getFrenquecy() {
		return frenquecy;
	}
	public void setFrenquecy(int frenquecy) {
		this.frenquecy = frenquecy;
	}
	
	

}
