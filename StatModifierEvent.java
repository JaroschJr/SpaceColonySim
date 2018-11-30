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
		double dWorkingDouble1 = 0;
		double dWorkingDouble2 = 0;
		String sToPrint = null;
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
			
			switch(eStat){
				case Population:
					dWorkingDouble1 = iOutcome;
					dWorkingDouble2 = dWorkingDouble1/100;
					iOutcome = (int) Math.floor(scg.iPopulation*dWorkingDouble2);
					scg.iPopulation += iOutcome;
					break;
				case Money:
					dWorkingDouble1 = iOutcome;
					dWorkingDouble2 = dWorkingDouble1/100;
					iOutcome = (int) Math.floor(scg.iMoney*dWorkingDouble2);
					scg.iMoney += iOutcome;
					break;
				case Ore:
					dWorkingDouble1 = iOutcome;
					dWorkingDouble2 = dWorkingDouble1/100;
					iOutcome = (int) Math.floor(scg.iOre*dWorkingDouble2);
					scg.iOre += iOutcome;
					break;
				case Silicon:
					dWorkingDouble1 = iOutcome;
					dWorkingDouble2 = dWorkingDouble1/100;
					iOutcome = (int) Math.floor(scg.iSilicon*dWorkingDouble2);
					scg.iSilicon += iOutcome;
					break;
				case Food:
					dWorkingDouble1 = iOutcome;
					dWorkingDouble2 = dWorkingDouble1/100;
					iOutcome = (int) Math.floor(scg.iFood*dWorkingDouble2);
					scg.iFood += iOutcome;
					break;
				case Ice:
					dWorkingDouble1 = iOutcome;
					dWorkingDouble2 = dWorkingDouble1/100;
					iOutcome = (int) Math.floor(scg.iIce*dWorkingDouble2);
					scg.iIce += iOutcome;
					break;
				case Water:
					dWorkingDouble1 = iOutcome;
					dWorkingDouble2 = dWorkingDouble1/100;
					iOutcome = (int) Math.floor(scg.iWater*dWorkingDouble2);
					scg.iWater += iOutcome;
					break;
				case TurnCount: 
					dWorkingDouble1 = iOutcome;
					dWorkingDouble2 = dWorkingDouble1/100;
					iOutcome = (int) Math.floor(scg.iMerchantCountDown*dWorkingDouble2);
					scg.iMerchantCountDown += iOutcome;
					break;
			}
			
		
			//add is default
		}else{
		//a swich statement for picking the stat.
			switch(eStat){
				case Population: scg.iPopulation += iOutcome;
					break;
				case Money: scg.iMoney += iOutcome;
					break;
				case Ore: scg.iOre += iOutcome;
					break;
				case Silicon: scg.iSilicon += iOutcome;
					break;
				case Food: scg.iFood +=iOutcome;
					break;
				case Ice: scg.iIce += iOutcome;
					break;
				case Water: scg.iWater+=iOutcome;
					break;
				case TurnCount: scg.iMerchantCountDown += iOutcome;
			}
			
			//Now the Fluff.
			
			
		}
		sToPrint = dbm.getDisplayText(sFluffAccess);
		if(iOutcome<0){
			iOutcome *= -1;
		}
		sToPrint = String.format(sToPrint,eStat.name(), iOutcome);
		/*if(eHowToFactor == SCSEnum.eFactorType.MULTIPLY){
			sToPrint = String.format(sToPrint,eStat.name(), iOutcome);
			}else{
			sToPrint = String.format(sToPrint,eStat.name(), iOutcome);
			}
		*/
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