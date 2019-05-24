import java.sql.*;
public class Good{
	public static final String FIELD_ID = "ID";
	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_TEXT_CODE = "TEXT_CODE";
	public static final String FIELD_PUBLISHED = "PUBLISHED";
	public static final String FIELD_MERCHANT_MIN_CARRY = "MERCHANT_MIN_CARRY";
	public static final String FIELD_MERCHANT_MAX_CARRY = "MERCHANT_MAX_CARRY";
	public static final String FIELD_MERCHANT_FREQ = "MERCHANT_FREQ";
	public static final String FIELD_BASE_PRICE = "BASE_PRICE";
	
	String sName;
	String sTextCode;
	String sGuid;
	int iQuant = 0;
	boolean bPublish;
	int MERCHANT_MIN_CARRY;
	int MERCHANT_MAX_CARRY;
	int MERCHANT_FREQ;
	int BASE_PRICE;
	int iPrice = 0;
	/*
	public void readFromDB(ResultSet resultSet)throws SQLException{
		sName = resultSet.getString(Good.FIELD_NAME);
		sTextCode = resultSet.getString(Good.FIELD_TEXT_CODE);
		sGuid = resultSet.getString(Good.FIELD_ID);
		bPublish = SCSDataModule.getBoolean(resultSet, Good.FIELD_PUBLISHED);
	 }
	 */
	 
	 @Override
	 public String toString(){
		 return sName +" "+ sTextCode +" "+ sGuid +" "+ iQuant+ " MERCHANT_MIN_CARRY " + MERCHANT_MAX_CARRY + " MERCHANT_MAX_CARRY " + MERCHANT_MIN_CARRY + " MERCH_FREQ " + MERCHANT_FREQ + " BASE_PRICE " + BASE_PRICE + " iPrice " + iPrice;
	 }
	 
	 public Good clone(){
		 Good gOut = new Good();
		 gOut.sName = sName;
		 gOut.sTextCode = sTextCode;
		 gOut.sGuid = sGuid;
		 gOut.iQuant = iQuant;
		 gOut.bPublish = bPublish;
		 gOut.MERCHANT_MIN_CARRY = MERCHANT_MIN_CARRY;
		 gOut.MERCHANT_MAX_CARRY = MERCHANT_MAX_CARRY;
		 gOut.MERCHANT_FREQ = MERCHANT_FREQ;
		 gOut.BASE_PRICE = BASE_PRICE;
		 gOut.iPrice = iPrice;
		 return gOut;
	 }

}