import java.sql.*;
public class RandomEvent {
	 int iProbWeight;//database: OCCURANCE
	 String sGuid;//database: ID
	 String sFluffAccess;//database: TEXT_ID
	 
	 public static final String FIELD_OCCURANCE = "OCCURANCE";
	 public static final String FIELD_ID = "ID";
	 public static final String FIELD_TEXT_ID = "TEXT_ID";
	 
	 //constructor
	 public RandomEvent(){	
	 
	 }
	 
	 public String toString(){
		 String sReturnString = ", Probabillity" + iProbWeight + ", Guid " + sGuid + ", Fluff Text ID " + sFluffAccess + ".";
		 return sReturnString;
	 }
	 
	 
	 public void performEvent(){
		 
	 }

	 public void readFromDB (ResultSet resultSet)throws SQLException{
		 iProbWeight = resultSet.getInt(RandomEvent.FIELD_OCCURANCE);
		 sGuid = resultSet.getString(RandomEvent.FIELD_ID);
		 sFluffAccess = resultSet.getString(RandomEvent.FIELD_TEXT_ID);
	 }
}