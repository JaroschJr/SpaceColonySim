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
	boolean bReport;
	
	public void readFromDB(ResultSet resultSet)throws SQLException{
		int i;
		sName = resultSet.getString(Good.FIELD_NAME);
		sTextCode = resultSet.getString(Good.FIELD_TEXT_CODE);
		sGuid = resultSet.getString(Good.FIELD_ID);
		Report = SCSDataModule.getBoolean(resultSet, Good.PUBLISHED)
	 }
}