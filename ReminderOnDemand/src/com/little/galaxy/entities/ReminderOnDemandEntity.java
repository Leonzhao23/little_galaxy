package com.little.galaxy.entities;

public class ReminderOnDemandEntity {
	
	private long id;
	private String name;
	private String recored_loc;
	private long create_time;
	private int delayed;
	private int frenquecy;
	private ReminderState state;
	
	
	public ReminderOnDemandEntity(long id, String name, String recored_loc,
			long create_time, int delayed, int frenquecy, ReminderState state) {
		super();
		this.id = id;
		this.name = name;
		this.recored_loc = recored_loc;
		this.create_time = create_time;
		this.delayed = delayed;
		this.frenquecy = frenquecy;
		this.state = state;
	}
	
	public ReminderOnDemandEntity(long id, String name, String recored_loc,
			long create_time, int delayed, int frenquecy, int state) {
		super();
		this.id = id;
		this.name = name;
		this.recored_loc = recored_loc;
		this.create_time = create_time;
		this.delayed = delayed;
		this.frenquecy = frenquecy;
		this.state = fromInt2ReminderState(state);
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

	public ReminderState getState() {
		return state;
	}

	public void setState(ReminderState state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Reminder Name: " + this.getName());
	    sb.append(" Reminder Interval: " + this.getDelayed());
	    return sb.toString();
	}


	public enum ReminderState{
		New(0),
		Start(1),
		Done(2);
		
		int state;
		ReminderState(int state){
			this.state = state;
		}
		
		public int getState(){
			return state;
		}
		
	}
	
	public static ReminderState fromInt2ReminderState(int state){
		switch (state){
		case 0 :
			return ReminderState.New;
		case 1: 
			return ReminderState.Start;
		case 2: 
			return ReminderState.Done;
		}
		return null;
	}

}
