import java.util.ArrayList;
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
	ArrayList<People> pWorkers = new ArrayList<People>();
	
	
	//reading from db is handled in the factory for it.
	
	
	//some way of tracking the rescorces needed
	//and outputs.
	//and the recepies it can make.
	@Override
	public String toString(){
		String sOut = ID+" "+NAME+" "+TEXT_CODE+"  MAX_WORKERS "+MAX_WORKERS +" Workers " + pWorkers.size() +"\n";
		for(int i = 0; i<pWorkers.size(); i++){
			sOut += pWorkers.get(i).toString();
		}
		return sOut;
	}
	
	public Structure clone(){//there will never be a Structure that is not of a subclass, so this is just to demonstrate that they will have one.
		Structure s = new Structure();
		cloneStructure(s);
		
		return s;
	}
	
	public void setWork(Population pop, int workers){
		for(int i = pWorkers.size() -1; i>=0; i--){
			pWorkers.get(i).assigned = false;
			pWorkers.remove(i);
		}
		
		if(workers>=MAX_WORKERS){
			workers = MAX_WORKERS;
		}
		
		if(workers>pop.howManyUnassigned()){
			workers = pop.howManyUnassigned();
		}
		
		iWorkers = workers;
		
	
		
		for(int i = 0; i<workers; i++){
			pWorkers.add(pop.firstUnassigned());
			pWorkers.get(i).assigned = true;
				
		}
			
		
	}
	
	public void doProduction(SpaceColonyGame scg){
		
	}
	
	public void cloneStructure(Structure s){
		s.ID = ID;
		s.NAME = NAME;
		s.TEXT_CODE = TEXT_CODE;
		s.MAX_WORKERS = MAX_WORKERS;
	}
	
	
}