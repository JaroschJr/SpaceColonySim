import java.sql.*;
import java.util.ArrayList;
public class InventoryFactory{
	
	private SCSDataModule _data;
	
    public InventoryFactory(SCSDataModule database){
        _data = database;
    }
	
	public Inventory getList(){
		ResultSet rResultSet;
		//1. create the array list that will be returned
		Inventory rResultData = new Inventory();
		try{
			//2. get the data from the database
			rResultSet = _data.getResultSet("SELECT * FROM SCS_INVENTORY_ITEMS");
			//3. loop through the result set and process each row
			//   call readEvent pass in the result set
			//4. put the event in the array list
			boolean bMoreLines = true;
			bMoreLines = rResultSet.next();
			while(bMoreLines == true){
				
				rResultData.add(getGood(rResultSet));		
				bMoreLines = rResultSet.next();
			}
		}catch(SQLException sqle){
			System.out.println(sqle.getMessage());
		}
        //5. return the array list
		//System.out.println(rResultData.toString());
		return rResultData;
    }
	
	private Good getGood(ResultSet resultSet)throws SQLException{
		Good gOut = new Good();
		gOut.readFromDB(resultSet);
		return gOut;
		
	}

}