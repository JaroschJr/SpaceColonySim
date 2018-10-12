import java.util.Random;
import java.util.ArrayList;
public class RandomEventList extends ArrayList<RandomEvent>
{
	RandomEvent currentRandom;
	RandomEvent otherCurrentRandom;
	Random r= new Random();
	public RandomEvent generateEvent(){
		RandomEvent returnRandom = null;
		int iWeightCounter = r.nextInt(getWeightSum());
		int iEntryCounter = 0;
		while(returnRandom == null){
			otherCurrentRandom = get(iEntryCounter);
			if(otherCurrentRandom.iProbWeight <= iWeightCounter){
				iWeightCounter =  iWeightCounter - otherCurrentRandom.iProbWeight;
			}else{
				returnRandom = otherCurrentRandom;
				//break;
			}
			iEntryCounter++;
			
		}
		//search through the list, couign down. Start at random number between 0 and sum of weight, and count down by weight each time an event is checked
			return returnRandom;
	}
	
	private int getWeightSum(){
		int sum = 0;
		for(int i=0; i < size(); i++){
			currentRandom = get(i);
			sum += currentRandom.iProbWeight;
			
		}
		return sum;
	}
}