import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
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
	
	public Inventory loadInventory(){
		ResultSet rResultSet;
		return new Inventory();
		//this part wont do anything untill we have a loadSave
	}
	
	public RecipeList getRecipeList(){
		ResultSet rResultSet;
		RecipeList rOutRecList = new RecipeList();
		
		try{
			rResultSet = _data.getResultSet("SELECT * FROM SCS_RECIPES");
			boolean bMoreLines = true;
			bMoreLines = rResultSet.next();
			while(bMoreLines){
				rOutRecList.add(getRecipe(rResultSet));
				bMoreLines = rResultSet.next();
			}
			
		}catch(SQLException sqle){
			System.out.println(sqle.getMessage());
		}
		return rOutRecList;
	}
	
	private Recipe getRecipe(ResultSet resultSet) throws SQLException{
		Recipe outRecipe = new Recipe();
		//System.out.println("Reading in recipe.");
		outRecipe.ID = resultSet.getString(Recipe.FIELD_ID);
		//System.out.println("ID is " + outRecipe.ID); 
		outRecipe.NAME = resultSet.getString(Recipe.FIELD_NAME);
		//System.out.println("ID is " + outRecipe.ID);
		outRecipe.TEXT_CODE = resultSet.getString(Recipe.FIELD_TEXT_CODE);
		//System.out.println("ID is " + outRecipe.ID);
		outRecipe.MAN_HOURS = resultSet.getInt(Recipe.FIELD_MAN_HOURS);
		//System.out.println("ID is " + outRecipe.ID);
		outRecipe.YIELD = resultSet.getInt(Recipe.FIELD_YIELD);
		
		//An exemplary SQL Query is 
		/*
		select a.NAME as RECIPE_NAME, a.MAN_HOURS, b.QUANTITY, c.NAME as INGREDIENT_NAME
		from SCS_RECIPES a, SCS_RECIPE_GOODS b, SCS_INVENTORY_ITEMS c
		where b.RECIPE = a.ID
		and b.GOOD = c.ID
		and a.NAME = 'farm'
		*/
		
		ResultSet rOtherResultSet;
		try{
			rOtherResultSet = _data.getResultSet("select a.NAME as RECIPE_NAME, a.MAN_HOURS, b.QUANTITY, c.NAME as INGREDIENT_NAME from SCS_RECIPES a, SCS_RECIPE_GOODS b, SCS_INVENTORY_ITEMS c where b.RECIPE = a.ID and b.GOOD = c.ID and a.NAME = '" + outRecipe.NAME + "'");
			boolean bMoreLines = true;
			bMoreLines = rOtherResultSet.next();
			
			while(bMoreLines){
				outRecipe.add(altGetGood(rOtherResultSet));
				bMoreLines = rOtherResultSet.next();
			}
			
		}catch(SQLException sqle){
			System.out.println(sqle.getMessage());
		}
		return outRecipe;
	}
	
	private Good getGood(ResultSet resultSet)throws SQLException{
		Good gOut = new Good();
		//gOut.readFromDB(resultSet); this method did what the thing below now does.
		gOut.sName = resultSet.getString(Good.FIELD_NAME);
		gOut.sTextCode = resultSet.getString(Good.FIELD_TEXT_CODE);
		gOut.sGuid = resultSet.getString(Good.FIELD_ID);
		gOut.bPublish = SCSDataModule.getBoolean(resultSet, Good.FIELD_PUBLISHED);
		gOut.MERCHANT_MIN_CARRY = resultSet.getInt(Good.FIELD_MERCHANT_MIN_CARRY);
		gOut.MERCHANT_MAX_CARRY = resultSet.getInt(Good.FIELD_MERCHANT_MAX_CARRY);
		gOut.MERCHANT_FREQ = resultSet.getInt(Good.FIELD_MERCHANT_FREQ);
		gOut.BASE_PRICE = resultSet.getInt(Good.FIELD_BASE_PRICE);
		return gOut;
		
	}
	
	/*
	public TraderList getTraderList(){
		TraderList tListOut = new TraderList();
		
		
	}
	*/
	
	private Good altGetGood(ResultSet resultSet)throws SQLException{
		Good gOut = new Good();
		gOut.sName = resultSet.getString("INGREDIENT_NAME");
		gOut.iQuant = resultSet.getInt("QUANTITY");
		return gOut;
	}
}