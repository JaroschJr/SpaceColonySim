import java.sql.*;
import java.util.Random;
import java.util.Formatter;

public class DestroyStructureEvent extends RandomEvent{
	Random rng = new Random();
	Structure structDestroyed;

	public DestroyStructureEvent(){
		
	}
	
	@Override
	public void readFromDB(ResultSet resultSet)throws SQLException{
		super.readFromDB(resultSet);
	}
	
	@Override
	public boolean performEvent(SpaceColonyGame scg, ISCSIO ioman, SCSDataModule dbm){
		int targetSlot = rng.nextInt(scg.structures.size());
		
		String sToPrint = sToPrint = dbm.getDisplayText(sFluffAccess);
		sToPrint = String.format(sToPrint, dbm.getDisplayText(scg.structures.get(targetSlot).TEXT_CODE));
		
		scg.structures.get(targetSlot).setWork(scg.pop,0);
		scg.structures.remove(targetSlot);
		
				iLastEffect = (int) Math.round(dMoraleEffect);
		
		ioman.lineOut("________________________________________");
		ioman.lineOut(sToPrint);
		ioman.lineOut("________________________________________");
		
		return true;
	}
}