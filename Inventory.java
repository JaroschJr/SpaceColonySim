import java.util.ArrayList;
public class Inventory extends ArrayList<Good>{
	public Good getGoodByName(String name){
		Good gOut = new Good();
		for(int i = 0; i <size(); i++){
			//gOut = get(i);
			if(get(i).sName.equals(name.toString())){
				//System.out.println(get(i).sName.toString()+" = "+  name.toString());
				gOut = get(i);
			}else{
				
			}
		}
		return gOut;
	}
	
	public void setGoodQuantByName(String name, int value){
		Good gCheck;
		for(int i = 0; i <size(); i++){
			gCheck = get(i);
			if(gCheck.sName.equals(name)){
				gCheck.iQuant = value;
			}
		}		
	}	
}