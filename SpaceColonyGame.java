/**
 * Class to represent a specific instance of
 * a game play through.  This will store all
 * of the data needed for the game.  This will
 * also contain the logic for the game mechanics.
 */
public class SpaceColonyGame{
	public boolean bIsOngoing;
	public int iTurnCount = 1;
	public int iPopulation = 0;
	public int iMoney = 50;
	public int iOre = 0;
	public int iSilicon = 0;
	public int iMerchantCountDown = 0;

	public void gameReset(){
		bIsOngoing = true;
		iTurnCount = 1;
		iPopulation = 0;
		iMoney = 50;
		iOre = 0;
		iSilicon = 0;
		iMerchantCountDown = 0;

	}

}