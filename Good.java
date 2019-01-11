import java.sql.*;
public class Good{
	public static final String FIELD_ID = "ID";
	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_TEXT_CODE = "TEXT_CODE";
	public static final String FIELD_PUBLISHED = "PUBLISHED";
	
	String sName;
	String sTextCode;
	String sGuid;
	int iQuant = 0;
	boolean bPublish;
	
	public void readFromDB(ResultSet resultSet)throws SQLException{
		sName = resultSet.getString(Good.FIELD_NAME);
		sTextCode = resultSet.getString(Good.FIELD_TEXT_CODE);
		sGuid = resultSet.getString(Good.FIELD_ID);
		bPublish = SCSDataModule.getBoolean(resultSet, Good.FIELD_PUBLISHED);
	 }
	 
	 @Override
	 public String toString(){
		 return sName +" "+ sTextCode +" "+ sGuid +" "+ iQuant;
	 }
}