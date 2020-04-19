package Common;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

	public class TestUtility extends TestBase {
		private static final String Outputtype = null;
		public static WebDriver driver;
		public static int ExpWait;
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: explicitWait
	*  Function Arg: WebElement ExpwebElement,String ExPText,int timeOut
	*  FunctionOutPut: It will wait until element visible 
	* 
	**************************************************************************************************************/
	
	public static Boolean explicitWait(WebElement ExpwebElement,String ExPText,int timeOut ){
		System.out.println("Befor Wait");
		WebDriverWait ExWait=new WebDriverWait(driver,timeOut);
		
		Boolean waitStatus=ExWait.ignoring(StaleElementReferenceException.class).until( ExpectedConditions.textToBePresentInElementValue(ExpwebElement,ExPText));
		
		if(waitStatus.TRUE)
			{
				System.out.println("Three Is no Element found ");
			}
		else
			{
				System.out.println(waitStatus);
			}
		return waitStatus;
	}

	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: setAlert
	*  Function Arg:  
	*  FunctionOutPut: It will handle Alert acuction
	* 
	* **************************************************************************************************************/
	
	public static  Alert setAlert(){
		Alert alert = driver.switchTo().alert();
		return alert;
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: frame
	*  Function Arg:  ExFrame it can be index or webelement or value 
	*  FunctionOutPut: It will handle frame auction
	* 
	* 
	**************************************************************************************************************/
	
	public static   WebDriver setFrame(WebElement ExFrame){
		 WebDriver frame = driver.switchTo().frame(ExFrame);
		return frame;
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: frame
	*  Function Arg:  ExFrame it can be index or webelement or value 
	*  FunctionOutPut: It will handle frame auction
	* 
	* 
	**************************************************************************************************************/
	
	public static    Actions setAction(){
		Actions  action = new Actions(driver);
		return action;
	}

	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: createFolder
	*  Function Arg: String expFolderName
	*  FunctionOutPut: It will take Create a folder with current date
	* 
	***************************************************************************************************************/
	
	public static String createFolder(String expFolderName){
	 	
		Reporter.log("******************************************Folder Create Strated******************************************");
		System.out.println("******************************************Folder Create Strated**********************************************");
	 	String folderNamePath;
		DateFormat formatter = new SimpleDateFormat("MM-dd-yy");
		String Cdate=formatter.format(new Date()); // Get Current Date 
		Cdate=Cdate.replace("-","_");    // Replace -
		if(expFolderName.isEmpty()||expFolderName.isBlank())   // Check if user provide folder path?
			{
				folderNamePath="c:\\ScreenShot\\ScreenShot_"+Cdate;
			}
		else
			{
				folderNamePath="c:\\ScreenShot\\"+expFolderName+"_"+Cdate;
			}
		File file = new File(folderNamePath );
		if (!file.exists())
	        {
	        	file.mkdirs();
	            System.out.println("******************************************Directory Created******************************************");
	        }
	    else
	        {
				System.out.println("******************************************Directory already exists***********************************");
	        }
			Reporter.log("******************************************Folder Create Ended******************************************");
			System.out.println("******************************************Folder Create Ended*************************************************");	
			return folderNamePath;
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: createFolder
	*  Function Arg: String expFolderName
	*  FunctionOutPut: It will take Create a folder with current date
	* 
	***************************************************************************************************************/
	
	public static String createFolderFile(String ExpFileName,String FileExtension) throws IOException{
	 	
		Reporter.log("******************************************Folder and File Create Strated******************************************");
		System.out.println("******************************************Folder and File Strated********************************************");
		
		//String FolderName;
		LocalTime time = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		String CTime=time.format(formatter);
		CTime=CTime.replace(":","_");
		String filePath=createFolder(ExpFileName);
		if(ExpFileName.isEmpty()||ExpFileName.isBlank())
		{
			ExpFileName="Test";
		}
		if(FileExtension.isEmpty()||FileExtension.isBlank())
			{
			
				filePath=filePath+"\\"+ExpFileName+"_"+CTime+".docx";
			}
		else
			{
				filePath=filePath+"\\"+ExpFileName+"_"+CTime+FileExtension;
			}
		File file = new File(filePath);
	    if (file.createNewFile())
	        {
				System.out.println("******************************************File Created******************************************");
	        }
	    else
	        {
				System.out.println("******************************************File already exists***********************************");
	        }
			Reporter.log("******************************************Folder and File Ended******************************************");
			System.out.println("******************************************Folder and File Ended******************************************");	
		return filePath;
	}
		
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: takeScreenShot
	*  Function Arg: methodName
	*  FunctionOutPut: It will take a screen shot from screen
	* 
	***************************************************************************************************************/
		
	public static void takeScreenShot(String methodName) throws IOException {
		
		String filePath=createFolderFile(methodName,".png");
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        //The below method will save the screen shot in d drive with test method name 
		FileUtils.copyFile(scrFile, new File(filePath));
		System.out.println("********      Placed where screen shot in              *******    => "+filePath);
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: setBorder
	*  Function Arg: expElement which element want make border 
	*  FunctionOutPut: It will make border which element you want 
	* 
	***************************************************************************************************************/
		
	public static void setBorder(WebElement expElement) throws IOException {
		JavascriptExecutor objjs=((JavascriptExecutor)driver);
		objjs.executeScript("arguments[0].style.border='3px solid red'", expElement);
	}
}
