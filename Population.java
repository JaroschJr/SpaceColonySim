import java.util.ArrayList;
public class Population extends ArrayList<People>{

	public void gainPop(int gain){
		for(int i = 0; i<gain; i++){
			add(new People());
		}
	}
	
	public void losePop(int loss){
		if(loss>size()){
			loss=size();
		}
		for(int i = 0; i<loss; i++){
			remove(size()-1);
		}
	}
}