public class StatModifierEvent extends RandomEvent{
	SCSEnum.eStatModded eStat;
	SCSEnum.eSign eThisSign;
	SCSEnum.eFactorType eHowToFactor; //If true, this is rolling for a percentage increase or decrese. If false, than it is for absolute value.
	int iDiceCount;
	int iDiceSide;

	
	//Constuctor
	public StatModifierEvent(){
		
	}

}