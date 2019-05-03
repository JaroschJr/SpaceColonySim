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
		sList.add(StructList.getStructureByName("oreMine").clone());
		sList.add(StructList.getStructureByName("farm").clone());
		sList.add(StructList.getStructureByName("farm").clone());
		sList.add(StructList.getStructureByName("farm").clone());
		
		SCG.structures = sList;
		SCG.pop = new Population();
		SCG.pop.gainPop(10);
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
			//truncate it so it does one and only one iteration.
			/*
			if (SCG.iTurnCount >20){
			SCG.bIsOngoing = false;//truncate it so it does one and only one iteration. Its looping infinitely caused issues during testing to complex to understand here.
			}
			*/
			
			_ioman.lineOut("");
			viewAndSetProduction();
			_ioman.lineOut("");
			
			_ioman.lineOut("Enter to continue");
			String SINN = _ioman.stringIn("");
			merchantThings();
			_ioman.lineOut("Enter to continue");
			SINN = _ioman.stringIn("");
			produce();
			currentEvent.performEvent(SCG, _ioman, _scsdm);
			validateStructures();
			SCG.iTurnCount++;
		}
	}

	private boolean traderAriveOrNot(){
		if (SCG.iMerchantCountDown == 0){
			return true;
		}else{
			return false;
		}
	}
	
	public void validateStructures(){
		for(int i = 0; i<SCG.structures.size(); i++){
			SCG.structures.get(i).assignValidate();
		}
	}
	
	private void report(){
		int answer = 0;
		_ioman.lineOut(_scsdm.getDisplayText("TURN_REPORT_TURN") +getSpacer(20-_scsdm.getDisplayText("TURN_REPORT_TURN").length()-(int)Math.log10(SCG.iTurnCount+1))+ SCG.iTurnCount);
		_ioman.lineOut(_scsdm.getDisplayText("TURN_REPORT_POPULATION") +getSpacer(20-_scsdm.getDisplayText("TURN_REPORT_POPULATION").length()-(int)Math.log10(SCG.pop.size()+1))+ SCG.pop.size());
		for(int i = 0; i<SCG.iInv.size(); i++){
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
	
	
	
	public int selectScreen(String prelude, String question, String[][] text){
		int iAnswer = 0;
		_ioman.lineOut(prelude);
		
		for(int i = 0; i<text.length; i++){
			String sOut = "";
			if(i==1){
				_ioman.lineOut(" 0-" + getSpacer(20-" 0-".length()) + _scsdm.getDisplayText("CANCEL"));
			}
			for(int j = 0; j<text[i].length; j++){
				//System.out.println("line " + i + " column " +j);
				sOut += text[i][j]+ getSpacer(20-text[i][j].length());
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
			String[][] structReport = new String[SCG.structures.size()+1][3];
			structReport[0][0] = _scsdm.getDisplayText("BUILDING");
			structReport[0][1] = _scsdm.getDisplayText("WORKERS");
			structReport[0][2] = _scsdm.getDisplayText("RECIPE");
				for(int i = 1;i<=SCG.structures.size(); i++){
				
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
					
					if(SCG.structures.get(i-1) instanceof ProductionBuilding){
						ProductionBuilding tempPBSlot = (ProductionBuilding) SCG.structures.get(i-1);
						//System.out.println(SCG.structures.get(i-1));
					
						//System.out.println(tempPBSlot.toString());
						if(tempPBSlot.currentRecipe!=null){
							structReport[i][2] = tempPBSlot.currentRecipe.NAME;
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
			Structure bWorkingBuilding;
			int iAnswer = selectScreen(_scsdm.getDisplayText("SELECT_CURRENT_ASSIGNMENT"), _scsdm.getDisplayText("WHICH_TO_CHANGE" ) + ". "+ SCG.pop.howManyUnassigned() + " " + _scsdm.getDisplayText("IDLE_WORKERS"), structReport);
			if(iAnswer>0&&iAnswer <SCG.structures.size()+1){
				bWorkingBuilding = SCG.structures.get(iAnswer-1);
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
				
				//int iNewWorkers =_ioman.intIn(_scsdm.getDisplayText("HOW_MANY_WORKERS")); 
				if(iNewWorkers>0){
					if(bWorkingBuilding instanceof ProductionBuilding){
						ProductionBuilding bTempo = (ProductionBuilding) bWorkingBuilding;
						if(bTempo.sPosibleRecipes.size() == 1){
							bTempo.setWork(SCG.pop, iNewWorkers, listOfRecipes.getRecipeByName(bTempo.sPosibleRecipes.get(0)));
						}else{
							String[][] sRecipePick = new String[bTempo.sPosibleRecipes.size()+1][3];
							sRecipePick[0][0] = _scsdm.getDisplayText("RECIPE");
							sRecipePick[0][1] = _scsdm.getDisplayText("MAN_HOURS");
							sRecipePick[0][2] = _scsdm.getDisplayText("MATERIALS");
							
							for(int i = 0; i <bTempo.sPosibleRecipes.size(); i++){
								sRecipePick[i+1][0] = _scsdm.getDisplayText(listOfRecipes.getRecipeByName(bTempo.sPosibleRecipes.get(i)).TEXT_CODE);
								sRecipePick[i+1][1] = Integer.toString(listOfRecipes.getRecipeByName(bTempo.sPosibleRecipes.get(i)).MAN_HOURS);
								sRecipePick[i+1][2] = getRecipeDisplay(listOfRecipes.getRecipeByName(bTempo.sPosibleRecipes.get(i)));
							}
							
							int recipResponse = 0;
							while(recipResponse<=0||recipResponse>bTempo.sPosibleRecipes.size()){
								 recipResponse = selectScreen(_scsdm.getDisplayText("WHICH_RECIPE"), _scsdm.getDisplayText("WHICH_RECIPE"), sRecipePick);
							}
							bTempo.setWork(SCG.pop, iNewWorkers, listOfRecipes.getRecipeByName(bTempo.sPosibleRecipes.get(recipResponse-1)));
						}
					}else{
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
			}else{
				break;
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
	
	public void merchantThings(){
		TraderList tBList = makeBuyList();
		Inventory tSList = makeSellList(tBList);
		System.out.println("merchant toString " + tBList.toString());
		System.out.println("inventory toString " +tSList.toString());
		
	}
	
	public TraderList makeBuyList(){
		TraderList tOut = new TraderList();
		
		while(true){//getting in all the goods
			Good gTemp;
			int iCount = 0;
			int targ = rand.nextInt(SCG.iInv.merchFreqSum());
			while(true){//randomly selecting a given good
				if(targ<=SCG.iInv.get(iCount).MERCHANT_FREQ){
					gTemp = SCG.iInv.get(iCount).clone();
					break;
				}else{
					targ -= SCG.iInv.get(iCount).MERCHANT_FREQ;
					iCount++;
				}
					
			}
				
			if((tOut.getGoodByName(gTemp.sName)!= null)&&gTemp.MERCHANT_CARRY!=0){
				gTemp.iQuant = 10*(1+ rand.nextInt(gTemp.MERCHANT_CARRY-1));
				if(((gTemp.iQuant+tOut.getTotalQuant())>=tOut.MaxSpace)){
					break;
				}else{
					gTemp.iPrice = gTemp.BASE_PRICE+rand.nextInt(2*gTemp.BASE_PRICE);
					tOut.add(gTemp);
				}
				
			}
			
			if(tOut.size()<SCG.iInv.size()){

			}else{
				break;
			}
				
		}
		return tOut;
	}
	
	public Inventory makeSellList(TraderList tBuy){
		Inventory iOut = new Inventory();
		Good gTemp;
		for(int i = 0; i< SCG.iInv.size(); i++){
			//System.out.println("Making Sell List Iteration" + i);
			//System.out.println(SCG.iInv.get(i).MERCHANT_CARRY+"!="+0+"&&"+SCG.iInv.get(i).iQuant+"!="+0);
			if(SCG.iInv.get(i).MERCHANT_CARRY!=0&&SCG.iInv.get(i).iQuant!=0){
				gTemp = SCG.iInv.get(i).clone();
				if(tBuy.getGoodByName(gTemp.sName)!=null){
					gTemp.iPrice = tBuy.getGoodByName(gTemp.sName).iPrice/2;
				}else{
					gTemp.iPrice = gTemp.BASE_PRICE+rand.nextInt(gTemp.BASE_PRICE); 
				}
				iOut.add(gTemp);
			}
			
		}
		return iOut;
	}
	
	public String getRecipeDisplay(Recipe r){
		String sOut = "";
		for(int i = 0; i <r.size(); i++){
			sOut += r.get(i).iQuant + " " + _scsdm.getDisplayText(r.get(i).sTextCode) + " ,";
		}
		
		return sOut;
	}
	
	public void produce(){
		for(int i = 0; i<SCG.structures.size(); i++){
			SCG.structures.get(i).doProduction(SCG);
		}
	}
}