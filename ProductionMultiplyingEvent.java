import java.sql.*;
import java.util.Formatter;
public class ProductionMultiplyingEvent extends RandomEvent{
	double dProdMultFactor;//database: PROD_VALUE
	String sTargetProduction;//database: PROD_TYPE //This is actually the text code, so prepair accordingly.
	public boolean bPrint = false;
	
	public static final String FIELD_PROD_VALUE = "PROD_VALUE";
	public static final String FIELD_PROD_TYPE = "PROD_TYPE";
	
	//Constuctor
	public ProductionMultiplyingEvent(){
		
	}
		@Override
	public void readFromDB(ResultSet resultSet)throws SQLException{
		super.readFromDB(resultSet);
		
		dProdMultFactor = resultSet.getDouble(FIELD_PROD_VALUE);
		
		String sType = resultSet.getString(FIELD_PROD_TYPE);
		if(sType != null){
			sTargetProduction = sType;
		}//end if
	}
	
	@Override
	public boolean performEvent(SpaceColonyGame scg, ISCSIO ioman, SCSDataModule dbm){
		//it will multiply the production by a certain amount. currently this is not posible. come back to this once the rest of it is finished.
		String sToPrint = dbm.getDisplayText(sFluffAccess); 
		sToPrint = String.format(sToPrint,dbm.getDisplayText(sTargetProduction), dProdMultFactor);
		if(bPrint){
			ioman.lineOut("________________________________________");
			ioman.lineOut(sToPrint);
			ioman.lineOut("________________________________________");
		}
		//iLastEffect = dprodMultFactor*dMoraleEffect*5;
		bPrint = false;
		return true;
	
	}
	
	@Override
	public String toString(){
		String rReturnString = super.toString() + ", Production Multiplying Event. Multiplies" + sTargetProduction + " By " + dProdMultFactor + ", bPrint " +bPrint;
		return rReturnString;
	}

}