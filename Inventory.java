import java.util.ArrayList;
public class Inventory extends ArrayList<Good>{
	public Good getGoodByName(String name){
		Good gOut = new Good();
		System.out.println("looking for Good named " + name);
		for(int i = 0; i <size(); i++){
			//gOut = get(i);
			if(get(i).sName.equals(name.toString())){
				//System.out.println(get(i).sName.toString()+" = "+  name.toString());
				gOut = get(i);
				System.out.println("Gotten Good " +gOut.sName);
			}else{
				
			}
		}
		return gOut;
	}
	
	public void setGoodByName(String name, Good value){
		Good gCheck;
		for(int i = 0; i <size(); i++){
			gCheck = get(i);
			if(gCheck.sName.equals(name)){
				set(i, value);
			}
		}		
	}	
}