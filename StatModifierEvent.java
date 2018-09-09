import java.sql.*;
public class StatModifierEvent extends RandomEvent{
	SCSEnum.eStatModded eStat;//database: STAT_TYPE
	SCSEnum.eSign eThisSign;//database: STAT_SIGN
	SCSEnum.eFactorType eHowToFactor;//database: STAT_FACTOR
	int iDiceCount;//database: STAT_DICE_QUAN
	int iDiceSide;//database: STAT_DIE_SIZE
	
	public static final String TYPE = "STAT_TYPE";
	public static final String SIGN = "STAT_SIGN";
	public static final String FACT = "STAT_FACTOR";

	
	//Constuctor
	public StatModifierEvent(){
		
	}
	
	
	@Override
	public void readFromDB(ResultSet resultSet)throws SQLException{
		super.readFromDB(resultSet);
		
		String sTypeA = resultSet.getString(TYPE);
			if(sTypeA != null){
				eStat = SCSEnum.eStatModded.valueOf(sTypeA);
			}
		
		String sTypeB = resultSet.getString(SIGN);
			if(sTypeB != null){
				eThisSign = SCSEnum.eSign.valueOf(sTypeB);
			}			
		
		String sTypeC = resultSet.getString(FACT);
			if(sTypeC != null){
				eHowToFactor = SCSEnum.eFactorType.valueOf(sTypeC);
			}
		
		iDiceCount = resultSet.getInt("STAT_DICE_QUAN");
		iDiceSide = resultSet.getInt("STAT_DIE_SIZE");
		
	}
	
	@Override
	public String toString(){
		String returnString =  super.toString() + ", STAT_TYPE ";
			returnString = returnString + eStat.name();
			returnString = returnString + ", STAT_SIGN ";
			returnString = returnString + eThisSign.name() + ", STAT_FACTOR ";
			returnString = returnString + eHowToFactor.name() + ", STAT_DICE_QUAN ";
			returnString = returnString + iDiceCount + ", STAT_DIE_SIZE ";
			returnString = returnString + iDiceSide;
		
		return returnString;
		
	}

}