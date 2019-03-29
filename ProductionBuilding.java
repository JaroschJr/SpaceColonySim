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
		
	}
	
	public void produce(){
		
	}


	

}