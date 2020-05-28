import java.sql.*;
import java.util.Formatter;
public class RandomEvent {
	int iProbWeight;//database: OCCURANCE
	double dMoraleEffect;
	int iLastEffect = 0;
	String sGuid;//database: ID
	String sFluffAccess;//database: TEXT_ID
	 
	public static final String FIELD_OCCURANCE = "OCCURANCE";
	public static final String FIELD_ID = "ID";
	public static final String FIELD_TEXT_ID = "TEXT_ID";
	public static final String FIELD_MORALE_FACTOR = "MORALE_FACTOR";
	 
	//constructor
	public RandomEvent(){	
	
	}
	 
	public String toString(){
		String sReturnString = "Probabillity " + iProbWeight + ", Guid " + sGuid + ", Fluff Text ID " + sFluffAccess;
		return sReturnString;
	}
	 
	 
	public boolean performEvent(SpaceColonyGame scg, ISCSIO ioman, SCSDataModule dbm){//dbm=DataBaseManager
		boolean bRet;
		if(checkIsValid(scg)){
			bRet = true;
			//System.out.println("Look over here in RandomEvent");
			//scg.subMorale+=dMoraleEffect;
		}//end if
		else{ 
			bRet = false;
		}//end else
		return bRet;
	}
	 
	public boolean checkIsValid(SpaceColonyGame scg){
		return true;
	}

	public void readFromDB (ResultSet resultSet)throws SQLException{
		iProbWeight = resultSet.getInt(RandomEvent.FIELD_OCCURANCE);
		dMoraleEffect = resultSet.getDouble(RandomEvent.FIELD_MORALE_FACTOR);
		sGuid = resultSet.getString(RandomEvent.FIELD_ID);
		sFluffAccess = resultSet.getString(RandomEvent.FIELD_TEXT_ID);
		
	}
}