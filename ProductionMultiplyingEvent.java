public class ProductionMultiplyingEvent extends RandomEvent{
	double dProdMultFactor;//database: PROD_VALUE
	SCSEnum.eProductionModded eTargetProduction;//database: PROD_TYPE
	
	public static final String FIELD_PROD_VALUE = "PROD_VALUE";
	public static final String FIELD_PROD_TYPE = "PROD_TYPE";
	
	//Constuctor
	public ProductionMultiplyingEvent(){
		
	}
	
	public void readFromDB(ResultSet resultSet){
		super(resultSet);
		
		dProdMultFactor = resultSet.getDouble(FIELD_PROD_VALUE);
		
		String sType = resultSet.getString(FIELD_PROD_TYPE);
		if(sType != null){
			eTargetProduction = SCSEnum.eProductionModded.valueOf(sType);
		}//end if
	}

}