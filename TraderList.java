import java.util.ArrayList;
public class TraderList extends Inventory{
	int MaxSpace = 100;
	
	@Override
	public String toString(){
		String sOut = "Max Inventory " + MaxSpace;
		sOut += super.toString();
		return sOut;
	}
	
}