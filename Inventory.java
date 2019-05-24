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
	
	public boolean hasGoodByName(String name){
		boolean b = false;
		for(int i = 0; i <size(); i++){
			//gOut = get(i);
			if(get(i).sName.equals(name.toString())){
				//System.out.println(get(i).sName.toString()+" = "+  name.toString());
				b = true;
				break;
			}else{
				
			}
		}
		return b;
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
	
	public int getTotalQuant(){
		int iSum = 0;
		for(int i = 0; i <size(); i++){
			iSum += get(i).iQuant;
		}
		return iSum;
	}
	
	public int merchFreqSum(){
		int iSum = 0;
		for(int i = 0; i <size(); i++){
			if(get(i).MERCHANT_MAX_CARRY >0){
				iSum += get(i).MERCHANT_FREQ;
			}
		}
		return iSum;
	}
	
	@Override
	public String toString(){
		String sOut = "Total Quantity " +getTotalQuant();
		for(int i = 0; i< size(); i++){
			sOut += " Good " +i+ ": " + get(i).toString() + "\n";
		}
		return sOut;
	}
	
}