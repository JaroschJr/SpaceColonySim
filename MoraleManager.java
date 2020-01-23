//Morale Mannager
import java.util.Random;
public class MoraleManager{

	public int calcObjectiveMorale(SpaceColonyGame scg, RandomEvent rLastRand){
		int morale = 0;
		if(scg.iInv.getGoodByName("Food").iQuant>scg.pop.size()*2){
			morale += 10;
		}else if(scg.iInv.getGoodByName("Food").iQuant<scg.pop.size()){
			morale -=10;
		}else{
			
		}
		
		if(scg.iInv.getGoodByName("Food").iQuant<=0){
			morale -=scg.pop.size();
		}
		
		if(scg.iInv.getGoodByName("Water").iQuant>scg.pop.size()*2){
			morale += 15;
		}else if(scg.iInv.getGoodByName("Water").iQuant<scg.pop.size()){
			morale -=15;
		}else{
			
		}
		
		if(scg.iInv.getGoodByName("Water").iQuant<=0){
			morale -=50;
			morale -= scg.pop.size()*3;
		}
		
		for(int i = 0; i<scg.structures.size(); i++){
			if(scg.structures.get(i).NAME.equals("entertainment")){
				morale +=2*scg.structures.get(i).iWorkers;
			}
		}
		if(scg.pop.size()>2*scg.pop.howManyAssigned()){
			morale -= (scg.pop.size()-2*scg.pop.howManyAssigned())*2;
		}
		
		if(scg.subMorale >=80){
			morale -=(scg.subMorale-80);
		}
		
		if(scg.subMorale <=20){
			morale +=(20-scg.subMorale);
		}
		morale += rLastRand.iLastEffect;
		//System.out.println("The Morale Effect of the Random Event is "+rLastRand.iLastEffect);
		//System.out.println("Objective Morale " + morale);
		return morale;
	}
	
	public int calcSubjectiveMorale(SpaceColonyGame scg){
		Random rand = new Random();
		int iOut = scg.subMorale;
		if(scg.obMorale>=50){
			iOut +=5+rand.nextInt(3)-1;
		}else if(scg.obMorale>=40){
			iOut +=4+rand.nextInt(3)-1;
		}else if(scg.obMorale>=30){
			iOut +=3+rand.nextInt(3)-1;
		}else if(scg.obMorale>=20){
			iOut +=2+rand.nextInt(3)-1;
		}else if(scg.obMorale>=10){
			iOut +=1+rand.nextInt(3)-1;
		}else if(scg.obMorale>=-10){
			iOut +=0+rand.nextInt(3)-1;
		}else if(scg.obMorale>=-20){
			iOut -=1+rand.nextInt(3)-1;
		}else if(scg.obMorale>=-30){
			iOut -=2+rand.nextInt(3)-1;
		}else if(scg.obMorale>=-40){
			iOut -=3+rand.nextInt(3)-1;
		}else if(scg.obMorale>=-50){
			iOut -=4+rand.nextInt(3)-1;
		}else{
			iOut -=5+rand.nextInt(3)-1;
		}
		return iOut;
	}
	
	public String moraleReport(SpaceColonyGame scg, SCSDataModule _scsdm){
		String sOut = new String();
		//int morale = 0;
		if(scg.iInv.getGoodByName("Food").iQuant>scg.pop.size()*2){
			
			sOut += _scsdm.getDisplayText("MORALE_REPORT_FOOD_STOCKPILE_GOOD")+ "\n";
		}else if(scg.iInv.getGoodByName("Food").iQuant<scg.pop.size()){
			sOut += _scsdm.getDisplayText("MORALE_REPORT_FOOD_STOCKPILE_BAD")+ "\n";
		}
		
		if(scg.iInv.getGoodByName("Water").iQuant>scg.pop.size()*2){
			sOut += _scsdm.getDisplayText("MORALE_REPORT_WATER_STOCKPILE_GOOD") + "\n";
		}else if(scg.iInv.getGoodByName("Water").iQuant<scg.pop.size()){
			sOut += _scsdm.getDisplayText("MORALE_REPORT_WATER_STOCKPILE_BAD") + "\n";
		}
		
		for(int i = 0; i<scg.structures.size(); i++){
			if(scg.structures.get(i).NAME.equals("entertainment")){
				//morale +=2*scg.structures.get(i).iWorkers;
			}
		}
		if(scg.pop.size()>2*scg.pop.howManyAssigned()){
			sOut += _scsdm.getDisplayText("MORALE_REPORT_IDLE_WORKER_PENALTY") + "\n";
		}
		
		if(scg.subMorale >=80){
			sOut += _scsdm.getDisplayText("MORALE_REPORT_HIGH_MORALE_PENALTY") + "\n";
		}
		
		if(scg.subMorale <=20){
			sOut += _scsdm.getDisplayText("MORALE_REPORT_LOW_MORALE_BONUS") + "\n";
		}
		return sOut;
	}
}