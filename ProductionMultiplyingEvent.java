import java.sql.*;
public class ProductionMultiplyingEvent extends RandomEvent{
	double dProdMultFactor;//database: PROD_VALUE
	SCSEnum.eProductionModded eTargetProduction;//database: PROD_TYPE
	
	public static final String FIELD_PROD_VALUE = "PROD_VALUE";
	public static final String FIELD_PROD_TYPE = "PROD_TYPE";
	
	//Constuctor
	public ProductionMultiplyingEvent(){
		
	}
	
	public void readFromDB(ResultSet resultSet){
		//super.readFromDB(resultSet);
		
		//dProdMultFactor = resultSet.getDouble(FIELD_PROD_VALUE);
		/*
		String sType = resultSet.getString(FIELD_PROD_TYPE);
		if(sType != null){
			eTargetProduction = SCSEnum.eProductionModded.valueOf(sType);
		}//end if */
	}
	public String toString(){
		String rReturnString = "Production Multiplying Event. Multiplies" + eTargetProduction.name() + " By " + dProdMultFactor + super.toString();
		return rReturnString;
	}

}