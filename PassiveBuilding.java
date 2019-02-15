public class PassiveBuilding extends Structure{
	//a set of strings corresponding to DataBase positions.
	boolean NEED_WORKERS;

	SCSEnum.eBenefit BENEFIT;
	//some method of checking and applying it. Possibly here, possibly elsewhere.
	@Override
	public String toString(){
		String sOut = super.toString();
		sOut += " NEED_WORKERS " + NEED_WORKERS + " BENNEFIT " + BENEFIT + " ";
		return sOut;
	}
	
	@Override
	public PassiveBuilding clone(){
		PassiveBuilding sOut = new PassiveBuilding();
		sOut.ID = ID;
		sOut.NAME = NAME;
		sOut.TEXT_CODE = TEXT_CODE;
		sOut.MAX_WORKERS = MAX_WORKERS;
		sOut.NEED_WORKERS = NEED_WORKERS;
		sOut.BENEFIT = BENEFIT;
		
		return sOut;
	}
}