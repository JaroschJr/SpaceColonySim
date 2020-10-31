import java.util.ArrayList;
import java.util.Random;
import java.util.Formatter;
public class ProductOrder{
	public Good need;
	public int countdown;
	public boolean fulfilled = false;
	Random r = new Random();
	
	public ProductOrder(){
		
	}
	
	public ProductOrder(SpaceColonyGame scg){
		generateOrder(scg);
	}
	
	public void generateOrder(SpaceColonyGame scg){
		Inventory deck = scg.iInv.clone();
		int iWeightSum = 0;
		for(int i = 0; i<deck.size(); i++){
			iWeightSum+=deck.get(i).ORDER_FREQ;
		}
		
		int iWeightCounter = r.nextInt(iWeightSum);
		int iEntryCounter = 0;
		for(int i = 0; i<deck.size(); i++){
			if(deck.get(i).ORDER_FREQ <= iWeightCounter){
				iWeightCounter -= deck.get(i).ORDER_FREQ;
			}else{
				need = deck.get(i).clone();
				need.iQuant = 10+2*r.nextInt(10);
				countdown = 12+r.nextInt(10) + scg.iTurnCount ;
				break;
			}
			iEntryCounter++;
			
		}
		
	}
	
	@Override
	public String toString(){
		String sOut = countdown + need.toString();
		return sOut;
	}
	
	public boolean isFulfillable(SpaceColonyGame scg){
		boolean bOut = scg.iInv.getGoodByName(need.sName).iQuant>need.iQuant;
		return bOut;
		
	}
	
	public void fulfill(SpaceColonyGame scg){
		if(isFulfillable(scg)){
			scg.iInv.getGoodByName(need.sName).iQuant -= need.iQuant;
			fulfilled = true;
		}
	}

}