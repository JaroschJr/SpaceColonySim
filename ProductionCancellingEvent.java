import java.sql.*;
public class ProductionCancellingEvent extends RandomInputEvent{
	
	
	//Constuctor
	public ProductionCancellingEvent(){
		
	}
	
	@Override
	public void readFromDB(ResultSet resultSet)throws SQLException{
		super.readFromDB(resultSet);
	}
	
	@Override
	public String toString(){
		String sReturnString = super.toString();//This will need to be modified once the rest of the class is finished.
		return sReturnString;
	}

}