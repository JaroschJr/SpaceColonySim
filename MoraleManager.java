//Morale Mannager
import java.util.Random;
public class MoraleManager{

	public int calcObjectiveMorale(SpaceColonyGame scg, RandomEvent rLastRand){
		int morale = 0;
		int enoughFoodBonus = 10;
		int enoughWaterBonus = 15;
		int outOfWaterPenalty = 50;
		int outOfWaterPopMult = 3;
		int happynessPerEntertainer = 2;
		int notProducingPenalty = 10;
		int perUnassigned = 2;
		int minMorale = 20;
		int maxMorale = 80;
		int artBounus = 1;
		int artBonusCap = 5;
		
		int moraleBallancingForce = 1;
		//first factors related to materiel stockpiles.
		morale -= scg.pop.size();
		
		if(scg.iInv.getGoodByName("Food").iQuant>scg.pop.size()*2){
			morale += enoughFoodBonus;
		}else if(scg.iInv.getGoodByName("Food").iQuant<scg.pop.size()){
			morale -=enoughFoodBonus;
		}else{
			
		}
		
		if(scg.iInv.getGoodByName("Food").iQuant<=0){
			morale -=scg.pop.size();
		}
		
		if(scg.iInv.getGoodByName("Water").iQuant>scg.pop.size()*2){
			morale += enoughWaterBonus;
		}else if(scg.iInv.getGoodByName("Water").iQuant<scg.pop.size()){
			morale -=enoughWaterBonus;
		}else{
			
		}
		
		if(scg.iInv.getGoodByName("Art").iQuant>0){
			if(scg.iInv.getGoodByName("Art").iQuant<=artBonusCap){
				morale += scg.iInv.getGoodByName("Art").iQuant*artBounus;
			}else{
				morale = artBonusCap*artBounus;
			}
			
		}
		
		if(scg.iInv.getGoodByName("Water").iQuant<=0){
			morale -=outOfWaterPenalty;
			morale -= scg.pop.size()*outOfWaterPopMult;
		}
		
		//now things that relate to structures directly.
		int iFarmers = 0;
		int iWaterPurifiers = 0;
		for(int i = 0; i<scg.structures.size(); i++){//first count how many wokrers there are.
			if(scg.structures.get(i).NAME.equals("entertainment")){
				morale +=happynessPerEntertainer*scg.structures.get(i).iWorkers;
			}
			
			if(scg.structures.get(i).NAME.equals("farm")){
				iFarmers += scg.structures.get(i).iWorkers;
			}
			
			if(scg.structures.get(i).NAME.equals("waterPurifier")){
				iWaterPurifiers += scg.structures.get(i).iWorkers;
			}
		}
		//then applies the relavent things.
		if(iFarmers == 0){
			morale -= notProducingPenalty;
		}
		
		if(iWaterPurifiers<scg.pop.size()/6){
			morale -= notProducingPenalty;
		}
		
		if(iWaterPurifiers ==0){
			morale -=notProducingPenalty;
		}
		
		//and it checks if more than half of the population is idle.
		if(scg.pop.size()>2*scg.pop.howManyAssigned()){
			morale -= perUnassigned*(scg.pop.size()-2*scg.pop.howManyAssigned());
		}
		
		if(scg.subMorale >=maxMorale){
			morale -=moraleBallancingForce*(scg.subMorale-maxMorale);//so as go give the morale dimishing returns upon reaching a certain threshold
		}
		
		if(scg.subMorale <=minMorale){
			morale +=moraleBallancingForce*(maxMorale-scg.subMorale);//Likewise in reverse
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
		
		if(scg.iInv.getGoodByName("Art").iQuant>0){
			sOut += _scsdm.getDisplayText("ART_MORALE_REPORT") + "\n";;
			
		}
		
		
		for(int i = 0; i<scg.structures.size(); i++){
			if(scg.structures.get(i).NAME.equals("entertainment")){
				//morale +=2*scg.structures.get(i).iWorkers;
			}
		}
		
		//now things that relate to structures directly.
		int iFarmers = 0;
		int iWaterPurifiers = 0;
		for(int i = 0; i<scg.structures.size(); i++){
			
			if(scg.structures.get(i).NAME.equals("farm")){
				iFarmers += scg.structures.get(i).iWorkers;
			}
			
			if(scg.structures.get(i).NAME.equals("waterPurifier")){
				iWaterPurifiers += scg.structures.get(i).iWorkers;
			}
		}
		if(iFarmers == 0){
			sOut+= _scsdm.getDisplayText("MORALE_NOT_PORDUCING_FOOD") + "\n";
		}
		
		if(iWaterPurifiers<scg.pop.size()/6){
			sOut+= _scsdm.getDisplayText("MORALE_NOT_PORDUCING_WATER") + "\n";
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