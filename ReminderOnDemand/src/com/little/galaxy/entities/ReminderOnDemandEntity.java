package com.little.galaxy.entities;

public class ReminderOnDemandEntity {
	
	private long id;
	private String name;
	private String recoredLoc;
	private long createTime;
	private long execTime;
	private int interval;
	private int frenquecy;
	private ReminderState state;
	
	
	public ReminderOnDemandEntity(long id, String name, String recoredLoc,
			long createTime, long execTime, int interval, int frenquecy, ReminderState state) {
		super();
		this.id = id;
		this.name = name;
		this.recoredLoc = recoredLoc;
		this.createTime = createTime;
		this.execTime = execTime;
		this.interval = interval;
		this.frenquecy = frenquecy;
		this.state = state;
	}
	
	public ReminderOnDemandEntity(long id, String name, String recoredLoc,
			long createTime, int interval, int frenquecy, ReminderState state) {
		super();
		this.id = id;
		this.name = name;
		this.recoredLoc = recoredLoc;
		this.createTime = createTime;
		this.interval = interval;
		this.frenquecy = frenquecy;
		this.state = state;
	}
	
	public ReminderOnDemandEntity(long id, String name, String recoredLoc,
			long createTime, int interval, int frenquecy, int state) {
		super();
		this.id = id;
		this.name = name;
		this.recoredLoc = recoredLoc;
		this.createTime = createTime;
		this.interval = interval;
		this.frenquecy = frenquecy;
		this.state = fromInt2ReminderState(state);
	}
	
	
	
	public ReminderOnDemandEntity(long id, String name, String recoredLoc,
			long createTime, long execTime, int interval, int frenquecy, int state) {
		super();
		this.id = id;
		this.name = name;
		this.recoredLoc = recoredLoc;
		this.createTime = createTime;
		this.execTime = execTime;
		this.interval = interval;
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
	public String getRecoredLoc() {
		return recoredLoc;
	}
	public void setRecoredLoc(String recoredLoc) {
		this.recoredLoc = recoredLoc;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
	public long getExecTime() {
		return execTime;
	}

	public void setExecTime(long execTime) {
		this.execTime = execTime;
	}

	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
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
	    sb.append(" Reminder Interval: " + this.getInterval());
	    return sb.toString();
	}


	public enum ReminderState{
		New(0),
		Start(1),
		Done(2),
		Cancel(3);
		
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
		case 3:
			return ReminderState.Cancel;
		}
		return null;
	}

}
