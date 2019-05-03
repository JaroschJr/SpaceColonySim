import java.util.ArrayList;
public class Population extends ArrayList<People>{

	public void gainPop(int gain){
		for(int i = 0; i<gain; i++){
			add(new People());
		}
	}
	
	public People firstUnassigned(){
		People pOut = null;
		for( int i = 0; i<size(); i++){
			if(get(i).assigned == false){
				pOut = get(i);
				break;
			}
		}
		return pOut;
	}
	
	public void losePop(int loss){
		if(loss>size()){
			loss=size();
		}
		for(int i = 0; i<loss; i++){
			get(size()-1).assigned = false;
			remove(size()-1);
		}
	}
	
	public int howManyAssigned(){
		int count = 0;
		for( int i = 0; i < size(); i++){
			if (get(i).assigned != false){
				count++;
			}
		}		
		return count;
	}
	
	public int howManyUnassigned(){
		int i = size();
		i -= howManyAssigned();
		return i;
	}
	
	@Override
	public String toString(){
		String sOut = size() + " Workers ";
		for(int i = 0; i<size(); i++){
			sOut += i + get(i).toString();
		}
		return sOut;
	}
}