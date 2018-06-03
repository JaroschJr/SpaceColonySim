/**
 * Class to manage the execution of the the game and
 * direct the interface with the user.  This class
 * will be responsible for talking to the database,
 * user I/O, and will create and own the actual
 * game object.
 */
import java.util.Random;
public class SpaceColonyEngine implements ISCSError{
	//for Joey, compile with: Call "C:\Program Files (x86)\Java\jdk1.8.0_91\bin\javac.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim" SpaceColonyEngine.java
	//and run with Call "C:\Program Files (x86)\Java\jre1.8.0_91\bin\java.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim;C:\Joey's coding stuf\SpaceColonySim\sqlite-jdbc-3.18.0.jar" SpaceColonyEngine
	//For joey CONSTR woudl be jdbc:sqlite:C:\\Joey's coding stuf\\SpaceColonySim\\SpaceColonySim\\SCSDataBase.db
	private static String CONSTR = "";     //For Sean D:\\Dev\\JavaJoe\\SCS, for joey C:\\Joey's coding stuf\\SpaceColonySim\\SpaceColonySim;
	private SCSDataModule _scsdm;
	private SpaceColonyGame SCG;
	private ISCSIO _ioman;
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
			_ioman.lineOut("The data base could not be connected to. Exiting game");
			//System.out.println("The data base could not be connected to. Exiting game");
			System.exit(1);
		}//end if
	}

	/**
	 * Creates the game object and prepares it
	 * for a playthrough, either initializing
	 * a new game or loading a previous game.
	 */
	private void launchGame(){
		SCG = new SpaceColonyGame();

		getLanguagePreference();
		loadOrNew();

		System.out.println(_scsdm.getDisplayText("INVENTORY"));
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

	private boolean traderAriveOrNot(){
		if (SCG.iMerchantCountDown == 0){
			return true;
		}else{
			return false;
		}
	}
}