import java.SQL.ResultSet;

/**
 * Class for reading random events in from
 * the database to a list of random event
 * definitions.
 */
public class RandomEventFactory
{
    private SCSDataModule _data;
	
    /**
     * Constructor.
     * @param database The data module for
     *                 accessing the database.
     */
    public RandomEventFactory(SCSDataModule database){
        _data = database;
    }

    /**
     * Creates and reads in a list of random event
     * objects defined in the database.
     * @return Returns the list of random event
     *         objects.
     */
    public ArrayList<RandomEvent> getList(){
		try{
			//1. create the array list that will be returned
			ArrayList<RandomEvent> rResultData = new ArrayList<RandomEvent>(1);
			//2. get the data from the database
			//   look at SCSDataModule.getLanguages for help
			//   use the SCSDataModule.getResultSet method
			public rResultSet = _data.getResultSet("SELECT * FROM SCS_RANDOM_EVENTS");
			//3. loop through the result set and process each row
			//   call readEvent pass in the result set
			//4. put the event in the array list
			while(boolean bMoreLines = true){
				bMoreLines = rResultSet.next();// I call it first, because per documentation, the cursor is initialy ABOVE the first row.
				rResultData.add(readEvent(rResultSet));
				
			}
		}catch(SQLException sqle){
			System.out.println(sqle.getErrorCode);
		}
        //5. return the array list
		return rResultData;
    }

    /**
     * Creates a random event object from a
     * record in the database.
     * @param resultSet The database result set,
     *                  note that the cursor of
     *                  the result set must be
     *                  set to the correct record
     *                  before passing it into
     *                  this method.
     * @return Returns the random event object
     *         as defined in the database.
     */
    private RandomEvent readEvent(ResultSet resultSet){
		rRandomEvent rResultEvent = new RandomEvent();
        //1. find the class of the event - read from
        //   column CLASS_NAME
		String sClass_Name = resultSet.getString("CLASS_NAME");
        //2. convert the class name to its enumeration value
		SCSEnum.eRandomEventClasses classType = SCSEnum.eRandomEventClasses.valueOf(sClass_Name);// I leanred this trick here: https://stackoverflow.com/questions/7056959/convert-string-to-equivalent-enum-value
        //3. create the event based on the enumeration
        //   call createEvent method
		rResultEvent = CreateEvent(classType);
        //4. set the properties - call readFromDB on the event
		//rResultEvent = 
        //5. return the event
    }

    private RandomEvent createEvent(SCSEnum.eRandomEventClasses classType){
		RandomEvent rReturnEvent;
        //1. create the event - use a switch
		switch (classType){
			case SCSEnum.eRandomEventClasses.RandomEvent: rReturnEvent = new RandomEvent();
				break;
			case SCSEnum.eRandomEventClasses.ProductionMultiplyingEvent: rReturnEvent = new ProductionMultiplyingEvent();
				break;
			case SCSEnum.eRandomEventClasses.StatModifierEvent: rReturnEvent = new StatModifierEvent();
				break;
			
		}
        //2. return the event
		return rReturnEvent;
    }
}