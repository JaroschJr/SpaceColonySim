/**
 * Class to manage the execution of the the game and
 * direct the interface with the user.  This class
 * will be responsible for talking to the database,
 * user I/O, and will create and own the actual
 * game object.
 */
import java.util.Random;
import java.util.ArrayList;
public class SpaceColonyEngine implements ISCSError{
	//for Joey, compile with: Call "C:\Program Files (x86)\Java\jdk1.8.0_91\bin\javac.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim" SpaceColonyEngine.java
	//and run with Call "C:\Program Files (x86)\Java\jre1.8.0_91\bin\java.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim;C:\Joey's coding stuf\SpaceColonySim\sqlite-jdbc-3.18.0.jar" SpaceColonyEngine
	//For joey CONSTR woudl be jdbc:sqlite:C:\\Joey's coding stuf\\SpaceColonySim\\SpaceColonySim\\SCSDataBase.db
	private static String CONSTR = "";     //For Sean D:\\Dev\\JavaJoe\\SCS, for joey C:\\Joey's coding stuf\\SpaceColonySim\\SpaceColonySim;
	private SCSDataModule _scsdm;
	private SpaceColonyGame SCG;
	private ISCSIO _ioman;
	private RandomEventFactory rEventFactory;
	private RandomEventList events;
	private RandomEvent currentEvent;
	//to test to see if Structure and descendat classed were made correctly.
	private Structure testStructure;
	private RecipeList listOfRecipes;//For Testing.
	private InventoryFactory InvFact;
	private StructureFactory StructFact;
	private StructureList StructList;
	
	
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
		CONSTR = args[0];
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
	public void handleException(Exception thisException){

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

		_ioman = new SCSConsoleIO();

		//check if the database connection has been
		//established, if not then display an error
		//message and close the application
		if(!_scsdm.isConnected()){
			_ioman.lineOut("The database could not be connected to. Exiting game");
			//System.out.println("The data base could not be connected to. Exiting game");
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
		
		InvFact = new InventoryFactory(_scsdm);
		SCG.iInv = InvFact.getList();
		listOfRecipes = InvFact.getRecipeList();
		StructFact = new StructureFactory(_scsdm);
		StructList = StructFact.getStructureList();
		//get copies of starting structures.
		StructureList sList = new StructureList();
		sList.add(StructList.getStructureByName("farm").clone());
		sList.add(StructList.getStructureByName("oreMine").clone());
		SCG.structures = sList;
		SCG.pop = new Population();
		SCG.pop.gainPop(10);
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
					//load or save.
			while(done == false){
			System.out.println(_scsdm.getDisplayText("NEW_OR_LOAD"));
			String sInput = System.console().readLine();
			sInput = sInput.toUpperCase();
			switch(sInput){
				case "N":
					done = true;
					SCG.gameReset();
					SCG.iMerchantCountDown = rand.nextInt(5);
					break;

				case "L":
					System.out.println(_scsdm.getDisplayText("INCOMPLETE"));
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
		
		while(SCG.bIsOngoing == true){//Per the flowchart on the Cloud:
			report();
			//Tennative suggestion: Some kind of status update?
			currentEvent = events.generateEvent();
			//System.out.println(currentEvent.toString());
			currentEvent.performEvent(SCG, _ioman, _scsdm);
			//Freightery things
			//labour related things
			SCG.iTurnCount++;
			//truncate it so it does one and only one iteration.
			/*
			if (SCG.iTurnCount >20){
			SCG.bIsOngoing = false;//truncate it so it does one and only one iteration. Its looping infinitely caused issues during testing to complex to understand here.
			}
			*/
			_ioman.lineOut("Enter to continue");
			String SINN = _ioman.stringIn("");
			
			
		}
		System.out.println("It can run through one loop");
	}

	private boolean traderAriveOrNot(){
		if (SCG.iMerchantCountDown == 0){
			return true;
		}else{
			return false;
		}
	}
	
	private void report(){
		_ioman.lineOut(_scsdm.getDisplayText("TURN_REPORT_TURN") +getSpacer(20-_scsdm.getDisplayText("TURN_REPORT_TURN").length()-(int)Math.log10(SCG.iTurnCount+1))+ SCG.iTurnCount);
		_ioman.lineOut(_scsdm.getDisplayText("TURN_REPORT_POPULATION") +getSpacer(20-_scsdm.getDisplayText("TURN_REPORT_POPULATION").length()-(int)Math.log10(SCG.pop.size()+1))+ SCG.pop.size());
		for(int i = 0; i<SCG.iInv.size(); i++){
			int iSpacesNeeded;
			String sOut = "";
			Good gPrint = SCG.iInv.get(i);
			if(gPrint.bPublish){
				sOut = _scsdm.getDisplayText(gPrint.sTextCode);
				String sPlaceHolder = getSpacer(20-sOut.length()-(int)Math.log10(gPrint.iQuant+1));
				
				sOut += sPlaceHolder + gPrint.iQuant;
				_ioman.lineOut(sOut);
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
			sOut += getSpacer(20-sOut.length()-(int)Math.log10(iNum+1));
			sOut += iNum;
			_ioman.lineOut(sOut);
			}
			
		}
		
	}
	public String getSpacer(int iSize){
		String returnString = "";
		for(int i = 0; i<iSize; i++){
			returnString+=" ";
		}
		return returnString;
	}
}