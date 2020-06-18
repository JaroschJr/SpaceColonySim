/**
 * Class to represent a specific instance of
 * a game play through.  This will store all
 * of the data needed for the game.  This will
 * also contain the logic for the game mechanics.
 */
public class SpaceColonyGame{
	public boolean bIsOngoing;
	public String sGuid;
	public Inventory iInv;
	
	public int iDebt = 10000;
	
	public int obMorale;
	public int subMorale;
	public int iDebtMissCount;
	
	public int iTurnCount = 1;
	
	public int iMerchantCountDown = 10000;
	
	public Population pop;
	
	public StructureList structures;

	public void gameReset(){
		bIsOngoing = true;
		iTurnCount = 1;
		iMerchantCountDown = 1000;
		obMorale = 0;
		subMorale = 50;
		iDebt = 0;
		iDebtMissCount = 0;
	}

}