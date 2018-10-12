import java.sql.*;
import java.util.Random;
import java.util.Formatter;
public class StatModifierEvent extends RandomEvent{
	Random rng = new Random();
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
	public void performEvent(SpaceColonyGame scg, ISCSIO ioman, SCSDataModule dbm){
		//te creation of the random number.
		int iOutcome = 0;
		double dOutcome = 0;
		String sToPrint = null;
		for(int i = 0; i<iDiceCount; i++){
			iOutcome += 1+rng.nextInt(iDiceSide);
			
		}
		//made it increase or decrease.
		if(eThisSign == SCSEnum.eSign.NEGATIVE){
			iOutcome += iOutcome * -1;
		}
		//multiply or add?
		
		//if mult.
		if(eHowToFactor == SCSEnum.eFactorType.MULTIPLY){
			dOutcome = iOutcome;
			dOutcome = 1 + dOutcome/100;
			
			if(eStat == SCSEnum.eStatModded.Population){
				scg.iPopulation *= iOutcome; 
			}else if(eStat == SCSEnum.eStatModded.Money){
				scg.iMoney *= iOutcome; 
			}else if(eStat == SCSEnum.eStatModded.Ore){
				scg.iOre *= iOutcome; 
			}else if(eStat == SCSEnum.eStatModded.Silicon){
				scg.iSilicon *= iOutcome; 
			}else{//
				scg.iMerchantCountDown += iOutcome; 
			}
		
			//add is default
		}else{
		//an if loop for picking the stat.
			if(eStat == SCSEnum.eStatModded.Population){
				scg.iPopulation += iOutcome; 
			}else if(eStat == SCSEnum.eStatModded.Money){
				scg.iMoney += iOutcome; 
			}else if(eStat == SCSEnum.eStatModded.Ore){
				scg.iOre += iOutcome; 
			}else if(eStat == SCSEnum.eStatModded.Silicon){
				scg.iSilicon += iOutcome; 
			}else{//
				scg.iMerchantCountDown += iOutcome; 
			}
			//Now the Fluff.
			
			
		}
		sToPrint = dbm.getDisplayText(sFluffAccess);
		if(eHowToFactor == SCSEnum.eFactorType.MULTIPLY){
			sToPrint = String.format(sToPrint,eStat.name(), dOutcome);
			}else{
			sToPrint = String.format(sToPrint,eStat.name(), iOutcome);
		}
		ioman.lineOut(sToPrint);
		
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