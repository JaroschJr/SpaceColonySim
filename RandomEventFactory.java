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
        //1. create the array list that will be returned
        //2. get the data from the database
        //   look at SCSDataModule.getLanguages for help
        //   use the SCSDataModule.getResultSet method
        //3. loop through the result set and process each row
        //   call readEvent pass in the result set
        //4. put the event in the array list
        //5. return the array list
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
        //1. find the class of the event - read from
        //   column CLASS_NAME
        //2. convert the class name to its enumeration value
        //3. create the event based on the enumeration
        //   call createEvent method
        //4. set the properties - call readFromDB on the event
        //5. return the event
    }

    private RandomEvent createEvent(SCSEnum.eRandomEventClasses classType){
        //1. create the event - use a switch
        //2. return the event
    }
}