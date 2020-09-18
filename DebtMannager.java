import java.lang.Math;
import java.util.ArrayList;
public class DebtMannager{
	int iMinPayment;
	int iMissedMinFine = 25;
	double dInterestRate = 1.005;
	
	
	public void applyInterest(SpaceColonyGame scg){
		double iMoney = scg.iDebt;
		//System.out.println("debt was " + iMoney);
		if(iMoney>0){
			scg.iDebt = (int) Math.round(iMoney * dInterestRate);
		}
		//System.out.println("and now it is "+ scg.iDebt);
	}
	
	public boolean payDebt(SpaceColonyGame scg, SCSDataModule scsdm, ISCSIO ioman){//The value is weather or not the minimum payment was made
		iMinPayment = (int) (scg.iDebt*dInterestRate-scg.iDebt)/2;
		boolean bOngoing = true;
		boolean bMinPayed = false;
		String sTemp = "";
		// print: your debt is ___
		sTemp = scsdm.getDisplayText("DEBT_IS");
		sTemp = String.format(sTemp, scg.iDebt);
		ioman.lineOut(sTemp);
		// Print: minimum payment is ____
		sTemp = scsdm.getDisplayText("MIN_PAY");
		sTemp = String.format(sTemp, iMinPayment);
		ioman.lineOut(sTemp);
		// Print: You have ___ Money
		sTemp = scsdm.getDisplayText("YOU_HAVE");
		sTemp = String.format(sTemp, scg.iInv.getGoodByName("Money").iQuant, scsdm.getDisplayText("MONEY"));
		ioman.lineOut(sTemp);
		while( bOngoing ){
			// print: Pay how much?
			sTemp = scsdm.getDisplayText("PAY_WHAT");
			//sTemp = String.format(sTemp);
			// take input.
			int iInput = 0;
			iInput = ioman.intIn(sTemp);
			if(iInput>scg.iInv.getGoodByName("Money").iQuant){
				sTemp = scsdm.getDisplayText("CANT_AFFORD");
				//sTemp = String.format(sTemp);
				ioman.lineOut(sTemp);
			}else if(iInput>=iMinPayment){
				
				scg.iInv.getGoodByName("Money").iQuant -=iInput;
				scg.iDebt -= iInput;
				scg.iDebtMissCount = 0;
				bOngoing = false;
				bMinPayed = true;
				
			}else if(iInput >= scg.iDebt){
				iInput = scg.iDebt;
				
				scg.iInv.getGoodByName("Money").iQuant -=iInput;
				scg.iDebt -= iInput;
				scg.iDebtMissCount = 0;
				bOngoing = false;
				bMinPayed = true;
			
			}else if(iInput<iMinPayment){
				sTemp = scsdm.getDisplayText("PAY_BELOW_MINIMUM");
				sTemp = String.format(sTemp, iMissedMinFine);
				ioman.lineOut(sTemp);
				String sIn = System.console().readLine();
				sIn = sIn.toUpperCase();
				if(sIn.equals("Y")){
					scg.iInv.getGoodByName("Money").iQuant -=iInput;
					scg.iDebt -= iInput;
					scg.iDebt+=iMissedMinFine;
					scg.iDebtMissCount++;
					bOngoing= false;
				}else{
					
				}
				
			}
			//if input> money
				//Insuffucient funds
			//if input >= minimum
				//money -= input
				//debt -= input
				//bMinPayed = true;
				//bOngoing = false;
			//else if input < minimum
				//Are you sure y/n?
				//if y
				//bonging = false
				//money -= input
				//debt -= inut
				//debt += iMissedMinFine
				//bonging false
				//else if n
					//nothing
				//
			//
		}
		return bMinPayed;
			
		
	}
	
	
	
	
}