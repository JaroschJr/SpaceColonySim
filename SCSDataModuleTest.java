/**
 * Test class for exercising the data module.
 */
public class SCSDataModuleTest implements ISCSError{

	//to compile : Call "C:\Program Files (x86)\Java\jdk1.8.0_91\bin\javac.exe" -cp "C:\Joey's coding stuf\SpaceColonySim" SCSDataModuleTest.java
	//to run w DB: Call "C:\Program Files (x86)\Java\jre1.8.0_91\bin\java.exe" -cp "C:\Joey's coding stuf\SpaceColonySim;C:\Joey's coding stuf\SpaceColonySim\sqlite-jdbc-3.18.0.jar" SCSDataModuleTest

	private static final String CONSTR = "jdbc:sqlite:C:/Joey's coding stuf/SpaceColonySim/SCSDataBase.db";//For Sean D:\Dev\JavaJoe\SCS, for joey C:\Joey's coding stuf\SpaceColonySim

	public static void main (String[] args){
		SCSDataModuleTest whoCares = new SCSDataModuleTest();
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

		Thing.disConnect();
	}

	public void HandleException(Exception ThisException){
		System.out.println(ThisException.getMessage());

		}
}