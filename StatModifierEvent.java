import java.sql.*;
public class StatModifierEvent extends RandomEvent{
	SCSEnum.eStatModded eStat;//database: STAT_TYPE
	SCSEnum.eSign eThisSign;//database: STAT_SIGN
	SCSEnum.eFactorType eHowToFactor;//database: STAT_FACTOR
	int iDiceCount;//database: STAT_DICE_QUAN
	int iDiceSide;//database: STAT_DICE_SIZE

	
	//Constuctor
	public StatModifierEvent(){
		
	}
	
	
	public String toString(){
		String returnString =  " STAT_TYPE " + eStat.name() + ", STAT_SIGN " + eThisSign.name() + ", STAT_FACTOR " + eHowToFactor.name() + ", STAT_DICE_QUAN " + iDiceCount + ", STAT_DICE_SIZE " + iDiceSide + super.toString();
		
		return returnString;
		
	}

}