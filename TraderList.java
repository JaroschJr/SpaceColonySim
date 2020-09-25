import java.util.ArrayList;
public class TraderList extends Inventory{
	int MaxSpace;
	int recruits;
	int recruitPrice;
	
	public TraderList(int i){
		MaxSpace = i;
	}
	
	@Override
	public String toString(){
		String sOut = "Max Inventory " + MaxSpace;
		sOut += super.toString();
		return sOut;
	}
	
}