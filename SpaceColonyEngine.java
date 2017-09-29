public class SpaceColonyEngine implements ISCError{

	private static final String CONSTR = "jdbc:sqlite:C:/Joey's coding stuf/SpaceColonySim/SCSDataBase.db";//For Sean D:\Dev\JavaJoe\SCS, for joey C:\Joey's coding stuf\SpaceColonySim;
	private SCSDataModule _scsdm;
	private SpaceColonyGame SCG;


	public static void main(String[] args){
		SpaceColonyEngine engine = new SpaceColonyEngine();
		engine.initialize();
		engine.launchGame();
	}

	private void launchGame(){

	}

	private void initialize(){
		_scsdm = new SCSDataModule();
		_scsdm.errorHandler = this;
		_scsdm.setConnectionString(CONSTR);
		SCG = new SpaceColonyGame();
		System.out.println("Press 1 for English, 2 para Espanol");
		String sInput = System.console.readln();
		int iInput = Integer.parseInt(sInput);
		boolean done = false;
		while(done == false){
			switch(iInput){
				case 1:
					_scsdm.setLanguage("ENG");
					done = true
					break;
				case 2:
					_scsdm.setLanguage("ESP");
					done = true
					break;

				default:
					System.out.println("Invalid Input. Try Again.")
			}
		}
		done = false;
			//load or save.
		while(done == false){
			System.out.println(_scsdm.getDisplayText("NEW_OR_LOAD");
			iInput = Integer.parseInt(sInput);
			switch(iInput){
				case N:
					done = true;
					break;
				case L:
					System.out.println("Feature not yet implemented");
					break;
				default:
					System.out.println("Invalid Input. Try Again.");
					break;

			}
		}
		done = false;

		_scsdm.connect();
		//if it is not connected, why bother?
		if(_scsdm.isConnected() == true){
			//nothing, exit the loop.
		}else{
			System.out.println("The data base could not be connected to. Exiting game");
			break;
		}
		_scsdm.traderMagic();
		System.out.println(_scsdm.getDisplayText("INVENTORY");
		_scsdm.displayLabour





	}

	public void handleException(Exception thisException){

	}

	public void randomEvent(){
		int iRandomEventTag = 100*Math.rand();
	}

	public void randomEffects(){

	}

	public void produce(){
	//Mining production happens here. SHoudl manufacturing be done here also, or should it bwe its own thing?
	// idea: instead of producing 1 unit per worker, each worker could potentialy produce 1d4 units. Woudl it be Nd4, or 1d4*N? Etheir way works. But one produces a curve, and the other does not. Both are posible, but they have differing ramifications.
	}

	public void consume(){
	//food is eaten, and procuction matereals are consumed.
	}

	public void traderMagic(){
	//if yess, do the freighter things. If not, it will go to the next thing.
		while (SCS.iMerchantCountDown == 0){
			System.out.println(_scsdm.getDisplayText("TRADER_ARIVES");
			sInput = System.console.readln()
						switch(sInput){
							case B:
								engine.buy();
								iTraderCountDown = Math.rint(3 * Math.rand())+1;
								break;
							case S:
								engine.sell();
								iTraderCountDown = Math.rint(3 * Math.rand())+1
								break;
							case N:
								iTraderCountDown = Math.rint(3 * Math.rand())+1
								//Then it just leaves.
								break;

							default:
								System.out.println("Invalid Input. Try Again.")
						}

			}
		}
	 }

	public void setFreighterArrivalDate(){
	//2d4 is an idea taht I had. Anything else shoudl work.
	}

	public void displayInventory(){
		//Prints out all of the items you have and quantities therof.
	}

	public void buy(){
		System.out.println(_scsdm.getDisplayText("BUY_WHAT");
	}

	public void sell(){
		System.out.println(_scsdm.getDisplayText("SELL_WHAT");
	}

	public void displayLabour(){
		//tells you what you used last turn for owrker assignmetns.
		System.out.println(_scsdm.getDisplayText("PREV_WORK_ASSIGN");
	}

	public void modifyLabour(){
		//to set production for next turn. You have the option to simply leave it with the
	}

	public void checkAsignments(){
		//are there too manny or too few workers assigned.
	}

	public void endTurnMiscelanea(){
		//Incremetns turn-count, an any other miscelaneous things that we may think of later
	}

}