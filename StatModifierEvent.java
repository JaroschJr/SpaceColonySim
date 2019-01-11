import java.sql.*;
import java.util.Random;
import java.util.Formatter;
public class StatModifierEvent extends RandomEvent{
	Random rng = new Random();
	String StatToMod;//database: STAT_TYPE
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
				StatToMod = sTypeA;
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
	public boolean performEvent(SpaceColonyGame scg, ISCSIO ioman, SCSDataModule dbm){
		boolean bOut = false;
		if(super.performEvent(scg, ioman, dbm)){
			bOut = true;
			//the creation of the random number.
			int iOutcome = 0;
			double dWorkingDouble1 = 0;
			double dWorkingDouble2 = 0;
			String sToPrint = null;
			Good gGoodWorkedWith = scg.iInv.getGoodByName(StatToMod);
			for(int i = 0; i<iDiceCount; i++){
				iOutcome += 1+rng.nextInt(iDiceSide);
				
			}
			//made it increase or decrease.
			if(eThisSign == SCSEnum.eSign.NEGATIVE){
				iOutcome = iOutcome * -1;
			}
			//multiply or add?
			
			//if mult.
			if(eHowToFactor == SCSEnum.eFactorType.MULTIPLY){
				dWorkingDouble1 = iOutcome;
				dWorkingDouble2 = dWorkingDouble1/100;
				iOutcome = (int) Math.floor(gGoodWorkedWith.iQuant*dWorkingDouble2);	
				
			}
			
			if(checkIsValid(iOutcome, gGoodWorkedWith) == false){
				gGoodWorkedWith.iQuant = 0;
			}else{
				gGoodWorkedWith.iQuant+=iOutcome;
			}
			
			scg.iInv.setGoodQuantByName(StatToMod, gGoodWorkedWith.iQuant);
			
			sToPrint = dbm.getDisplayText(sFluffAccess);
			
				
			if(doesItPrint(StatToMod, iOutcome)){
				
				if(iOutcome<0){
				iOutcome *= -1;
				}
				
				sToPrint = String.format(sToPrint, StatToMod, iOutcome);
				ioman.lineOut(sToPrint);
			}
			
		}//end if
		return bOut;
		
	}
	
	//@Override
	//overloads the parent, rather than overriding it- the origional still exists, per some manner of speaking.
	public boolean checkIsValid(int iMod, Good sModded){
		boolean bValid = true;
		if((iMod + sModded.iQuant)<0){
			bValid = false;
		}
		
		return bValid;
	}
	
	public boolean doesItPrint(String sStat, int iMod){
		boolean bOut = true;
		if(sStat.equals("TurnCount")){
			bOut = false;
		}else if(iMod == 0){
			bOut = false;
		}
		return bOut;
		
	}
	
	@Override
	public String toString(){
		String returnString =  super.toString() + ", STAT_TYPE ";
			returnString = returnString + StatToMod;
			returnString = returnString + ", STAT_SIGN ";
			returnString = returnString + eThisSign.name() + ", STAT_FACTOR ";
			returnString = returnString + eHowToFactor.name() + ", STAT_DICE_QUAN ";
			returnString = returnString + iDiceCount + ", STAT_DIE_SIZE ";
			returnString = returnString + iDiceSide;
		
		return returnString;
		
	}

}