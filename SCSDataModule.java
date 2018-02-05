import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

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

	/**
	 * Retrieves text for display purposes from
	 * the database based on the current language
	 * preferece setting.
	 * @param textCode The code specifying the
	 *                 text to retrieve.
	 * @return Returns the texxt to display.
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
}