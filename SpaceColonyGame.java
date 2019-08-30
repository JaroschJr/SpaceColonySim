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
	
	public int iTurnCount = 1;
	
	public int iMerchantCountDown = 0;
	
	public Population pop;
	
	public StructureList structures;

	public void gameReset(){
		bIsOngoing = true;
		iTurnCount = 1;
		iMerchantCountDown = 0;

	}

}