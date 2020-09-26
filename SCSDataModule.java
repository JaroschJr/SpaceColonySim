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
		errorHandler.handleException( ExcepVar, "", false );
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
			errorHandler.handleException( sqle, _connectionStr, false);

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
			errorHandler.handleException( eException, "", false  );
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
			errorHandler.handleException(sqle, "", false );
		}//end catch sqle

		return _rs;
	}
	
	/**
	 * Retrieves the save version of the database.
	 */
	public String getSaveVersion() {
		ResultSet rs = null;
		String result = null;
		try {
			Statement st = _conn.createStatement();
			rs = st.executeQuery("SELECT SAVE_VERSION FROM SCS_VERSIONS LIMIT 1");
			result = rs.getString("SAVE_VERSION");
		}//end try
		catch(SQLException sqle) {
			result = sqle.getMessage();
		}//end catch sqle
		
		return result;
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
			errorHandler.handleException( EXCEPTIONVARIABLE, SQLQuery, false  );
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
			errorHandler.handleException(sqle, "", false );
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
		String lastSQLString=" ";
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
			String sGameSaveTemplate = "INSERT INTO SCS_SAVE_GAMES(GUID, SAVE_NAME, TURN, POP, MERCH_COUNT, MORALE, DEBT, PAYMENTS_MISSED) VALUES ( '%1$s', '%2$s', %3$s , %4$s , %5$s , %6$s , %7$s, %8$s) ";
			String sSQLThing = String.format(sGameSaveTemplate, scg.sGuid, sName, scg.iTurnCount, scg.pop.size(), scg.iMerchantCountDown, scg.subMorale, scg.iDebt, scg.iDebtMissCount);
			//sStatement.execute("INSERT INTO SCS_SAVE_GAMES(GUID, SAVE_NAME, TURN, POP, MERCH_COUNT, MORALE) VALUES ('" + scg.sGuid +"', '"+sName+"', " +scg.iTurnCount+", "+scg.pop.size()+", "+scg.iMerchantCountDown+", "+ scg.subMorale+")");
			sStatement.execute(sSQLThing);
			for(int i = 0; i<scg.structures.size(); i++){
				//System.out.println("getting entry " + i +" of "+scg.structures.size());
				//System.out.println(scg.structures.get(i).toString());
				String sSQLStructSave = "INSERT INTO SCS_SAVE_STRUCTURES(GUID, SAVE_GUID, FACTORY, WORKERS, RECIPE, COMPLETENESS) VALUES( '%1$s' , '%2$s', '%3$s' , %4$s , %5$s , %6$s )";
				if(scg.structures.get(i) instanceof ProductionBuilding){
					ProductionBuilding temp = (ProductionBuilding) scg.structures.get(i);
					if(temp.currentRecipe!=null){
						String sEntry = String.format(sSQLStructSave,getGuid(),scg.sGuid, temp.NAME, temp.iWorkers, "'"+ temp.currentRecipe.NAME+"'",temp.iCompleteness );
						//sStatement.execute("INSERT INTO SCS_SAVE_STRUCTURES(GUID, SAVE_GUID, FACTORY, WORKERS, RECIPE, COMPLETENESS) VALUES('"+getGuid()+"', '"+scg.sGuid+"', '" + temp.NAME+"', "+temp.iWorkers+", '"+ temp.currentRecipe.NAME+"', '"+ temp.iCompleteness+ "')");
						lastSQLString = sEntry;
						sStatement.execute(sEntry);
					}else{
						String sEntry = String.format(sSQLStructSave,getGuid(),scg.sGuid, temp.NAME, temp.iWorkers, "null" ,temp.iCompleteness );
						lastSQLString = sEntry;
						sStatement.execute(sEntry);
//INSERT INTO SCS_SAVE_STRUCTURES(GUID, SAVE_GUID, FACTORY, WORKERS, RECIPE, COMPLETENESS) VALUES( 'd4b2358e-b134-43f9-9de4-0ff0f4cac1f6' , '9e0be399-fa8d-4e80-8792-7ab734e39034', 'oreMine' , 5 , 'Ore' , 10 )
					}
				}else{
					String sEntry = String.format(sSQLStructSave,getGuid(),scg.sGuid, scg.structures.get(i).NAME, scg.structures.get(i).iWorkers, "null" ,scg.structures.get(i).iCompleteness );
					lastSQLString = sEntry;
					sStatement.execute(sEntry);
				}
			}
			
			
			for(int i = 0; i<scg.iInv.size(); i++){
				//System.out.println("INSERT INTO SCS_SAVE_ITEMS(GUID, SAVE_GUID, QUANTITY, ITEM) VALUES('"+getGuid()+"', '"+scg.sGuid+"',"+scg.iInv.get(i).iQuant+",'"+scg.iInv.get(i).sName+"')");
				String sItemIn = "INSERT INTO SCS_SAVE_ITEMS(GUID, SAVE_GUID, QUANTITY, ITEM) VALUES('%1$s', '%2$s',%3$s,'%4$s')";
				sStatement.execute("INSERT INTO SCS_SAVE_ITEMS(GUID, SAVE_GUID, QUANTITY, ITEM) VALUES('"+getGuid()+"', '"+scg.sGuid+"',"+scg.iInv.get(i).iQuant+",'"+scg.iInv.get(i).sName+"')");
			}
			
			
			
		}catch(SQLException sqle){
			errorHandler.handleException(sqle, lastSQLString, true );
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
			errorHandler.handleException(sqle, "", false );
			//System.out.println(sqle.getMessage());
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
			scg.iDebt = rTargSave.getInt("DEBT");
			scg.iDebtMissCount = rTargSave.getInt("PAYMENTS_MISSED");
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
			errorHandler.handleException(sqle, "", false );
		
			
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
			errorHandler.handleException(sqle , "", false );
		}
	}
	
	public void saveGameTurnLog(SpaceColonyGame scg){
		//VALUES(ID, GAME, TURN, POPULATION, MONEY, MORALE, FOOD, WATER, ORE, ICE)
		String turnGUID = getGuid();
		int money = scg.iInv.getGoodByName(SCSEnum.eItems.Money.name()).iQuant;
		int food = scg.iInv.getGoodByName(SCSEnum.eItems.Food.name()).iQuant;
		int water = scg.iInv.getGoodByName(SCSEnum.eItems.Water.name()).iQuant;
		int ore = scg.iInv.getGoodByName(SCSEnum.eItems.Ore.name()).iQuant;
		int ice = scg.iInv.getGoodByName(SCSEnum.eItems.Ice.name()).iQuant;
		int debt = scg.iDebt;
		String sql = "INSERT INTO SCS_GAME_LOGS VALUES(\"%1$s\", \"%2$s\", %3$d, %4$d, %5$d, %6$d, %7$d, %8$d, %9$d, %10$d, %11$d)";
		String exeSQL = String.format(sql, turnGUID, scg.sGuid, scg.iTurnCount, scg.pop.size(), money, scg.subMorale, food, water, ore, ice, debt);
		
		try{
			Statement s = _conn.createStatement();
			s.execute(exeSQL);
		}//end try
		catch(SQLException sqle){
			errorHandler.handleException(sqle , exeSQL, false );
		}//end catch sqle
	}
	
}