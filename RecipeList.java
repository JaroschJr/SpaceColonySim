import java.util.ArrayList;
public class RecipeList extends ArrayList<Recipe>{
	
	public Recipe getRecipeByName(String name){
		Recipe rOut = new Recipe();
		for(int i = 0; i <size(); i++){
			//gOut = get(i);
			if(get(i).NAME.equals(name.toString())){
				//System.out.println(get(i).sName.toString()+" = "+  name.toString());
				rOut = get(i);
			}else{
				
			}
		}
		return rOut;
	}

}