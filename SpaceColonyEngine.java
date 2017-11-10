public class SpaceColonyEngine implements ISCSError{
	//for Joey, compile with: Call "C:\Program Files (x86)\Java\jdk1.8.0_91\bin\javac.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim" SpaceColonyEngine.java
	//and Compile with Call "C:\Program Files (x86)\Java\jre1.8.0_91\bin\java.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim;C:\Joey's coding stuf\SpaceColonySim\sqlite-jdbc-3.18.0.jar" SpaceColonyEngine

	private static final String CONSTR = "jdbc:sqlite:C:\\Joey's coding stuf\\SpaceColonySim\\SpaceColonySim\\SCSDataBase.db";     //For Sean D:\\Dev\\JavaJoe\\SCS, for joey C:\\Joey's coding stuf\\SpaceColonySim;
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
					done = true;
					break;
				case 2:
					_scsdm.setLanguage("ESP");
					done = true;
					break;

				default:
					System.out.println("Invalid Input. Try Again.");
			}
		}
		loadOrNew();



		done = false;

		_scsdm.connect();
		//if it is not connected, why bother?
		if(_scsdm.isConnected() == true){
			//nothing, exit the loop.
		}else{
			System.out.println("The data base could not be connected to. Exiting game");
			break;
		}
		//System.out.println(_scsdm.getDisplayText("INVENTORY");
		//_scsdm.traderMagic();
//		_scsdm.displayLabour
	}


	public void traderMagic(){
	//if yess, do the freighter things. If not, it will go to the next thing.
		if (traderAriveOrNot() == true){
			System.out.println(_scsdm.getDisplayText("TRADER_ARIVES"));
			sInput = System.console.readln();
						switch(sInput){
							case B:
								System.out.println(_scsdm.getDisplayText("INCOMPLETE"));
								//iTraderCountDown = Math.rint(3 * Math.rand())+1;
								break;
							case S:
								System.out.println(_scsdm.getDisplayText("INCOMPLETE"));

								//iTraderCountDown = Math.rint(3 * Math.rand())+1;
								break;
							case N:
								//iTraderCountDown = Math.rint(3 * Math.rand())+1;
								//Then it just leaves.
								break;

							default:
								System.out.println("Invalid Input. Try Again.");
						}
					}
				}


	public void loadOrNew(){
		done = false;
					//load or save.
			while(done == false){
			System.out.println(_scsdm.getDisplayText("NEW_OR_LOAD"));
			iInput = Integer.parseInt(sInput);
			switch(iInput){
				case N:
					done = true;
					break;
				case L:
					System.out.println(_scsdm.getDisplayText("INCOMPLETE"));
					break;
				default:
					System.out.println("Invalid Input. Try Again.");
		break;
			}
		}
	}

	private boolean traderAriveOrNot(){
		if (SCG.iMerchantCountDown == 0){
			return true;
		}else{
				return false;
		}
	}
}