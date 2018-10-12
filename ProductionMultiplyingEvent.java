import java.sql.*;
import java.util.Formatter;
public class ProductionMultiplyingEvent extends RandomEvent{
	double dProdMultFactor;//database: PROD_VALUE
	SCSEnum.eProductionModded eTargetProduction;//database: PROD_TYPE
	
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
			eTargetProduction = SCSEnum.eProductionModded.valueOf(sType);
		}//end if
	}
	
	@Override
	public void performEvent(SpaceColonyGame scg, ISCSIO ioman, SCSDataModule dbm){
		//it will multiply the production by a certain amount. currently this is not posible. come back to this once the rest of it is finished.
		String sToPrint = dbm.getDisplayText(sFluffAccess); 
		sToPrint = String.format(sToPrint,eTargetProduction.name(), dProdMultFactor);
		ioman.lineOut(sToPrint);
	
	}
	
	@Override
	public String toString(){
		String rReturnString = super.toString() + ", Production Multiplying Event. Multiplies" + eTargetProduction.name() + " By " + dProdMultFactor;
		return rReturnString;
	}

}