/**
 * Class to represent a specific instance of
 * a game play through.  This will store all
 * of the data needed for the game.  This will
 * also contain the logic for the game mechanics.
 */
public class SpaceColonyGame{
	public int iTurnCount = 1;
	public int iPopulation = 0;
	public int iMoney = 50;
	public int iOre = 0;
	public int iScilicon = 0;
	public int iMerchantCountDown = 0;

	public void gameReset(){
		iTurnCount = 1;
		iPopulation = 0;
		iMoney = 50;
		iOre = 0;
		iScilicon = 0;
		iMerchantCountDown = 0;

	}

}