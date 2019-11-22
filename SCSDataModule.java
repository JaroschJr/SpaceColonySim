import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Class responsible for facilitating communication
 * to and from the database.
 */
public class SCSDataModule{
	public ISCSError errorHandler;
	private String _Language;

	public String getLanguage(){
		return _Language;
	}
	public void setLanguage(String language){
		_Language = language;
	}
	private Connection _conn;

	//connection string property
	private String _connectionStr;

	public String getConnectionString()
	{
		return _connectionStr;
	}

	public void setConnectionString(String value)
	{
	try{
		if(_conn != null){
		  _conn.close();
		  _conn = null;
		}
	}catch(SQLException ExcepVar){
		errorHandler.handleException( ExcepVar );
		}
		_connectionStr = value;
	}

	/**
	 * Determines if the database connection
	 * is currently active.
	 * @return Returns true is the database is
	 *         connnected; false if not.
	 */
	public boolean isConnected(){
		if(_conn != null){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Attempts to extablish a connection to the
	 * database using the connection string
	 * property.
	 * @return Returns true if the arrempt was
	 *         successful; false if not.
	 */
	public boolean connect(){
		try{
			_conn = DriverManager.getConnection(_connectionStr);
		}//end try
		catch(SQLException sqle){
			_conn = null;
			errorHandler.handleException( sqle);

		}//end catch sqle

		return isConnected();
	}

	/**
	 * Terminates the active connection to the
	 * database.
	 */
	public void disConnect(){
		try{
			if (_conn != null){
			_conn.close();
			}
		}catch(SQLException eException){
			errorHandler.handleException( eException );
		}
	}
	
	public static boolean getBoolean(ResultSet resultSet, String name)throws SQLException{
		int i = resultSet.getInt(name);
		return (i == 1);
	}
	
	public static void setBoolean(ResultSet resultSet, String name, boolean value)throws SQLException{
		resultSet.updateInt(name, (value) ? 1 : 0);	
	} 

	/**
	 * Retrieves a result set from the database
	 * given a SQL statement.
	 * @param sql The SQL statement to run.
	 * @return Returns a result set of data.
	 */
	public ResultSet getResultSet(String sql){
		ResultSet _rs = null;

		try{
			Statement _stat = _conn.createStatement();
			_rs = _stat.executeQuery(sql);
		}//end try
		catch(SQLException sqle){
			errorHandler.handleException(sqle);
		}//end catch sqle

		return _rs;
	}

	/**
	 * Retrieves text for display purposes from
	 * the database based on the current language
	 * preferece setting.
	 * @param textCode The code specifying the
	 *                 text to retrieve.
	 * @return Returns the text to display.
	 */
	public String getDisplayText(String textCode)
	{
		String sProxy = "PROXY";
		String SQLQuery = "SELECT DISPLAY_TEXT FROM SCS_Fluf_Text WHERE LANGUAGE = '" + _Language + "' AND TEXT_CODE = '" + textCode + "'";
		try{
			Statement sStatement = _conn.createStatement();
			ResultSet _FlufText = sStatement.executeQuery(SQLQuery);
			sProxy = _FlufText.getString("DISPLAY_TEXT");
		}catch(SQLException EXCEPTIONVARIABLE){
			errorHandler.handleException( EXCEPTIONVARIABLE );
		}
		// finish this, trychatch stuf, the like, ect.
		return sProxy;
	}

	/**
	 * Retrieves an array of languages supported
	 * by the database.
	 * @return Returns a matrix of languages and
	 *         codes which are supported by the
	 *         database.
	 */
	public String[][] getLanguages()
	{
		String[][] _langs = null;
		String _count_query = "SELECT COUNT(*) AS CNT FROM SCS_LANGUAGES";
		String _lang_query = "SELECT NAME, CODE FROM SCS_LANGUAGES";
		int _count = 0;

		try{
			Statement _stat = _conn.createStatement();

			//first get the record count so we can initialize
			//the result array
			ResultSet _rs = _stat.executeQuery(_count_query);
			_count = _rs.getInt("CNT");
			_langs = new String[_count][2];

			//now get the data
			_rs = _stat.executeQuery(_lang_query);
			while(_rs.next()){
				int i = _rs.getRow() - 1;
				_langs[i][0] = _rs.getString("CODE");
				_langs[i][1] = _rs.getString("NAME");
			}//end while
		}//end try
		catch(SQLException sqle){
			errorHandler.handleException(sqle);
		}//end catch)

		return _langs;
	}
	
	public String getGuid(){
		String sOut;
		UUID uuid = UUID.randomUUID();
		sOut = uuid.toString();
		return sOut;
	}
	
	public void saveGame(SpaceColonyGame scg, String sName){
		
		try{
			//System.out.println("Gets to 1");
			boolean bNew = true;
			if(scg.sGuid == null){
				bNew = true;
				scg.sGuid = getGuid();
			}else{
				bNew = false;
			}
			Statement sStatement = _conn.createStatement();
			//System.out.println("Gets to 2");
				//Eliminate existing save.
			//System.out.println("SELECT GUID FROM SCS_SAVE_GAMES WHERE SAVE_NAME IS '"+sName+"'");
			ResultSet rOtherGuid = getResultSet("SELECT GUID FROM SCS_SAVE_GAMES WHERE SAVE_NAME IS '"+sName+"'");
			//System.out.println("Gets to 3");
			while(rOtherGuid.next()){
				
				String oldGuid = rOtherGuid.getString("GUID");
				//System.out.println("DELETE FROM SCS_SAVE_GAMES WHERE GUID IS '"+oldGuid+"'");
				//System.out.println("DELETE FROM SCS_SAVE_ITEMS WHERE SAVE_GUID IS '"+oldGuid+"'");
				//System.out.println("DELETE FROM SCS_SAVE_STRUCTURES WHERE SAVE_GUID IS '"+oldGuid+"'");
				
				
				
				sStatement.execute("DELETE FROM SCS_SAVE_GAMES WHERE GUID IS '"+oldGuid+"'");
				sStatement.execute("DELETE FROM SCS_SAVE_ITEMS WHERE SAVE_GUID IS '"+oldGuid+"'");
				sStatement.execute("DELETE FROM SCS_SAVE_STRUCTURES WHERE SAVE_GUID IS '"+oldGuid+"'");
			}
			//System.out.println("Gets to 4");
			//Sample SQL:
			//For the Save
			//System.out.println("INSERT INTO SCS_SAVE_GAMES(GUID, SAVE_NAME, TURN, POP, MERCH_COUNT) VALUES (" + scg.sGuid +", "+sName+", " +scg.iTurnCount+", "+scg.pop.size()+", "+scg.iMerchantCountDown+")");
			sStatement.execute("INSERT INTO SCS_SAVE_GAMES(GUID, SAVE_NAME, TURN, POP, MERCH_COUNT, MORALE) VALUES ('" + scg.sGuid +"', '"+sName+"', " +scg.iTurnCount+", "+scg.pop.size()+", "+scg.iMerchantCountDown+", "+ scg.subMorale+")");
			for(int i = 0; i<scg.structures.size(); i++){
				//System.out.println("getting entry " + i +" of "+scg.structures.size());
				//System.out.println(scg.structures.get(i).toString());
				
				if(scg.structures.get(i) instanceof ProductionBuilding){
					ProductionBuilding temp = (ProductionBuilding) scg.structures.get(i);
					if(temp.currentRecipe!=null){
						
						sStatement.execute("INSERT INTO SCS_SAVE_STRUCTURES(GUID, SAVE_GUID, FACTORY, WORKERS, RECIPE, COMPLETENESS) VALUES('"+getGuid()+"', '"+scg.sGuid+"', '" + temp.NAME+"', "+temp.iWorkers+", '"+ temp.currentRecipe.NAME+"', '"+ temp.iCompleteness+ "')");
					}else{
						
						sStatement.execute("INSERT INTO SCS_SAVE_STRUCTURES(GUID, SAVE_GUID, FACTORY, WORKERS, RECIPE, COMPLETENESS) VALUES('"+getGuid()+"', '"+scg.sGuid+"', '" + temp.NAME+"', "+temp.iWorkers+", '-'"+", '"+ temp.iCompleteness+ "')");

					}
				}else{
					sStatement.execute("INSERT INTO SCS_SAVE_STRUCTURES(GUID, SAVE_GUID, FACTORY, WORKERS, COMPLETENESS) VALUES('"+getGuid()+"', '"+scg.sGuid+"', " + scg.structures.get(i).NAME+"', "+scg.structures.get(i).iWorkers+"', '"+ scg.structures.get(i).iCompleteness+ "')");
				}
			}
			
			
			for(int i = 0; i<scg.iInv.size(); i++){
				//System.out.println("INSERT INTO SCS_SAVE_ITEMS(GUID, SAVE_GUID, QUANTITY, ITEM) VALUES('"+getGuid()+"', '"+scg.sGuid+"',"+scg.iInv.get(i).iQuant+",'"+scg.iInv.get(i).sName+"')");
				sStatement.execute("INSERT INTO SCS_SAVE_ITEMS(GUID, SAVE_GUID, QUANTITY, ITEM) VALUES('"+getGuid()+"', '"+scg.sGuid+"',"+scg.iInv.get(i).iQuant+",'"+scg.iInv.get(i).sName+"')");
			}
			
			
			
		}catch(SQLException sqle){
			errorHandler.handleException(sqle);
			System.out.println(sqle.getMessage());
		}
		
	}
	
	public ArrayList<String> getSaveNames(){
		ArrayList<String> outArray = new ArrayList<String>();
		try{	
			ResultSet rSet = getResultSet("select SAVE_NAME FROM SCS_SAVE_GAMES");
			
			while(rSet.next()){
				//System.out.println(rSet.getString("SAVE_NAME"));
				outArray.add(rSet.getString("SAVE_NAME"));
			}
		}catch(SQLException sqle){
			errorHandler.handleException(sqle);
			System.out.println(sqle.getMessage());
		}
		return outArray;
		
	}
	
	
	
	public void loadGameByName(String sName, SpaceColonyGame scg, SCSDataModule masterDB){
		try{
			InventoryFactory invfact = new InventoryFactory(masterDB);
			StructureFactory structFact = new StructureFactory(masterDB);
			ResultSet rTargSave = getResultSet("SELECT * FROM SCS_SAVE_GAMES WHERE SAVE_NAME IS '"+sName+"'");
			String sTargGuid = rTargSave.getString("GUID");
			scg.bIsOngoing = true;
			scg.sGuid = sTargGuid;
			scg.iTurnCount = rTargSave.getInt("TURN");
			scg.iMerchantCountDown = rTargSave.getInt("MERCH_COUNT");
			scg.subMorale = rTargSave.getInt("MORALE");
			int iNewPop = rTargSave.getInt("POP");
			scg.pop = new Population();
			scg.pop.gainPop(iNewPop);
			ResultSet rInvent = getResultSet("SELECT * FROM SCS_SAVE_ITEMS WHERE SAVE_GUID IS '"+sTargGuid+"'");
			
			while(rInvent.next()){ 
				Good currentGood = scg.iInv.getGoodByName(rInvent.getString("ITEM"));
				currentGood.iQuant = rInvent.getInt("QUANTITY");
				
			}
			
			ResultSet rStructures = getResultSet("SELECT * FROM SCS_SAVE_STRUCTURES WHERE SAVE_GUID IS '"+sTargGuid+"'");
			StructureList masterStructList = structFact.getStructureList();
			RecipeList rList = invfact.getRecipeList();
			scg.structures = new StructureList();
			while(rStructures.next()){
				Structure sIn;
				String sname = rStructures.getString("FACTORY");
				sIn = masterStructList.getStructureByName(sname).clone();
				sIn.iCompleteness = rStructures.getInt("COMPLETENESS");
				//System.out.println("Loading" + sIn.NAME);
				//System.out.println("Completeness "+sIn.iCompleteness+"<"+sIn.HOURS_TO_BUILD+" Needed");
				if(sIn.iCompleteness<sIn.HOURS_TO_BUILD){
					sIn.bComplete = false;
					//System.out.println("false = " +sIn.bComplete);
					sIn.bBuildingSelf = true;
					//System.out.println("true = " +sIn.bBuildingSelf);
					sIn.setWork(scg.pop, rStructures.getInt("WORKERS"));
					scg.structures.add(sIn);
				}else if((sIn instanceof ProductionBuilding)&& (rStructures.getInt("WORKERS")!=0)){
					ProductionBuilding bTempo = (ProductionBuilding) sIn;
					bTempo.setWork(scg.pop, rStructures.getInt("WORKERS"), rList.getRecipeByName(rStructures.getString("RECIPE")));
					scg.structures.add(bTempo);
				}else if(rStructures.getInt("WORKERS")!=0){
					sIn.setWork(scg.pop, rStructures.getInt("WORKERS"));
					scg.structures.add(sIn);
				}else{
					scg.structures.add(sIn);
				}
				
				
			}
			
			
		}catch(SQLException sqle){
			errorHandler.handleException(sqle);
			System.out.println(sqle.getMessage());
		}
		
			
	}
	
	public void deleteGameByName(String sName){
		try{
			ResultSet rTargSave = getResultSet("SELECT GUID FROM SCS_SAVE_GAMES WHERE SAVE_NAME IS '"+sName+"'");
			String sTargGuid = rTargSave.getString("GUID");
			Statement sStatement = _conn.createStatement();
			
			sStatement.execute("DELETE FROM SCS_SAVE_GAMES WHERE GUID IS '"+sTargGuid+"'");
			sStatement.execute("DELETE FROM SCS_SAVE_ITEMS WHERE SAVE_GUID IS '"+sTargGuid+"'");
			sStatement.execute("DELETE FROM SCS_SAVE_STRUCTURES WHERE SAVE_GUID IS '"+sTargGuid+"'");
		}catch(SQLException sqle){
			errorHandler.handleException(sqle);
			System.out.println(sqle.getMessage());
		}
	}
	
}