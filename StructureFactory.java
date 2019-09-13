import java.sql.*;
import java.util.ArrayList;
public class StructureFactory{
	 private SCSDataModule _data;
	 
	 public StructureFactory(SCSDataModule database){
		 _data = database;
	 }
	 
	 public StructureList getStructureList(){
		ResultSet rResultSet;
		StructureList listOut = new StructureList();
		try{
		rResultSet = _data.getResultSet("SELECT * FROM SCS_STRUCTURES");
		 
			boolean bMoreLines = true;
			bMoreLines = rResultSet.next();
			while(bMoreLines == true){
				listOut.add(readStructFromDB(rResultSet));		
				bMoreLines = rResultSet.next();
			}
		}catch(SQLException sqle){
			System.out.println(sqle.getMessage());
		}
		
		return listOut;
	 }
	 
	 private Structure readStructFromDB(ResultSet resultSet)throws SQLException{
		 Structure outStructure = new Structure();
		 if(resultSet.getString(Structure.FIELD_TYPE).equals("PRODUCTION")){
			 outStructure = readProductionBuildingFromDB(resultSet);
		 }else{
			outStructure = readPassiveBuildingFromDB(resultSet);
		 }
		 return outStructure;
		 
	 }
	 
	 private PassiveBuilding readPassiveBuildingFromDB(ResultSet resultSet)throws SQLException{
		PassiveBuilding outPB = new PassiveBuilding();
		outPB.ID = resultSet.getString(Structure.FIELD_ID);
		outPB.NAME = resultSet.getString(Structure.FIELD_NAME);
		outPB.TEXT_CODE = resultSet.getString(Structure.FIELD_TEXT_CODE);
		outPB.BENEFIT = SCSEnum.eBenefit.valueOf(resultSet.getString(Structure.FIELD_BENEFIT));
		outPB.NEED_WORKERS = SCSDataModule.getBoolean(resultSet, Structure.FIELD_NEED_WORKERS);
		outPB.MAX_WORKERS= resultSet.getInt(Structure.FIELD_MAX_WORKERS);
		outPB.HOURS_TO_BUILD = resultSet.getInt(Structure.FIELD_HOURS_TO_BUILD);
		outPB.iCompleteness = outPB.HOURS_TO_BUILD;
		outPB.bComplete = true;
		return outPB;
	 }
	 
	 private ProductionBuilding readProductionBuildingFromDB(ResultSet resultSet)throws SQLException{
		ProductionBuilding outPB = new ProductionBuilding();
		 
		outPB.ID = resultSet.getString(Structure.FIELD_ID);
		outPB.NAME = resultSet.getString(Structure.FIELD_NAME);
		outPB.TEXT_CODE = resultSet.getString(Structure.FIELD_TEXT_CODE);
		outPB.MAX_WORKERS= resultSet.getInt(Structure.FIELD_MAX_WORKERS);
		outPB.HOURS_TO_BUILD = resultSet.getInt(Structure.FIELD_HOURS_TO_BUILD);
		outPB.iCompleteness = outPB.HOURS_TO_BUILD;
		outPB.bComplete = true;
		
		ResultSet rResultSet = _data.getResultSet("select a.NAME as RECIPE from SCS_RECIPES a, SCS_STRUCTURES b, SCS_STRUCTURE_RECIPIES c where c.RECIPE = a.ID and c.STRUCTURE_USING = b.ID and b.NAME = '" + outPB.NAME +"'");
		
		boolean bMoreLines = true;
		bMoreLines = rResultSet.next();
		outPB.sPosibleRecipes = new ArrayList<String>();
		
		while(bMoreLines){
			outPB.sPosibleRecipes.add(rResultSet.getString("RECIPE"));
			bMoreLines = rResultSet.next();
		}
		
		 /*to get names of recipes it uses, check this:
			select a.NAME as RECIPE
			from SCS_RECIPES a, SCS_STRUCTURES b, SCS_STRUCTURE_RECIPIES c
			where c.RECIPE = a.ID
			and c.STRUCTURE_USING = b.ID
			and b.NAME = "farm"
		 */
		 return outPB;
		 
	 } 
	
}