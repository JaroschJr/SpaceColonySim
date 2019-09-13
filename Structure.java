import java.util.ArrayList;
public class Structure{
	public static final String FIELD_ID = "ID";
	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_TEXT_CODE = "TEXT_CODE";
	public static final String FIELD_TYPE = "TYPE";
	public static final String FIELD_NEED_WORKERS="NEED_WORKERS";
	public static final String FIELD_MAX_WORKERS = "MAX_WORKERS";
	public static final String FIELD_BENEFIT = "BENEFIT";
	public static final String FIELD_HOURS_TO_BUILD = "HOURS_TO_BUILD";

	String ID;
	String NAME;
	String TEXT_CODE;
	boolean bComplete;
	boolean bBuildingSelf;
	int iWorkers;
	int MAX_WORKERS;
	int HOURS_TO_BUILD;
	int iCompleteness;
	
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
		System.out.println("It gets to 1");
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
	
	public void buildSelf(){
		if(bComplete !=true){
			iCompleteness+=iWorkers;
			if(iCompleteness >=HOURS_TO_BUILD){
				iCompleteness = HOURS_TO_BUILD;
				bComplete = true;
				bBuildingSelf = false;
			}
		}
	}
	
	public void doProduction(SpaceColonyGame scg, RandomEvent cEvent){
		
	}
	
	public void assignValidate(){
		//System.out.println("Pre Validation: "+pWorkers.toString());
		for(int i = pWorkers.size() -1; i>=0; i--){
			if(pWorkers.get(i).assigned == false){
				pWorkers.remove(i);
			}
		}
		iWorkers = pWorkers.size();
		//System.out.println("post Validation: "+pWorkers.toString());
	}
	
	public void cloneStructure(Structure s){
		s.ID = ID;
		s.NAME = NAME;
		s.TEXT_CODE = TEXT_CODE;
		s.MAX_WORKERS = MAX_WORKERS;
		s.HOURS_TO_BUILD = HOURS_TO_BUILD;
		s.iCompleteness = HOURS_TO_BUILD;
		s.bComplete = true;
	}
	
	public String howDone(){
		double dPercent = (double) iCompleteness / (double) HOURS_TO_BUILD;
		dPercent *= 100;
		int iRoundedPercent = (int) dPercent;
		String sOut = iRoundedPercent + "%";
		return sOut;
	}
	
}