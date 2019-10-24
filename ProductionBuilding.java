import java.util.ArrayList;
public class ProductionBuilding extends Structure{
	ArrayList<String> sPosibleRecipes;//names of the various recipies, so as to point to them.
	Recipe currentRecipe;
	
	@Override
	public String toString(){
		
		String sOut = super.toString();
		sOut += "Possible Rcipes: ";
		
		for(int i = 0; i<sPosibleRecipes.size(); i++){
			sOut += " -  Possible Recipe no." + i + " " + sPosibleRecipes.get(i).toString() ;
		}
		
		if(currentRecipe != null){
			sOut += " Current Recipe " + currentRecipe.toString();
		}else{
			sOut += "_";
		}
		
		return sOut;
	}
	
	@Override
	public Structure clone(){
		ProductionBuilding sOut = new ProductionBuilding();
		cloneStructure(sOut);
		sOut.sPosibleRecipes = sPosibleRecipes;
		
		return sOut;
	}
	
	//@Override
	public void setWork(Population pop, int workers, Recipe r){
		setWork(pop, workers);
		if(iWorkers != 0){
			//System.out.println("New Recipe " +r.NAME);
			currentRecipe = r;
		}
		
		if(iWorkers == 0){
			currentRecipe = null;
		}
		bBuildingSelf = false;
		
	}
	
	@Override
	public void doProduction(SpaceColonyGame scg, RandomEvent cEvent){
		//super.doProduction();
		if(bBuildingSelf){
			buildSelf();
			if(bComplete == true){
				setWork(scg.pop, 0, null);
			}
		}else if(currentRecipe !=null){
			int times = 0;
			if(cEvent instanceof ProductionMultiplyingEvent){
				ProductionMultiplyingEvent tEvent = (ProductionMultiplyingEvent) cEvent;
				
				//System.out.println(currentRecipe.toString());
				if(currentRecipe.TEXT_CODE.equals( tEvent.sTargetProduction)){
					//System.out.println("Before the event bPrint was " + tEvent.bPrint);
					tEvent.bPrint = true;
					//System.out.println("And then it was " + tEvent.bPrint);
					
					times = (int) Math.round((iWorkers/currentRecipe.MAN_HOURS)*tEvent.dProdMultFactor);
				}else{
					//System.out.println("Before the evennt bPrint was " + tEvent.bPrint);
					//tEvent.bPrint = false;
					//System.out.println("And thenn it was " + tEvent.bPrint);
					times = (int) (iWorkers/currentRecipe.MAN_HOURS);
				}
				//System.out.println("The event is " + cEvent.toString());
				
				
			}else{
				//System.out.println("Building Self "+bBuildingSelf);
				//System.out.println(currentRecipe.toString());
				//System.out.println(currentRecipe.MAN_HOURS);
				times = (int) (iWorkers/currentRecipe.MAN_HOURS);
			}
			for(int i =0; i < times; i++ ){
				execRecipe(scg);
			}
		}
		
	}
	
	@Override
	public void assignValidate(){
		super.assignValidate();
		if(iWorkers == 0){
			currentRecipe = null;
		}
	}
	
	public void execRecipe(SpaceColonyGame scg){
		boolean enoughMats = true;
		Good goodBeingCompared;
		for(int i = 0; i<currentRecipe.size(); i++){
			goodBeingCompared = scg.iInv.getGoodByName(currentRecipe.get(i).sName);
			enoughMats = (enoughMats &&(goodBeingCompared.iQuant >= currentRecipe.get(i).iQuant));
		}
		
		if(enoughMats){
			goodBeingCompared = scg.iInv.getGoodByName(currentRecipe.NAME);
			goodBeingCompared.iQuant++;
			for(int i = 0; i<currentRecipe.size(); i++){
				goodBeingCompared = scg.iInv.getGoodByName(currentRecipe.get(i).sName);
				goodBeingCompared.iQuant -= currentRecipe.get(i).iQuant;
			}
		}	
	}


	

}