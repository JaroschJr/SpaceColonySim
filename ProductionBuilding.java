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
	public void doProduction(SpaceColonyGame scg){
		if(currentRecipe !=null){
			for(int i =0; i < (iWorkers/currentRecipe.MAN_HOURS); i++ ){
				execRecipe(scg);
			}
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