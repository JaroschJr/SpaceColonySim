/**
 * Test class for exercising the data module.
 */
public class SCSDataModuleTest implements ISCSError{

	//to compile : Call "C:\Program Files (x86)\Java\jdk1.8.0_91\bin\javac.exe" -cp "C:\Joey's coding stuf\SpaceColonySim" SCSDataModuleTest.java
	//to run w DB: Call "C:\Program Files (x86)\Java\jre1.8.0_91\bin\java.exe" -cp "C:\Joey's coding stuf\SpaceColonySim;C:\Joey's coding stuf\SpaceColonySim\sqlite-jdbc-3.18.0.jar" SCSDataModuleTest

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

		Thing.disConnect();
	}

	public void handleException(Exception ThisException){
		System.out.println(ThisException.getMessage());

		}
}