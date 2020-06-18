import java.util.ArrayList;
public class Recipe extends ArrayList<Good>{
	public String ID;
	public String NAME;
	public String TEXT_CODE;
	public int MAN_HOURS;
	public int YIELD;
	
	public static final String FIELD_ID = "ID";
	public static final String FIELD_NAME = "NAME";
	public static final String FIELD_TEXT_CODE = "TEXT_CODE";
	public static final String FIELD_MAN_HOURS = "MAN_HOURS";
	public static final String FIELD_YIELD = "YIELD";
	//Man hours is always the first ingredient in the lsit
	@Override
	public String toString(){
		String sOut = ID+" "+NAME+"  TEXT_CODE "+TEXT_CODE+" MAN_HOURS "+MAN_HOURS + " YIELD " + YIELD + " ";
		for(int i = 0; i<size(); i++){
			sOut += " - Good no." + i + " " + get(i).toString() ;
		}
		return sOut;
	}
}