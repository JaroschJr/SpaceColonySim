import java.util.ArrayList;
public class ProductionBuilding extends Structure{
	ArrayList<String> sPosibleRecipes;//names of the various recipies, so as to point to them.
	
	@Override
	public String toString(){
		
		String sOut = super.toString();
		sOut += "Rcipes: ";
		
		for(int i = 0; i<sPosibleRecipes.size(); i++){
			sOut += " -  Possible Recipe no." + i + " " + sPosibleRecipes.get(i).toString() ;
		}
		
		return sOut;
	}
	
	@Override
	public ProductionBuilding clone(){
		ProductionBuilding sOut = new ProductionBuilding();
		sOut.ID = ID;
		sOut.NAME = NAME;
		sOut.TEXT_CODE = TEXT_CODE;
		sOut.MAX_WORKERS = MAX_WORKERS;
		sOut.sPosibleRecipes = sPosibleRecipes;
		
		return sOut;
	}
	

}