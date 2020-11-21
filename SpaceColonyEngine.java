/**
 * Class to manage the execution of the the game and
 * direct the interface with the user.  This class
 * will be responsible for talking to the database,
 * user I/O, and will create and own the actual
 * game object.
 */
import java.util.Random;
import java.util.ArrayList;
import java.io.File;
import java.util.Formatter;
import java.nio.file.*;

public class SpaceColonyEngine implements ISCSError{
	//for Joey, compile with: Call "C:\Program Files (x86)\Java\jdk1.8.0_91\bin\javac.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim" SpaceColonyEngine.java
	//and run with Call "C:\Program Files (x86)\Java\jre1.8.0_91\bin\java.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim;C:\Joey's coding stuf\SpaceColonySim\sqlite-jdbc-3.18.0.jar" SpaceColonyEngine
	//For joey CONSTR woudl be jdbc:sqlite:C:\\Joey's coding stuf\\SpaceColonySim\\SpaceColonySim\\SCSDataBase.db
	private static final String SQLITE = "jdbc:sqlite";
	private static final String GAME_DATABASE = "SCSDataBase.db";
	private static final String SAVE_DATABASE = "SCSSaves.db";
	private static final String MASTER_SAVE_DATABASE = "SCSSavesMaster.db";
	
	private int debtFrequency = 20;
	
	private static String FOLDER_PATH = "";
	private static String CONSTR = "";
	private static String SAVECONSTR = "";
	private SCSDataModule _scsdm;
	private SCSDataModule _savedm;
	private SpaceColonyGame SCG;
	private ISCSIO _ioman;
	private RandomEventFactory rEventFactory;
	private RandomEventList events;
	private RandomEvent currentEvent;
	//to test to see if Structure and descendat classed were made correctly.
	private Structure testStructure;
	private RecipeList listOfRecipes;
	private InventoryFactory InvFact;
	private StructureFactory StructFact;
	private StructureList StructList;
	private MoraleManager mManager;
	private DebtMannager dMan;
	
	private static final String SCS_SAVE_GAME_VERSION = "1.6";
	
	
	/*
	//Begining of example random events, to check for bugs.
	private RandomInputEvent aEvent;
	private ProductionCancellingEvent bEvent;
	private StatModifierEvent cEvent;
	//End of example random events.
	*/
	Random rand = new Random();

	/**
	 * Standard main method for executing a playthrough
	 * of the game.
	 * @param args The SQLite connectionn string
	 *             should be passed in as a command
	 *             line argument.
	 */
	public static void main(String[] args){
		
		
		FOLDER_PATH = args[0];
		CONSTR = SQLITE + ":" + FOLDER_PATH + File.separator + GAME_DATABASE;
		SAVECONSTR = SQLITE + ":" + FOLDER_PATH + File.separator + SAVE_DATABASE;
		//Random rand = new Random();
		SpaceColonyEngine engine = new SpaceColonyEngine();
		engine.initialize();
		engine.launchGame();
		//System.out.println("Start turns");
		engine.turns();
	}

	/**
	 * Callback method for dealing with exceptions
	 * which occur wuthin the game object.
	 * @param thisException The exception which
	 *                      has occurred.
	 */
	public void handleException(Exception thisException, String sExtraInfo, boolean bFatal){
		System.out.println(thisException.getMessage());
		System.out.println(sExtraInfo);
		
		if(bFatal){
			System.exit(0);
		}else{
			
		}
	}
	
	/**
	 * Checks if the saved game database is up-to-date
	 * and asks if the user wants to automatically upgrade
	 * at the cost of losing all saved games.
	 */
	private void checkForUpgrade() {
		//check the save database version to see if it
		//is compatible with the game database
		String saveVersion = _savedm.getSaveVersion();
		if(!saveVersion.equals(SCS_SAVE_GAME_VERSION)) {
			System.out.println("Invalid save version! Current version should be " + SCS_SAVE_GAME_VERSION);
			System.out.println("Version found: " + saveVersion);
			System.out.println();
			System.out.println("The save database can be upgraded automatically.");
			System.out.println("Please note: THIS WILL DELETE ALL EXISTING SAVED GAMES!");
			System.out.println("Or, you may close the game and upgrade the save game");
			System.out.println("database manually and then restart");
			System.out.println();
			System.out.println("Please type the word 'UPDATE' to perform the update.");
			String answer = System.console().readLine();
			
			if(answer.equalsIgnoreCase("UPDATE")) {
				System.out.println("Updating saved games database");
				
				//shut down the current save database
				_savedm.disConnect();
				_savedm = null;
			
				try {
					//delete the SCSSaves.db file
					File dbPath = new File(FOLDER_PATH + File.separator + SAVE_DATABASE);
					File masterDbPath = new File(FOLDER_PATH + File.separator + MASTER_SAVE_DATABASE);
					Files.deleteIfExists(dbPath.toPath());
					
					//copy over the blank master database SCSSavesMaster.db
					Files.copy(masterDbPath.toPath(), dbPath.toPath());
				}//end try
				catch(Exception e) {
					System.out.println("Could not copy the master save database, please upgrade manually.");
					System.exit(3);
				}//end catch e
				
				//recreate the save database
				_savedm = new SCSDataModule();
				_savedm.errorHandler = this;
				_savedm.setConnectionString(SAVECONSTR);
				_savedm.connect();
			}//end if
			else {
				System.out.println("Exiting without update");
				System.exit(3);
			}//end else
		}//end if
	}

	/**
	 * Initializes the state of the game engine.
	 * Sets up the database connection, and
	 * establishes the users language preference.
	 */
	private void initialize(){
		//_scsdm startup
		_scsdm = new SCSDataModule();
		_scsdm.errorHandler = this;
		_scsdm.setConnectionString(CONSTR);
		_scsdm.connect();
		
		_savedm = new SCSDataModule();
		_savedm.errorHandler = this;
		_savedm.setConnectionString(SAVECONSTR);
		_savedm.connect();
		
		checkForUpgrade();

		_ioman = new SCSConsoleIO();
		mManager = new MoraleManager();
		dMan = new DebtMannager();
		

		//check if the database connection has been
		//established, if not then display an error
		//message and close the application
		
		if(!_scsdm.isConnected()){
			_ioman.lineOut("The database could not be connected to. Exiting game");
			//System.out.println("The data base could not be connected to. Exiting game");
			System.exit(1);
		}
		
		if(!_savedm.isConnected()){
			_ioman.lineOut("No save data base detected. Exiting game");
			System.exit(1);
		}//end if
		
		rEventFactory = new RandomEventFactory(_scsdm);
		events = rEventFactory.getList();
	}

	/**
	 * Creates the game object and prepares it
	 * for a playthrough, either initializing
	 * a new game or loading a previous game.
	 */
	private void launchGame(){
		SCG = new SpaceColonyGame();
		//delete this later
		
		
		InvFact = new InventoryFactory(_scsdm);
		SCG.iInv = InvFact.getList();
		SCG.iInv.setGoodQuantByName("Water", 20);
		listOfRecipes = InvFact.getRecipeList();
		
		StructFact = new StructureFactory(_scsdm);
		StructList = StructFact.getStructureList();
		//get copies of starting structures.
		StructureList sList = new StructureList(); 
		sList.add(StructList.getStructureByName("oreMine").clone());
		sList.add(StructList.getStructureByName("farm").clone());
		//sList.add(StructList.getStructureByName("farm").clone());
		//sList.add(StructList.getStructureByName("goodFactory").clone());
		//System.out.println("Starting Structure Finished Test");
		
		SCG.structures = sList;
		SCG.pop = new Population();
		SCG.pop.gainPop(10);
		//System.out.println("Sample Guid " + _scsdm.getGuid());
		
		//System.out.println(SCG.pop.toString());
		//sList.getStructureByName("farm").setWork(SCG.pop, 5, listOfRecipes.getRecipeByName("food"));
		//ProductionBuilding pbTest = (ProductionBuilding) sList.getStructureByName("farm");
		//pbTest.setWork(SCG.pop, 5, listOfRecipes.getRecipeByName("Food"));
		//System.out.println(sList.getStructureByName("farm").toString());
		//end gettign structures.
		
		//for testing purposes
		//System.out.println(StructList.toString());
		//System.out.println(listOfRecipes.get(0).get(0).toString());
		//System.out.println(listOfRecipes.get(0).toString());
		
		//Good g = SCG.iInv.get(0);
		//System.out.println(g.sName)

		getLanguagePreference();
		loadOrNew();

		//System.out.println(_scsdm.getDisplayText("INVENTORY"));
	}

	/**
	 * Asks the user if the game is to be a new
	 * game or loading a proviously saved game.
	 * Prepares the game object according to
	 * the choice.
	 */
	public void loadOrNew(){
		boolean done = false;
					//load or new.
		while(done == false){
			System.out.println(_scsdm.getDisplayText("NEW_OR_LOAD"));
			String sInput = System.console().readLine();
			sInput = sInput.toUpperCase();
			switch(sInput){
				case "N":
					done = true;
					SCG.gameReset();
					SCG.iMerchantCountDown = 2+rand.nextInt(3);
					break;

				case "L":
					if(_savedm.getSaveNames().size()>0){
						loadSave();
						done = true;
					}else{
						_ioman.lineOut(_scsdm.getDisplayText("NO_SAVE"));
					}
					//System.out.println(_scsdm.getDisplayText("INCOMPLETE"));
					break;
				case "D":
					if(_savedm.getSaveNames().size()>0){
						delSave();
					}else{
						_ioman.lineOut(_scsdm.getDisplayText("NO_SAVE"));
					}
				
					break;
					
				default:
					System.out.println("Invalid Input. Try Again.");
					break;
			}
		}
	}

	/**
	 * Asks the user to set the language preference
	 * for the current playthrough.
	 */
	private void getLanguagePreference(){
		//get the matrix of languages supported
		//by the database.
		String[][] _langs;

		_langs = _scsdm.getLanguages();

		//exit the program if there are no
		//languages in the database
		if(_langs.length == 0){
			_ioman.lineOut("No languages found!");
			System.exit(2);
		}//end if

		//ask the user to choose the language
		String _langCode = null;
		while(_langCode == null){
			for(int i = 0; i < _langs.length; i++){
				_ioman.lineOut(i + ": " + _langs[i][1]);
			}//end for i

			int idx = _ioman.intIn("Select your preferred language.");

			if((idx >= 0)&& (idx < _langs.length)){
				_langCode = _langs[idx][0];
			}//end if
			else{
				_ioman.lineOut("Invalid, enter 0 - " + (_langs.length - 1));
			}//end else
		}//end while

		//set the language code on the data module
		_scsdm.setLanguage(_langCode);

		_ioman.lineOut(_scsdm.getDisplayText("WELCOME"));
	}
	
	private void turns(){
		if(SCG.iTurnCount == 1){
			SCG.sGuid = _savedm.getGuid();
			_savedm.saveGameTurnLog(SCG);
		}//end if
		
		while(SCG.bIsOngoing == true){//Per the flowchart on the Cloud:
			//System.out.println(SCG.iInv.toString());
			report();
			String SINN;
		
			/*for(int i = 0; i<SCG.structures.size(); i++){
				System.out.println(SCG.structures.get(i));
			}*/
			
			
			//Tennative suggestion: Some kind of status update?
			/*
			for (int i = 0; i< listOfRecipes.size(); i++){
				System.out.println(listOfRecipes.get(i).toString());
			}
			*/
			currentEvent = events.generateEvent();
			//System.out.println(currentEvent.toString());
			//truncate it so it does one and only one iteration.
			/*
			if (SCG.iTurnCount >20){
			SCG.bIsOngoing = false;//truncate it so it does one and only one iteration. Its looping infinitely caused issues during testing to complex to understand here.
			}
			*/
			
			//Order related things
			if(SCG.iTurnCount>=25){
				if(SCG.request == null){//for the first time.
					SCG.request = new ProductOrder(SCG);
					_ioman.lineOut(String.format(_scsdm.getDisplayText("NEW_ORDER"),  SCG.request.need.iQuant, _scsdm.getDisplayText(SCG.request.need.sTextCode), SCG.request.countdown));
					//System.out.println(SCG.request.toString());
				}else if(SCG.request.fulfilled){
					SCG.request = new ProductOrder(SCG);
					_ioman.lineOut(String.format(_scsdm.getDisplayText("NEW_ORDER"),  SCG.request.need.iQuant, _scsdm.getDisplayText(SCG.request.need.sTextCode), SCG.request.countdown));
				}else if(SCG.iTurnCount == SCG.request.countdown ){
					if(SCG.request.isFulfillable(SCG)){
						SCG.request.fulfill(SCG);
					}else{
						SCG.iDebt += SCG.request.need.iQuant*SCG.request.need.BASE_PRICE;
						SCG.request.fulfilled = true;
					}
					
					
				}
			}
	
			
			_ioman.lineOut("");
			viewAndSetProduction();
			_ioman.lineOut("");
			
			
			if(traderAriveOrNot()){
			_ioman.lineOut(_scsdm.getDisplayText("CONTINUE"));
			SINN = _ioman.stringIn("");
				merchantThings();
				SCG.iMerchantCountDown = 3+rand.nextInt(6);
			}else{
				SCG.iMerchantCountDown--;
			}
			
			_ioman.lineOut(_scsdm.getDisplayText("CONTINUE"));
			SINN = _ioman.stringIn("");
			
			if(SCG.iTurnCount % debtFrequency == 0){
				if(SCG.iDebt>0){
					dMan.applyInterest(SCG);
					dMan.payDebt(SCG, _scsdm, _ioman);
				}
			}
			
			//System.out.println("HERE NO 3");
			produce();
			currentEvent.performEvent(SCG, _ioman, _scsdm);
			validateStructures();
			SCG.obMorale = mManager.calcObjectiveMorale(SCG,currentEvent);
			SCG.subMorale = mManager.calcSubjectiveMorale(SCG);
			popEat();
			
			
			SCG.iTurnCount++;
			saveCurrentGame();
			
			
			
			
			_ioman.lineOut("****************************************");
			_ioman.lineOut(_scsdm.getDisplayText("TURN_END_BREAK"));
			_ioman.lineOut("****************************************");
			
			if(SCG.pop.size()==0){
				_ioman.lineOut(_scsdm.getDisplayText("POPULATION_DEFEAT"));
				SCG.bIsOngoing = false;
			}else if(SCG.subMorale<=0){
				_ioman.lineOut(_scsdm.getDisplayText("MORALE_DEFEAT"));
				SCG.bIsOngoing = false;
			}else if(SCG.iDebtMissCount >=3){
				_ioman.lineOut(_scsdm.getDisplayText("DEBT_DEFEAT"));
				SCG.bIsOngoing = false;
			}
			
			if(SCG.bIsOngoing == false ){
				_ioman.lineOut(_scsdm.getDisplayText("DEFEAT_MESSAGE"));
			}
			//save some log information for testing purposes
			_savedm.saveGameTurnLog(SCG);
		}
	}
	
	
	private void popEat(){
		SCG.iInv.getGoodByName("Food").iQuant-=(SCG.pop.size()/6);
			if(SCG.iInv.getGoodByName("Food").iQuant<0){
				
				SCG.iInv.getGoodByName("Food").iQuant = 0;
			}
			
			SCG.iInv.getGoodByName("Water").iQuant-=(SCG.pop.size()/6);
			if(SCG.iInv.getGoodByName("Water").iQuant<0){
				
				SCG.iInv.getGoodByName("Water").iQuant = 0;
			}
			
	}
	

	private boolean traderAriveOrNot(){
		boolean bArive;
		if (SCG.iMerchantCountDown <= 0){
			bArive = true;
		}else{
			bArive = false;
		}
		return bArive;
	}
	
	public void validateStructures(){
		for(int i = 0; i<SCG.structures.size(); i++){
			SCG.structures.get(i).assignValidate();
		}
	}
	
	private void report(){
		int answer = 0;
		_ioman.lineOut(_scsdm.getDisplayText("TURN_REPORT_TURN") +getSpacer((20-_scsdm.getDisplayText("TURN_REPORT_TURN").length())- Integer.toString(SCG.iTurnCount).length()) + SCG.iTurnCount);
		//_ioman.lineOut(_scsdm.getDisplayText("ORDER")+getSpacer((20-_scsdm.getDisplayText("TURN_REPORT_TURN").length()))
		_ioman.lineOut(_scsdm.getDisplayText("TURN_REPORT_POPULATION") +getSpacer((20-_scsdm.getDisplayText("TURN_REPORT_POPULATION").length())- Integer.toString(SCG.pop.size()).length()) + SCG.pop.size());
		_ioman.lineOut(_scsdm.getDisplayText("MORALE") +getSpacer((20-_scsdm.getDisplayText("MORALE").length())- Integer.toString(SCG.subMorale).length()) + SCG.subMorale);
		_ioman.lineOut(_scsdm.getDisplayText("DEBT") + getSpacer((20-_scsdm.getDisplayText("DEBT").length())- Integer.toString(SCG.iDebt).length()) + SCG.iDebt);
		_ioman.lineOut(mManager.moraleReport(SCG,_scsdm));
		if(SCG.request!=null){
			_ioman.lineOut(String.format(_scsdm.getDisplayText("TURN_REPORT_ORDER"), _scsdm.getDisplayText(SCG.request.need.sTextCode), SCG.request.need.iQuant, SCG.request.countdown));
		}
		_ioman.lineOut(_scsdm.getDisplayText("CONTINUE"));
		 _ioman.stringIn("");
		for(int i = 0; i<SCG.iInv.size(); i++){
			String sOut = "";
			Good gPrint = SCG.iInv.get(i);
			if(gPrint.bPublish){
				sOut = _scsdm.getDisplayText(gPrint.sTextCode);
				String sPlaceHolder = getSpacer(20-sOut.length()-Integer.toString(gPrint.iQuant).length());
				
				sOut += sPlaceHolder + gPrint.iQuant;
				_ioman.lineOut(sOut);
			}
			
		}
		
		if(SCG.iInv.hasUnreported()){
			String[][] sAsk = new String[2][2];
			sAsk[0][0] = " ";
			sAsk[0][1] = " ";
			sAsk[1][0] = " 1-";
			sAsk[1][1] = _scsdm.getDisplayText("YES");
			int iAns = selectScreen(_scsdm.getDisplayText("MORE_ITEMS"), _scsdm.getDisplayText("FULL_INVENTORY"), sAsk);
			if(iAns == 1){
				fullItemReport();
			}
			
		}

		//for a gap:
		_ioman.lineOut(" ");
		_ioman.lineOut(_scsdm.getDisplayText("TURN_REPORT_BUILDINGS"));
		//reporting of structures
		for(int i = 0; i<StructList.size(); i++){
			int iNum = 0;
			for(int j = 0; j<SCG.structures.size(); j++){
				if(SCG.structures.get(j).NAME.equals(StructList.get(i).NAME)){
					iNum++;
				}
			}
			
			if(iNum>0){
			String sOut = "";
			sOut = _scsdm.getDisplayText(StructList.get(i).TEXT_CODE);
			sOut += getSpacer(20-sOut.length()-Integer.toString(iNum).length());
			sOut += iNum;
			_ioman.lineOut(sOut);
			}
			
		}
		
	}
	
	private void fullItemReport(){
		
		if(SCG.iInv.size()%2==0){
			for(int i = 0; i<SCG.iInv.size()-1; i+=2){
				String sOut = "";
				//First
				Good gPrint = SCG.iInv.get(i);
				sOut = _scsdm.getDisplayText(gPrint.sTextCode);
				String sPlaceHolder = getSpacer(20-sOut.length()-Integer.toString(gPrint.iQuant).length());	
				sOut += sPlaceHolder + gPrint.iQuant;
				//Second
				sOut+= getSpacer(10);
				gPrint = SCG.iInv.get(i+1);
				sOut += _scsdm.getDisplayText(gPrint.sTextCode);
				sPlaceHolder = getSpacer(20-_scsdm.getDisplayText(gPrint.sTextCode).length()-Integer.toString(gPrint.iQuant).length());
				sOut += sPlaceHolder + gPrint.iQuant;
				_ioman.lineOut(sOut);
			}
		}else{
			for(int i = 0; i<SCG.iInv.size()-1; i+=2){
				String sOut = "";
				//First
				Good gPrint = SCG.iInv.get(i);
				sOut = _scsdm.getDisplayText(gPrint.sTextCode);
				String sPlaceHolder = getSpacer(20-sOut.length()-Integer.toString(gPrint.iQuant).length());	
				sOut += sPlaceHolder + gPrint.iQuant;
				//Second
				sOut+= getSpacer(10);
				gPrint = SCG.iInv.get(i+1);
				sOut += _scsdm.getDisplayText(gPrint.sTextCode);
				sPlaceHolder = getSpacer(20-_scsdm.getDisplayText(gPrint.sTextCode).length()-Integer.toString(gPrint.iQuant).length());	
				sOut += sPlaceHolder + gPrint.iQuant;
				_ioman.lineOut(sOut);
			}
			
			Good gPrint = SCG.iInv.get(SCG.iInv.size()-1);
			String sOut = _scsdm.getDisplayText(gPrint.sTextCode);
			String sPlaceHolder = getSpacer(20-sOut.length()-Integer.toString(gPrint.iQuant).length());			
			sOut += sPlaceHolder + gPrint.iQuant;
			_ioman.lineOut(sOut);
		}
			
			
			
		_ioman.lineOut(_scsdm.getDisplayText("CONTINUE"));
		String SINN = _ioman.stringIn("");
	}
	
	
	
	public int selectScreen(String prelude, String question, String[][] text){
		int iAnswer = 0;
		_ioman.lineOut(prelude);
		//System.out.println("Text Length " + text.length);
		for(int i = 0; i<text.length; i++){
			String sOut = "";
			if(i==1){
				_ioman.lineOut(" 0-" + getSpacer(18-" 0-".length()) + _scsdm.getDisplayText("CANCEL"));
			}
			for(int j = 0; j<text[i].length; j++){
				//System.out.println("line " + i + " column " +j);
				//System.out.println(text[i][j]);
				sOut += text[i][j]+ getSpacer(18-text[i][j].length());
			}
			_ioman.lineOut(sOut);
		}
		//iAnswer = _ioman.intIn(question);
		
		while(true){
			iAnswer = _ioman.intIn(question);
			if(iAnswer>=0&&iAnswer<=(text.length-1)){
				break;
			}else{
				_ioman.lineOut(_scsdm.getDisplayText("INVALID"));
			}
		}
		
		
		
		return iAnswer;
	}
	
	public void viewAndSetProduction(){
		while(true){
			String[][] structReport = new String[SCG.structures.size()+3][3];
			structReport[0][0] = _scsdm.getDisplayText("BUILDING");
			structReport[0][1] = _scsdm.getDisplayText("WORKERS");
			structReport[0][2] = _scsdm.getDisplayText("RECIPE");
				for(int i = 1;i<=SCG.structures.size(); i++){//Come back here.
				
					if(i<10){
						structReport[i][0] =" "+i+"- " + _scsdm.getDisplayText(SCG.structures.get(i-1).TEXT_CODE);
						//System.out.println(SCG.structures.get(i-1).toString());
					}else{
						structReport[i][0] =i+"- " + _scsdm.getDisplayText(SCG.structures.get(i-1).TEXT_CODE);
						//System.out.println(SCG.structures.get(i-1).toString());
					}
				
					
					if(SCG.structures.get(i-1).MAX_WORKERS!=0){
					structReport[i][1] = Integer.toString( SCG.structures.get(i-1).iWorkers) + " / " + Integer.toString(SCG.structures.get(i-1).MAX_WORKERS);
					//System.out.println(SCG.structures.get(i-1).toString());
					}else{
						structReport[i][1] = "-------";
					}
					
					if(SCG.structures.get(i-1).bComplete == false){
						structReport[i][2] = _scsdm.getDisplayText("SELF")+ " (" + SCG.structures.get(i-1).howDone() + ")";
					}else if(SCG.structures.get(i-1) instanceof ProductionBuilding){
						ProductionBuilding tempPBSlot = (ProductionBuilding) SCG.structures.get(i-1);
						//System.out.println(SCG.structures.get(i-1));
					
						//System.out.println(tempPBSlot.toString());
						if(tempPBSlot.currentRecipe!=null){
							structReport[i][2] = _scsdm.getDisplayText(tempPBSlot.currentRecipe.TEXT_CODE);
						}else{
							structReport[i][2] = "-------";
						}
						//System.out.println(SCG.structures.get(i-1).toString());
					}else{
							structReport[i][2] = "-------";
					}
				}
			//structReport[SCG.structures.size()+1][0] = _scsdm.getDisplayText("IDLE_WORKERS");
			//structReport[SCG.structures.size()+1][1] = Integer.toString(SCG.pop.howManyUnassigned());
			//structReport[SCG.structures.size()+1][2] = "-------";
			if(SCG.structures.size()<10){
				structReport[SCG.structures.size()+1][0] = " "+(SCG.structures.size()+1)+"-" + _scsdm.getDisplayText("NEW_BUILDING");
			}else{
				structReport[SCG.structures.size()+1][0] = ""+(SCG.structures.size()+1)+"-" + _scsdm.getDisplayText("NEW_BUILDING");
			}
			structReport[SCG.structures.size()+1][1] = "";
			structReport[SCG.structures.size()+1][2] = "";
			
			//The Demolish Structures part
			if(SCG.structures.size()<10){
				structReport[SCG.structures.size()+2][0] = " "+(SCG.structures.size()+2)+"-" + _scsdm.getDisplayText("DEMOLISH");
			}else{
				structReport[SCG.structures.size()+2][0] = ""+(SCG.structures.size()+2)+"-" + _scsdm.getDisplayText("DEMOLISH");
			}
			structReport[SCG.structures.size()+2][1] = "";
			structReport[SCG.structures.size()+2][2] = "";
			
			
			Structure bWorkingBuilding;
			int iAnswer = selectScreen(_scsdm.getDisplayText("SELECT_CURRENT_ASSIGNMENT"), _scsdm.getDisplayText("WHICH_TO_CHANGE" ) + ". "+ SCG.pop.howManyUnassigned() + " " + _scsdm.getDisplayText("IDLE_WORKERS"), structReport);
			if(iAnswer>0&&iAnswer <=SCG.structures.size()){
				//
				bWorkingBuilding = SCG.structures.get(iAnswer-1);
				/*
				String[][] sQuantAsk = new String[bWorkingBuilding.MAX_WORKERS+1][2];
				sQuantAsk[0][0] = "   ";
				sQuantAsk[0][1] = "   ";
				
				for(int i = 1; i<=bWorkingBuilding.MAX_WORKERS; i++){
					if(i<10){
						sQuantAsk[i][0] =" "+i+"- ";
					}else{
						sQuantAsk[i][0] =i+"- ";
					}
					sQuantAsk[i][1] = Integer.toString( i);
				}
				
				
				int iNewWorkers = selectScreen(" ", _scsdm.getDisplayText("HOW_MANY_WORKERS"), sQuantAsk);
				*/
				int iNewWorkers;// = howManyWorkers(bWorkingBuilding);
				if(true){//BEFORE it was iNewWorkers>0
					if(bWorkingBuilding.bComplete == false){
						//System.out.println(" " +bWorkingBuilding.bComplete);
						//System.out.println("Reaches B");
						iNewWorkers = howManyWorkers(bWorkingBuilding);
						
						if(iNewWorkers > 0){
							bWorkingBuilding.setWork(SCG.pop, iNewWorkers);
						}
						
						if(bWorkingBuilding.iWorkers>0){
							bWorkingBuilding.bBuildingSelf = true;
						}
						
					}else if(bWorkingBuilding instanceof ProductionBuilding){
						ProductionBuilding bTempo = (ProductionBuilding) bWorkingBuilding;
						
						if(bTempo.sPosibleRecipes.size() == 1){
							iNewWorkers = howManyWorkers(bWorkingBuilding);
							if(iNewWorkers >0){
								bTempo.setWork(SCG.pop, iNewWorkers, listOfRecipes.getRecipeByGuid(bTempo.sPosibleRecipes.get(0)));
							}else{
								bTempo.setWork(SCG.pop, 0, null);
							}
						}else{
							String[][] sRecipePick = new String[bTempo.sPosibleRecipes.size()+1][4];
							sRecipePick[0][0] = "";
							sRecipePick[0][1] = _scsdm.getDisplayText("RECIPE");
							sRecipePick[0][2] = _scsdm.getDisplayText("MAN_HOURS");
							sRecipePick[0][3] = _scsdm.getDisplayText("MATERIALS");
							
							for(int i = 0; i <bTempo.sPosibleRecipes.size(); i++){
								sRecipePick[i+1][0]=" " + (i+1) +"-";
								sRecipePick[i+1][1] = _scsdm.getDisplayText(listOfRecipes.getRecipeByGuid(bTempo.sPosibleRecipes.get(i)).TEXT_CODE);
								sRecipePick[i+1][2] = Integer.toString(listOfRecipes.getRecipeByGuid(bTempo.sPosibleRecipes.get(i)).MAN_HOURS);
								sRecipePick[i+1][3] = getRecipeDisplay(listOfRecipes.getRecipeByGuid(bTempo.sPosibleRecipes.get(i)));
							}
							
							int recipResponse = 0;
							while(recipResponse<=0||recipResponse>bTempo.sPosibleRecipes.size()){
								recipResponse = selectScreen(_scsdm.getDisplayText("WHICH_RECIPE"), _scsdm.getDisplayText("WHICH_RECIPE"), sRecipePick);
								if(recipResponse == 0){
									break;
								}
							}
							
							if(recipResponse == 0){
								bTempo.setWork(SCG.pop, 0, null);
							}else{
								iNewWorkers = howManyWorkers(bWorkingBuilding);
								if(iNewWorkers >0){
									bTempo.setWork(SCG.pop, iNewWorkers, listOfRecipes.getRecipeByGuid(bTempo.sPosibleRecipes.get(recipResponse-1)));
								}else{
									bTempo.setWork(SCG.pop, 0, null);
								}
							}
						}
						
					}else{
						iNewWorkers = howManyWorkers(bWorkingBuilding);
						bWorkingBuilding.setWork(SCG.pop, iNewWorkers);
					}
					
				}else{
					if(bWorkingBuilding instanceof ProductionBuilding){
						ProductionBuilding bTempo = (ProductionBuilding) bWorkingBuilding;
						bTempo.setWork(SCG.pop, 0, null);
					}else{
						
						bWorkingBuilding.setWork(SCG.pop, 0);
					}
				}
			}else if(iAnswer == (SCG.structures.size()+1)){
				buildNewStructure();
			}else if(iAnswer == (SCG.structures.size()+2)){
				removeBuilding();
			}else{
				break;
			}	
		}
	}
	
	public int howManyWorkers(Structure bWorkingBuilding){
		
		String[][] sQuantAsk = new String[bWorkingBuilding.MAX_WORKERS+1][2];
			sQuantAsk[0][0] = "   ";
			sQuantAsk[0][1] = "   ";
				
			for(int i = 1; i<=bWorkingBuilding.MAX_WORKERS; i++){
				if(i<10){
					sQuantAsk[i][0] =" "+i+"- ";
				}else{
					sQuantAsk[i][0] =i+"- ";
				}
				sQuantAsk[i][1] = Integer.toString( i);
			}
					
			int iNewWorkers = selectScreen(" ", _scsdm.getDisplayText("HOW_MANY_WORKERS"), sQuantAsk);
			return iNewWorkers;
	}
	
	public String getSpacer(int iSize){
		String returnString = "";
		for(int i = 0; i<iSize; i++){
			returnString+=" ";
		}
		return returnString;
	}
	
	public void merchantThings(){
		TraderList tBList = makeBuyList();
		Inventory tSList = makeSellList(tBList);
		tBList.recruits = 1+rand.nextInt(3);
		tBList.recruitPrice = 10+rand.nextInt(20);
		//System.out.println("merchant toString " + tBList.toString());
		//System.out.println("inventory toString " +tSList.toString());
		while(true){
			int buyOrSell;
			String[][] sAsk = new String[3][2];
			sAsk[0][0] = " ";
			sAsk[0][1] = " ";
			sAsk[1][0] = " 1-";
			sAsk[1][1] = _scsdm.getDisplayText( "BUY");
			sAsk[2][0] = " 2-";
			sAsk[2][1] = _scsdm.getDisplayText("SELL");
			buyOrSell = selectScreen(_scsdm.getDisplayText( "MERCHANT_ARRIVAL"), _scsdm.getDisplayText("BUY_OR_SELL"), sAsk);
			if(buyOrSell==1){
				buyFromMerchant(tBList, tSList);
			}else if(buyOrSell == 2){
				sellToMerchant(tBList, tSList);
			}else if(buyOrSell == 0){
				break;
			}
		}
		inventoryReconcile(tSList);
		
	}
	
	public TraderList makeBuyList(){
		int maxSize = 200;
		for(int i = 0; i<SCG.structures.size(); i++){
			if(SCG.structures.get(i).NAME.equals("dockingRig")){
				maxSize += SCG.structures.get(i).iWorkers*10;
			}
		}
		
		TraderList tOut = new TraderList(maxSize);
		for(int i = 0; i<SCG.iInv.size(); i++){
			if(SCG.iInv.get(i).MERCHANT_MAX_CARRY!=0){
				Good gThing = SCG.iInv.get(i).clone();
				gThing.iQuant = 0;
				gThing.iPrice = gThing.BASE_PRICE+rand.nextInt(2*gThing.BASE_PRICE);
				tOut.add(gThing);
			}
		}

		int iInfLoopCheck = 0;
		while(true){//getting in all the goods
			Good gTemp;
			int iCount = 0;
			int targ = rand.nextInt(SCG.iInv.merchFreqSum());
			//boolean hasRepeated == false;
			while(true){//randomly selecting a given good
				if(targ<=SCG.iInv.get(iCount).MERCHANT_FREQ){
					gTemp = SCG.iInv.get(iCount).clone();
					break;
				}else{
					targ -= SCG.iInv.get(iCount).MERCHANT_FREQ;
					iCount++;
				}
					
			}
			/*
			if((tOut.hasGoodByName(gTemp.sName))){
				break;
			}
			*/
			
			if(gTemp.MERCHANT_MAX_CARRY!=0&&tOut.getGoodByName(gTemp.sName).iQuant == 0){
				gTemp.iQuant = (gTemp.MERCHANT_MIN_CARRY + rand.nextInt(gTemp.MERCHANT_MAX_CARRY-gTemp.MERCHANT_MIN_CARRY));
				//gTemp.iPrice = gTemp.BASE_PRICE+rand.nextInt(2*gTemp.BASE_PRICE);
				//tOut.getGoodByName(gTemp.sName).iPrice = gTemp.iPrice;
				if(((gTemp.iQuant+tOut.getTotalQuant())>=(tOut.MaxSpace- (int) (tOut.MaxSpace * 0.15)))){
					break;
				}else{
					tOut.getGoodByName(gTemp.sName).iQuant = gTemp.iQuant;
				}
				
			}
			boolean bHasAll = true;
			for(int i = 0; i<SCG.iInv.size(); i ++){
				if(SCG.iInv.get(i).MERCHANT_MAX_CARRY!=0&&SCG.iInv.get(i).iQuant==0){
					//System.out.println(SCG.iInv.get(i).toString());
					bHasAll = false;
				}
			}
			
			if(bHasAll){
				break;
			}else if(iInfLoopCheck >=50){
				break;
			}
			iInfLoopCheck ++;
			
				
		}
		return tOut;
	}
	
	public Inventory makeSellList(TraderList tBuy){
		Inventory iOut = new Inventory();
		Good gTemp;
		for(int i = 0; i< SCG.iInv.size(); i++){
			if(SCG.iInv.get(i).MERCHANT_MAX_CARRY!=0){
				gTemp = SCG.iInv.get(i).clone();
				gTemp.iPrice = tBuy.getGoodByName(gTemp.sName).iPrice/2;
				if(gTemp.iPrice == 0){
					gTemp.iPrice = 1;
				}
				iOut.add(gTemp);
			}
			
		}
		return iOut;
	}
	
	public String getRecipeDisplay(Recipe r){
		String sOut = "";
		for(int i = 0; i <r.size(); i++){
			sOut += r.get(i).iQuant + " " + _scsdm.getDisplayText(SCG.iInv.getGoodByName(r.get(i).sName).sTextCode) + " ,";
			//sOut += r.get(i).iQuant + " " + _scsdm.getDisplayText(r.get(i).sTextCode) + " ,";
		}
		
		return sOut;
	}
	
	public void buyFromMerchant(TraderList tList, Inventory iList){
		
		while(true){
			int iEntriesToPrint = 0;
			for(int i = 0; i<tList.size(); i++){
				if(tList.get(i).iQuant!=0 && tList.get(i).MERCHANT_MAX_CARRY!=0){
					iEntriesToPrint++;
				}
			}
			String[][] aCatalogue = new String[iEntriesToPrint+2][4];
			//System.out.println(iEntriesToPrint + ",");
			aCatalogue[0][0] = " ";
			aCatalogue[0][1] = _scsdm.getDisplayText("GOOD");
			aCatalogue[0][2] = _scsdm.getDisplayText("PRICE");
			aCatalogue[0][3] = _scsdm.getDisplayText("AMMOUNT");
			int k = 0;
			//System.out.println(aCatalogue.length +".");
			for(int i = 0; i<tList.size(); i++){
				if(k>=(aCatalogue.length-1)){
					break;
				}
				if(k<10){
					//System.out.println(k);
					aCatalogue[k+1][0] = " "+(k+1)+"-";
				}else{
					aCatalogue[k+1][0] = (k+1)+"-";
				}
				if(tList.get(i).iQuant!=0 && tList.get(i).MERCHANT_MAX_CARRY!=0){
					aCatalogue[k+1][1] = _scsdm.getDisplayText(tList.get(i).sTextCode);
					aCatalogue[k+1][2] = Integer.toString(tList.get(i).iPrice);
					aCatalogue[k+1][3] = Integer.toString(tList.get(i).iQuant);
					k++;
				}
			}
			
			if(iEntriesToPrint<10){
				aCatalogue[iEntriesToPrint+1][0] = " "+(iEntriesToPrint+1)+"-";
			}else{
				aCatalogue[iEntriesToPrint+1][0] = (iEntriesToPrint+1)+"-";
			}
			
			aCatalogue[iEntriesToPrint+1][1] = _scsdm.getDisplayText("RECRUIT");
			aCatalogue[iEntriesToPrint+1][2] = Integer.toString(tList.recruitPrice);
			aCatalogue[iEntriesToPrint+1][3] = Integer.toString(tList.recruits);
			
			int bResponse = selectScreen(_scsdm.getDisplayText("BUY_WHAT"),_scsdm.getDisplayText("MONEY") + ": " +SCG.iInv.getGoodByName("Money").iQuant, aCatalogue);
			if(bResponse == (iEntriesToPrint+1)){
				int hires = _ioman.intIn(_scsdm.getDisplayText("HOW_MANY"));
				if(hires>=tList.recruits){
					hires=tList.recruits;
				}
				if((hires*tList.recruitPrice)>SCG.iInv.getGoodByName("Money").iQuant){
					_ioman.lineOut(_scsdm.getDisplayText("CANT_AFFORD"));
				}else{
					SCG.pop.gainPop(hires);
					tList.recruits-=hires;
					SCG.iInv.getGoodByName("Money").iQuant-=hires*tList.recruitPrice;
				}
				
			}else if(bResponse != 0){
				int iBuyAmount;
				String sWorking  = aCatalogue[bResponse][1];
				Good gAGood = null;
				for(int i =0; i<tList.size(); i++){
					if(sWorking.equals(_scsdm.getDisplayText(tList.get(i).sTextCode))){
						gAGood = tList.get(i);
					}
					
				}
				
				while(true){
					iBuyAmount = _ioman.intIn(_scsdm.getDisplayText("HOW_MANY"));
					if(iBuyAmount <=0){
						break;
					}
					if(iBuyAmount >gAGood.iQuant){
						iBuyAmount = gAGood.iQuant;
					}
					
					if(SCG.iInv.getGoodByName("Money").iQuant < iBuyAmount* gAGood.iPrice){
						_ioman.lineOut(_scsdm.getDisplayText("CANT_AFFORD"));
					}else{
						break;
					}
				}
				
				
				//System.out.println("You had " + iList.getGoodByName(gAGood.sName).iQuant);
				//System.out.println("It should increase by " + iBuyAmount);
				//System.out.println("It should now have " + (iBuyAmount + iList.getGoodByName(gAGood.sName).iQuant));
				iList.getGoodByName(gAGood.sName).iQuant += iBuyAmount;
				//System.out.println(iList.getGoodByName(gAGood.sName).toString());
				//System.out.println("You now have " + iList.getGoodByName(gAGood.sName).iQuant );
				//System.out.println("The merchant had " + gAGood.iQuant );
				gAGood.iQuant -= iBuyAmount;
				//System.out.println("The merchant now has " + gAGood.iQuant );
				SCG.iInv.getGoodByName("Money").iQuant -= iBuyAmount*gAGood.iPrice;
				//you need to create a good and add it if it does not already have it.
				
				
				
			}else if(bResponse ==0){
				break;
			}
		}
		
	}
	
	public void sellToMerchant(TraderList tList, Inventory iList){
		
		
		while(true){
			int iEntriesToPrint = 0;
			for(int i = 0; i<iList.size(); i++){
				if(iList.get(i).iQuant!=0 && iList.get(i).MERCHANT_MAX_CARRY!=0){
					iEntriesToPrint++;
				}
			}
			//System.out.println(iEntriesToPrint + ",");
			String[][] aCatalogue = new String[iEntriesToPrint+1][4];
			aCatalogue[0][0] = " ";
			aCatalogue[0][1] = _scsdm.getDisplayText("GOOD");
			aCatalogue[0][2] = _scsdm.getDisplayText("PRICE");
			aCatalogue[0][3] = _scsdm.getDisplayText("AMMOUNT");
			int k = 0;
			//System.out.println(aCatalogue.length +".");
			for(int i = 0; i<iList.size(); i++){
				if(k>=(aCatalogue.length-1)){
					break;
				}
				
				if(k<10){
					
					//System.out.println(k);
					aCatalogue[k+1][0] = " "+(k+1)+"-";
				}else{
					aCatalogue[k+1][0] = (k+1)+"-";
				}
				
				if(iList.get(i).iQuant!=0 && iList.get(i).MERCHANT_MAX_CARRY!=0){
					aCatalogue[k+1][1] = _scsdm.getDisplayText(iList.get(i).sTextCode);
					aCatalogue[k+1][2] = Integer.toString(iList.get(i).iPrice);
					aCatalogue[k+1][3] = Integer.toString(iList.get(i).iQuant);
					k++;
				}
				
			}
			
			int bResponse = selectScreen(_scsdm.getDisplayText("SELL_WHAT"),_scsdm.getDisplayText("ROOM") + ": " + Integer.toString(tList.MaxSpace-tList.getTotalQuant()), aCatalogue);
			if(bResponse != 0){
				int iSellAmount;
				String sWorking  = aCatalogue[bResponse][1];
				Good gAGood = null;
				for(int i =0; i<iList.size(); i++){
					if(sWorking.equals(_scsdm.getDisplayText(iList.get(i).sTextCode))){
						//System.out.println("it is");
						gAGood = iList.get(i);
						break;
					}
					
				}
				
				while(true){
					iSellAmount = _ioman.intIn(_scsdm.getDisplayText("HOW_MANY"));
					if(iSellAmount <=0){
						break;
					}
					if(iSellAmount >gAGood.iQuant){
						iSellAmount = gAGood.iQuant;
					}
					
					if(iSellAmount > tList.MaxSpace-tList.getTotalQuant()){
						_ioman.lineOut(_scsdm.getDisplayText("NO_ROOM"));
					}else{
						break;
					}
				}
				//System.out.println("You have " +gAGood.iQuant);
				gAGood.iQuant -= iSellAmount;
				//System.out.println("You have " +gAGood.iQuant);
				//System.out.println("Merchant had " + tList.getGoodByName(gAGood.sName).iQuant);
				tList.getGoodByName(gAGood.sName).iQuant += iSellAmount;
				//System.out.println("Merchant now has " + tList.getGoodByName(gAGood.sName).iQuant);
				
				SCG.iInv.getGoodByName("Money").iQuant += iSellAmount*gAGood.iPrice;
				
			}else if(bResponse == 0){
				break;
			}
		}
	}
	
	public void inventoryReconcile(Inventory iList){
		for(int i = 0; i<iList.size(); i++){
			if(SCG.iInv.getGoodByName(iList.get(i).sName) != null){
				//System.out.println("The prior value of good "+iList.get(i).sName + " Is " + SCG.iInv.getGoodByName(iList.get(i).sName).iQuant + " And the new value should be "+ iList.get(i).iQuant);
				SCG.iInv.getGoodByName(iList.get(i).sName).iQuant = iList.get(i).iQuant;
				//System.out.println("The new value is " + SCG.iInv.getGoodByName(iList.get(i).sName).iQuant);
			}else{
				SCG.iInv.add(iList.get(i).clone());
			}
		}
	}
	
	public void produce(){
		for(int i = 0; i<SCG.structures.size(); i++){
			
			SCG.structures.get(i).doProduction(SCG, currentEvent);
		}
	}
	
	public void saveCurrentGame(){
		
		
		ArrayList<String> sSaveNames = _savedm.getSaveNames();
		String[][] sSaves = new String[sSaveNames.size()+2][2];
		sSaves[0][0] = " ";
		sSaves[0][1] = _scsdm.getDisplayText("SAVE");
		for(int i = 0; i<sSaveNames.size(); i++){
			if(i<10){
				sSaves[i+1][0] =  " "+(i+1)+"-";
			}else{
				sSaves[i+1][0] = (i+1)+"-";
			}
			
			sSaves[i+1][1] = sSaveNames.get(i);
		}
		
		if(sSaveNames.size()<10){
			sSaves[sSaveNames.size()+1][0] =  " "+(sSaveNames.size()+1)+"-";
		}else{
			sSaves[sSaveNames.size()+1][0] = (sSaveNames.size()+1)+"-";
		}
		
		sSaves[sSaveNames.size()+1][1] = _scsdm.getDisplayText("NEW_SAVE");
		int iAns = selectScreen(_scsdm.getDisplayText("SELECT_SAVE"), " ", sSaves);
		if(iAns ==0){
			
		}else if(iAns<=sSaveNames.size()){
			_savedm.saveGame(SCG, sSaveNames.get(iAns-1));
		}else if(iAns==sSaveNames.size()+1){
			System.out.println(_scsdm.getDisplayText("NEW_SAVE_NAME"));
			String newName = _ioman.stringIn(_scsdm.getDisplayText("NEW_SAVE_NAME"));
			if(newName.equals("")){
				
			}else{
				_savedm.saveGame(SCG, newName);
			}
		}
		
		//_scsdm.saveGame(SCG, "DEFAULT");
		//System.out.println("Autosaving...");
	}
	
	public void delSave(){
		ArrayList<String> sSaveNames = _savedm.getSaveNames();
		String[][] sSaves = new String[sSaveNames.size()+1][2];
		sSaves[0][0] = " ";
		sSaves[0][1] = _scsdm.getDisplayText("SAVE");
		for(int i = 0; i<sSaveNames.size(); i++){
			if(i<10){
				sSaves[i+1][0] =  " "+(i+1)+"-";
			}else{
				sSaves[i+1][0] = (i+1)+"-";
			}
			
			sSaves[i+1][1] = sSaveNames.get(i);
		}
		/*
		if(sSaveNames.size()<10){
			sSaves[sSaveNames.size()][0] =  " "+(sSaveNames.size()+1)+"-";
		}else{
			sSaves[sSaveNames.size()][0] = (sSaveNames.size()+1)+"-";
		}
		*/
		//sSaves[sSaveNames.size()+1][1] = _scsdm.getDisplayText("NEW_SAVE");
		int iAns = selectScreen(_scsdm.getDisplayText("SELECT_LOAD"), " ", sSaves);
		if(iAns<=sSaveNames.size()){
			//System.out.println(sSaveNames.get(iAns-1));
			_savedm.deleteGameByName(sSaveNames.get(iAns-1));
			
		}
	}
	
	public void loadSave(){
		ArrayList<String> sSaveNames = _savedm.getSaveNames();
		String[][] sSaves = new String[sSaveNames.size()+1][2];
		sSaves[0][0] = " ";
		sSaves[0][1] = _scsdm.getDisplayText("SAVE");
		for(int i = 0; i<sSaveNames.size(); i++){
			if(i<10){
				sSaves[i+1][0] =  " "+(i+1)+"-";
			}else{
				sSaves[i+1][0] = (i+1)+"-";
			}
			
			sSaves[i+1][1] = sSaveNames.get(i);
		}
		/*
		if(sSaveNames.size()<10){
			sSaves[sSaveNames.size()][0] =  " "+(sSaveNames.size()+1)+"-";
		}else{
			sSaves[sSaveNames.size()][0] = (sSaveNames.size()+1)+"-";
		}
		*/
		//sSaves[sSaveNames.size()+1][1] = _scsdm.getDisplayText("NEW_SAVE");
		int iAns = selectScreen(_scsdm.getDisplayText("SELECT_LOAD"), " ", sSaves);
		if(iAns<=sSaveNames.size()){
			System.out.println("Loading");
			_savedm.loadGameByName(sSaveNames.get(iAns-1), SCG, _scsdm);
			
		}
		
	}
	
	public void buildNewStructure(){
		RecipeList possibleStructureRecipes = new RecipeList();
		for(int i = 0; i < StructList.size(); i++){
			//System.out.println(StructList.get(i).NAME + "XXX");
			possibleStructureRecipes.add(listOfRecipes.getRecipeByName(StructList.get(i).NAME));
		}
		//getRecipeDisplay();
		String[][] sDisplayText = new String[possibleStructureRecipes.size()+1][4];
		sDisplayText[0][0] = "";
		sDisplayText[0][1] = _scsdm.getDisplayText("BUILDING");
		sDisplayText[0][2] = _scsdm.getDisplayText("MAN_HOURS");
		sDisplayText[0][3] = _scsdm.getDisplayText("MATERIALS");
			
		for(int i = 0 ; i<possibleStructureRecipes.size(); i++){
			if(i<10){
				sDisplayText[i+1][0] =  " "+(i+1)+"-";
			}else{
				sDisplayText[i+1][0] = (i+1)+"-";
			}
		}
		
		for(int i = 0 ; i<possibleStructureRecipes.size(); i++){
			//System.out.println(possibleStructureRecipes.get(i).NAME + "XXXX");
			sDisplayText[i+1][1] = _scsdm.getDisplayText(StructList.getStructureByName(possibleStructureRecipes.get(i).NAME).TEXT_CODE);
		}
		
		for(int i = 0 ; i<possibleStructureRecipes.size(); i++){
			sDisplayText[i+1][2] = possibleStructureRecipes.get(i).MAN_HOURS + " ";
		}
		
		for(int i = 0 ; i<possibleStructureRecipes.size(); i++){
			sDisplayText[i+1][3] = getRecipeDisplay(possibleStructureRecipes.get(i));
		}
		
		
		//TESTING
		/*
		for(int i = 0; i<sDisplayText.length; i++){
			System.out.println(" "+i+" "+sDisplayText[i][1]);
		}
		*/
		//END TESTING
		int iAns = selectScreen(_scsdm.getDisplayText("NEW_BUILDING"),_scsdm.getDisplayText("SELECT_NEW_BUILDING"),sDisplayText);
		
		if(iAns == 0){
			
		}else{
			Recipe recip = possibleStructureRecipes.get(iAns-1);
			
			Structure bNewBuilding = StructList.getStructureByName(recip.NAME).clone();
			bNewBuilding.iCompleteness = 0;
			bNewBuilding.bComplete = false;
			boolean hasEnoughMats = checkHasGoods(possibleStructureRecipes.get(iAns-1));
			if(hasEnoughMats){
				SCG.structures.add(bNewBuilding);
				for(int i = 0; i<recip.size(); i++){
					Good gChange = SCG.iInv.getGoodByName(recip.get(i).sName);
					gChange.iQuant -= recip.get(i).iQuant;
				}
				
				
			}else{
				_ioman.lineOut(_scsdm.getDisplayText("INSUFFICIENT_MATERIALS"));
			}
		}
		
	}
	
	public boolean checkHasGoods(Recipe recip){
		boolean b = true;
		
		for(int i = 0; i<recip.size(); i++){
			Good gCheck = SCG.iInv.getGoodByName(recip.get(i).sName);
			b = (b&&gCheck.iQuant>=recip.get(i).iQuant);
		}
		return b;
		
	}
	
	public void removeBuilding(){
		String[][] sQuestion = new String[SCG.structures.size()+1][1];
		sQuestion[0][0] = " ";
		//sQuestion[0][1] = " ";
		for(int i = 1;i<=SCG.structures.size(); i++){
			if(i<10){
				
				sQuestion[i][0] =" "+i+"- " + _scsdm.getDisplayText(SCG.structures.get(i-1).TEXT_CODE);
				//System.out.println(SCG.structures.get(i-1).toString());
			}else{
				sQuestion[i][0] =i+"- " + _scsdm.getDisplayText(SCG.structures.get(i-1).TEXT_CODE);
				//System.out.println(SCG.structures.get(i-1).toString());
			}
		}
		int iDemoTarget = selectScreen(_scsdm.getDisplayText("DEMOLISH"), _scsdm.getDisplayText("DEMOLISH_SELECT"), sQuestion );
		
		if(iDemoTarget == 0){
			
		}else{
			SCG.structures.get(iDemoTarget-1).setWork(SCG.pop,0);
			SCG.structures.remove(iDemoTarget-1);
		}
		
	}
	
	

}
