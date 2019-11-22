//Morale Mannager
public class MoraleManager{

	public int calcObjectiveMorale(SpaceColonyGame scg){
		int morale = 0;
		if(scg.iInv.getGoodByName("Food").iQuant>scg.pop.size()){
			morale += 10;
		}else{
			morale -=10;
		}
		
		if(scg.iInv.getGoodByName("Water").iQuant>scg.pop.size()){
			morale += 15;
		}else{
			morale -=15;
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
		
		System.out.println("Objective Morale " + morale);
		return morale;
	}
	
	public int calcSubjectiveMorale(SpaceColonyGame scg){
		int iOut = scg.subMorale;
		if(scg.obMorale>=50){
			iOut +=5;
		}else if(scg.obMorale>=40){
			iOut +=4;
		}else if(scg.obMorale>=30){
			iOut +=3;
		}else if(scg.obMorale>=20){
			iOut +=2;
		}else if(scg.obMorale>=10){
			iOut +=1;
		}else if(scg.obMorale>=-10){
			iOut +=0;
		}else if(scg.obMorale>=-20){
			iOut -=1;
		}else if(scg.obMorale>=-30){
			iOut -=2;
		}else if(scg.obMorale>=-40){
			iOut -=3;
		}else if(scg.obMorale>=-50){
			iOut -=4;
		}else{
			iOut -=4;
		}
		return iOut;
	}
}