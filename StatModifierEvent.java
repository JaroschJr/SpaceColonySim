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
		boolean bNeg = false;
		if(super.performEvent(scg, ioman, dbm)){
			bOut = true;
			//the creation of the random number.
			int iOutcome = 0;
			int iHolder = 0;
			double dWorkingDouble1 = 0;
			double dWorkingDouble2 = 0;
			String sToPrint = null;
			Good gGoodWorkedWith = scg.iInv.getGoodByName(StatToMod);
			for(int i = 0; i<iDiceCount; i++){
				iOutcome += 1+rng.nextInt(iDiceSide);
				
			}
			//made it increase or decrease.
			//System.out.println(eThisSign.toString());
			//System.out.println(SCSEnum.eSign.NEGATIVE.toString());
			if(eThisSign == SCSEnum.eSign.NEGATIVE){
				//System.out.println("IT COMES HERE");
				iOutcome = iOutcome * -1;
				bNeg = true;
			}
			//multiply or add?
			
			//if mult.
			if(eHowToFactor == SCSEnum.eFactorType.MULTIPLY){
				dWorkingDouble1 = iOutcome;
				dWorkingDouble2 = dWorkingDouble1/100;
				if(StatToMod.equals("Population")){
					//System.out.println("Increasing Population by "+iOutcome +"percent");
					iOutcome = (int) Math.floor(scg.pop.size()*dWorkingDouble2);
					iHolder = scg.pop.size();
					//System.out.println("Increasing Population "+iOutcome +"people");
				}else{
					//System.out.println("Decreasing Population by "+iOutcome +"percent");
					iOutcome = (int) Math.floor(gGoodWorkedWith.iQuant*dWorkingDouble2);
					//System.out.println("Decreasing Population "+iOutcome +"people");
				}
				
			}
			if(StatToMod.equals("Population")){
				if(iOutcome>0){
					iHolder = scg.pop.size();
					scg.pop.gainPop(iOutcome);
				}else if(iOutcome<0){
					iHolder = scg.pop.size();
					scg.pop.losePop(iOutcome*-1);
				}
			}else{
				if(checkIsValid(iOutcome, gGoodWorkedWith, scg) == false){
					iHolder = gGoodWorkedWith.iQuant;
					gGoodWorkedWith.iQuant = 0;
				}else{
					iHolder = gGoodWorkedWith.iQuant;
					gGoodWorkedWith.iQuant+=iOutcome;
				}
			}
			
			scg.iInv.setGoodQuantByName(StatToMod, gGoodWorkedWith.iQuant);
			
			sToPrint = dbm.getDisplayText(sFluffAccess);
			
				
			if(doesItPrint(StatToMod, iOutcome, iHolder, bNeg)){
				
				if(iOutcome<0){
				iOutcome *= -1;
				}
				
				if(iOutcome>0||iHolder!=0){
					sToPrint = String.format(sToPrint, StatToMod, iOutcome);
					ioman.lineOut(sToPrint);
				}
			}
			
		}//end if
		return bOut;
		
	}
	
	//@Override
	//overloads the parent, rather than overriding it- the origional still exists, per some manner of speaking.
	public boolean checkIsValid(int iMod, Good sModded, SpaceColonyGame tSCG){
		boolean bValid = true;
		if(StatToMod.equals("Population")){
			if((tSCG.pop.size()+iMod)<0){
				bValid = false;
			}
		}else{
			if((iMod + sModded.iQuant)<0){
				bValid = false;
			}
		}
		
		return bValid;
	}
	
	public boolean doesItPrint(String sStat, int iMod, int iHold, boolean bReduce){
		boolean bOut = true;
		//System.out.println("DOES IT PRINT");
		//System.out.println("iHold "+iHold);
		//System.out.println("Reduce " + bReduce);
		if(sStat.equals("TurnCount")){
			bOut = false;
		}else if(iMod == 0){
			bOut = false;
		}else if(iHold==0&&bReduce){
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