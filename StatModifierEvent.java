public class StatModifierEvent extends RandomEvent{
	SCSEnum.eStatModded eStat;//database: STAT_TYPE
	SCSEnum.eSign eThisSign;//database: STAT_SIGN
	SCSEnum.eFactorType eHowToFactor;//database: STAT_FACTOR
	int iDiceCount;//database: STAT_DICE_QUAN
	int iDiceSide;//database: STAT_DICE_SIZE

	
	//Constuctor
	public StatModifierEvent(){
		
	}

}