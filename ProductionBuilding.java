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
		
		sOut += " Current Recipe " + currentRecipe.toString();
		
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
			currentRecipe = r;
		}
		
		if(iWorkers == 0){
			currentRecipe = null;
		}
		
	}
	
	@Override
	public void doProduction(SpaceColonyGame scg, RandomEvent cEvent){
		if(currentRecipe !=null){
			int times = 0;
			if(cEvent instanceof ProductionMultiplyingEvent){
				ProductionMultiplyingEvent tEvent = (ProductionMultiplyingEvent) cEvent;
				if(currentRecipe.TEXT_CODE.equals( tEvent.sTargetProduction)){
					times = (int) Math.round((iWorkers/currentRecipe.MAN_HOURS)*tEvent.dProdMultFactor);
				}
			}else{
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