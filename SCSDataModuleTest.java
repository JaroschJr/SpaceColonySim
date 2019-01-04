import java.util.*;

/**
 * Test class for exercising the data module.
 */
public class SCSDataModuleTest implements ISCSError{

	//to compile : Call "C:\Program Files\Java\jdk-10.0.1\bin\javac.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim" SCSDataModuleTest.java
	//to run w DB: Call "C:\Program Files\Java\jdk-10.0.1\bin\java.exe" -cp "C:\Joey's coding stuf\SpaceColonySim\SpaceColonySim\;C:\Joey's coding stuf\SpaceColonySim\sqlite-jdbc-3.18.0.jar" SCSDataModuleTest "jdbc:sqlite:C:\\Joey's coding stuf\\SpaceColonySim\\SpaceColonySim\\SCSDataBase.db"

	private static String CONSTR;

	public static void main (String[] args){
		SCSDataModuleTest whoCares = new SCSDataModuleTest();
		CONSTR = args[0];
		whoCares.WorkAround();
	}
	public void WorkAround(){
		SCSDataModule Thing = new SCSDataModule();
		Thing.errorHandler = this;
		Thing.setConnectionString(CONSTR);
		Thing.connect();
		//boolean BisConnected = Thing.isConnected();
		if (Thing.isConnected() == true){
			System.out.println("IT IS CONNECTED. YAY.");
		}else{
			System.out.println("IT IS NOT CONNECTED. DRAT.");
			return;
		}
		Thing.setLanguage("ENG");
		String DisplayText;
		//DisplayText ="RANDOM";
		DisplayText = Thing.getDisplayText("WELCOME");
		System.out.println("The Display Text Is " + DisplayText);

		String[][] sLangs;
		sLangs = Thing.getLanguages();
		for(int i = 0; i < sLangs.length; i++){
			System.out.println("Lang " + i + " " + sLangs[i][0] + " - " + sLangs[i][1]);
		}//end for i
		
		RandomEventFactory ref = new RandomEventFactory(Thing);
		ArrayList<RandomEvent> events = ref.getList();
		
		for(int i = 0; i < events.size(); i++){
			System.out.println(events.get(i).toString());
			System.out.println("   ");
		}//end for i
		
		InventoryFactory invFac = new InventoryFactory(Thing);
		Inventory inv = invFac.getList();
		
		for(int i = 0; i < inv.size(); i++){
			System.out.println(inv.get(i).toString());
			System.out.println("   ");
		}//end for i
		
		Good g = inv.getGoodByName("Ore");
		System.out.println("Ore " + g.toString());
		
		Thing.disConnect();
	}

	public void handleException(Exception ThisException){
		System.out.println(ThisException.getMessage());

		}
}