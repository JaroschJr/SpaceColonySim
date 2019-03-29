import java.util.ArrayList;
public class StructureList extends ArrayList<Structure>{
	
	
	public Structure getStructureByName(String name){
		Structure sOut = new Structure();
		for(int i = 0; i<size(); i++){
			if(get(i).NAME.equals(name.toString())){
					//System.out.println(get(i).sName.toString()+" = "+  name.toString());
					sOut = get(i);
			}else{
				
			}
		}
		return sOut;
		
	}
	
	@Override
	public String toString(){
		String sOut = "Structure List toString() \n";
		
		for(int i = 0; i<size(); i++){
			sOut += " Structure no. " + i + " " + get(i).toString() + "\n" ;
		}
		return sOut;
	}
	

}