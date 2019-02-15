public class Structure{
	public static final String FIELD_ID = "ID";
	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_TEXT_CODE = "TEXT_CODE";
	public static final String FIELD_TYPE = "TYPE";
	public static final String FIELD_NEED_WORKERS="NEED_WORKERS";
	public static final String FIELD_MAX_WORKERS = "MAX_WORKERS";
	public static final String FIELD_BENEFIT = "BENEFIT";

	String ID;
	String NAME;
	String TEXT_CODE;
	int iWorkers;
	int MAX_WORKERS;
	
	//reading from db is handled in the factory for it.
	
	
	//some way of tracking the rescorces needed
	//and outputs.
	//and the recepies it can make.
	@Override
	public String toString(){
		return ID+" "+NAME+" "+TEXT_CODE+"  MAX_WORKERS "+MAX_WORKERS +" ";
	}
	
	public Structure clone(){//there will never be a Structure that is not of a subclass, so this is just to demonstrate that they will have one.
		
		return null;
	}
	
	/*
	public Structure clone(){
		Structure sOut = new Structure();
		sOut.ID = ID;
		sOut.NAME = NAME;
		sOut.TEXT_CODE = TEXT_CODE;
		sOut.MAX_WORKERS = MAX_WORKERS;
		return sOut;
	}
	*/
}