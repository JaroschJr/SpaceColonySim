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
		loadOrNew();

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


		public void loadOrNew(){
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
		}
	 }
}