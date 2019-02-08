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
}