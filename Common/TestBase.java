package Common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.Reporter;

//********************************************************************   Test Base Class    **************************************************************************  

public class TestBase {
	public static WebDriver driver;
	public static FileInputStream objFile;
	public static Properties objprop;
	public static int PAGE_LOAD_TIME;
	public static int IMPLICIT_WAIT;
	public static String DomainName;
	public static String browserName;
	public static String urlAddress;
	public static String UserId;
	public static String UserPassword;
	public static String Description;
	public static String Title;
	public static String DriverPath;
	public static int ExpWait;
	public static Logger Log=LogManager.getLogger(TestBase.class.getName());	
	public static String ConfigFile;
	
	/*********************************  Test Base Constractor **********************************************************************************/
	
	public TestBase()
	{
		try
		{
			objprop = new Properties();
			String DataFilePath=System.getProperty("user.dir");
			DataFilePath=DataFilePath.substring(0, DataFilePath.length());
			DataFilePath=DataFilePath+"\\src\\main\\java\\Data\\config.properties";
			objFile=new FileInputStream(DataFilePath);
			objprop.load(objFile);
		}catch (FileNotFoundException e){
				e.printStackTrace();
			}catch(IOException e) {
					e.printStackTrace();
				}
	}
	
	/****************************************************************************************************************
	 * Author: Md Rezaul Karim 
	 * Function Name: fileInStream
	 * Function Arg: String FileInStreamPath
	 * FunctionOutPut: It will create a Object For File Input Stream
	 * **************************************************************************************************************
	 */
	public static FileInputStream fileInStream(String FileInStreamPath ) throws FileNotFoundException
	{
		objFile=new FileInputStream(FileInStreamPath);
		return objFile;
	}
	/****************************************************************************************************************
	 * Author: Md Rezaul Karim 
	 * Function Name: getData()
	 * Function Arg: 
	 * FunctionOutPut: It will Return User Defined Data From Properties File
	 * **************************************************************************************************************
	 */
	public static void getData() throws IOException {
		Reporter.log("******************************************Get Data Imported Staretd******************************************");
		System.out.println("******************************************Get Data Imported Staretd******************************************");
		objprop = new Properties();
		//Get File Path generaci from project with Common Name GlobalData global Data properties
		String DataFilePath=System.getProperty("user.dir");
		DataFilePath=DataFilePath.substring(0, DataFilePath.length());
		DataFilePath=DataFilePath+"\\src\\main\\java\\Data\\config.properties";
		System.out.println(DataFilePath);
		//now to get data from were specify the path to do that crate a file input stream object 
		objFile=new FileInputStream(DataFilePath);
		objprop.load(objFile);
		DomainName = objprop.getProperty("UserDomainName");
		browserName = objprop.getProperty("BrowserName");
		DriverPath=objprop.getProperty("DriverPath");
		PAGE_LOAD_TIME=Integer.parseInt(objprop.getProperty("PageLoadTime"));
		IMPLICIT_WAIT=Integer.parseInt(objprop.getProperty("ImplicitWait"));
		ExpWait=Integer.parseInt(objprop.getProperty("ExpWait"));
		System.out.println(DriverPath);
		System.out.println(browserName);
		if (DomainName.contains("criglist")) 
			{
				UserId = objprop.getProperty("CrigListUserId");
				UserPassword = objprop.getProperty("CrigListUserPassword");
				urlAddress = objprop.getProperty("CrigListUrl");
			} 
		else if (DomainName.contains("mercary")) 
			{
				UserId = objprop.getProperty("MercaryUserId");
				UserPassword = objprop.getProperty("MercaryPassword");
				urlAddress = objprop.getProperty("MercaryUrl");
				Description=objprop.getProperty("MercaryDescription");
				Title=objprop.getProperty("MercaryTitle");
			}
		else
			{
				System.out.println("could not find any domain so defult id google");
			}
		Reporter.log("******************************************Get Data Imported Ended******************************************");
		System.out.println("******************************************Get Data Imported Ended********************************************");
	}
	
	/****************************************************************************************************************
	 * Author: Md Rezaul Karim 
	 * Function Name: getExcelData
	 * Function Arg: String expectedSheetName, String excellFilePath,String expectedTestCaseData(User Can Get Number of Data Or Using TCID Or TCFlag)
	 * FunctionOutPut: It will Return User Defined Data From Excel Or All Data From Excel
	 * ***************************************************************************************************************/

	public static Object[][] getExcelData(String expectedSheetName, String excellFilePath, String expectedTestCaseData)
		throws IOException {
		Reporter.log("******************************************Get Data From Excel Started******************************************");
		System.out.println("******************************************Get Data From Excel Started****************************************");
		int RowStart = 1,r = 0,ColStart =2,c = 0,TotalrowNum = 0, TotalcolNum = 0, FinalFlag = 0, AllTC = 0,AcTCID = 0; 
		int	ExTCFlagColStart = 0, RowArraySize = 0, ColArraySize = 0;
		double ExTCId = 0;
		String AcTCFlag = "", ExColData = "";
		Object[][] CellValue;								//Store Value to an two dimetion array
		String[] ExpTestCase = null; 						 // Declare Array Variable
		if (excellFilePath.isEmpty())						 // Check If File Path Is Empty
		{
			String CurrentPath = System.getProperty("user.dir");
			//	excellFilePath = "/" + CurrentPath + "/src/DataFile/controller.xlsx";						// Get System Dir
			excellFilePath = "/"+ CurrentPath +"/src/main/java/Data/controller.xlsx";
		}
		FileInputStream objFile = new FileInputStream(excellFilePath);
		XSSFWorkbook WorkBook = new XSSFWorkbook(objFile);
		int TotalSheet = WorkBook.getNumberOfSheets();				/// Total Sheet Number
		for (int i = 0; i <= TotalSheet; i++){
			String ActualSheetName = WorkBook.getSheetName(i);
			if (expectedSheetName.isEmpty())					///Check if user provide sheet name if not then default sheet will be first one
			{
				expectedSheetName = ActualSheetName;
				break;
			}
			else if (ActualSheetName.equalsIgnoreCase(expectedSheetName)){
				expectedSheetName = ActualSheetName;
				break;
			}
		}
		XSSFSheet objsheet = WorkBook.getSheet(expectedSheetName);
		XSSFRow row = objsheet.getRow(0);
		TotalrowNum = objsheet.getLastRowNum() + 1;				//Get total Row number
		TotalcolNum = row.getLastCellNum();						//Get total column number
		if (!(expectedTestCaseData.isEmpty()))					//Check user provide Expected Test Case Data
		{
			ExpTestCase = expectedTestCaseData.split(",");
			if (ExpTestCase.length < 2) // If User Provide Number Of Test Case from first that User Want Data
			{
				RowArraySize = Integer.parseInt(ExpTestCase[0]);/// Define The Array Size
				FinalFlag = 1;
				TotalrowNum=RowArraySize+RowStart;
			}
			else if (ExpTestCase.length == 2){
				for (int i = 0; i < TotalcolNum; i++){
					ExColData = objsheet.getRow(0).getCell(i).getStringCellValue();        // Find the Expected Column Test
					if (ExpTestCase[0].toLowerCase().contains(ExColData.toLowerCase())){
						if (ExColData.toLowerCase().contains(("tcid"))){
							ExTCId = Integer.parseInt(ExpTestCase[1]);
							AllTC = 1;
							RowStart = 1;
							ExTCFlagColStart = i;
							RowArraySize = 1; /// Define The Array Size
							break;
						}
						else{
							AllTC = 2;
							ExTCFlagColStart = i;
							for (int j = 0; j < TotalrowNum; j++){
								ExColData = objsheet.getRow(j).getCell(ExTCFlagColStart).getStringCellValue();
								if (ExpTestCase[1].toLowerCase().contains(ExColData.toLowerCase())){
									RowArraySize = RowArraySize + 1;
								}
							}
							break;
						}
					}
				}
			}
		} 
		else{
			FinalFlag = 1;
			RowArraySize = TotalrowNum - RowStart;
		}
		ColArraySize = TotalcolNum - ColStart;
		CellValue = new Object[RowArraySize][ColArraySize];
		for (int j = RowStart; j < TotalrowNum; j++){
			Cell CkCellEmpty = objsheet.getRow(j).getCell(ExTCFlagColStart);
			if (AllTC == 1 && CkCellEmpty != null) // check if user TCID provide then
			{
				AcTCID = (int) CkCellEmpty.getNumericCellValue();
				if (AcTCID == ExTCId){
					FinalFlag = 1;// If Flag value 1 then get data
				}
			}
			else if (AllTC == 2 && CkCellEmpty != null){
				AcTCFlag = CkCellEmpty.getStringCellValue();
				if (AcTCFlag.toLowerCase().contains(ExpTestCase[1].toLowerCase())){
					FinalFlag = 1;
				}
			}
			if (FinalFlag == 1){
				c = 0;
				for (int k = ColStart; k < TotalcolNum; k++){
					Cell CkCell = objsheet.getRow(j).getCell(k);
					if (CkCell != null) // Validate if cell value is empty
					{
						switch (CkCell.getCellType()){
						case BOOLEAN:
							Boolean BCell = CkCell.getBooleanCellValue();
							CellValue[r][c] = Boolean.toString(BCell);
							break;
						case STRING:
							CellValue[r][c] = CkCell.getRichStringCellValue().getString();
							break;
						case NUMERIC:
							if (DateUtil.isCellDateFormatted(CkCell)){
								Date DCell = CkCell.getDateCellValue();
								CellValue[r][c] = DCell.toString();
							}
							else{
								CellValue[r][c] = NumberToTextConverter.toText(CkCell.getNumericCellValue());
							}
							break;
						case FORMULA:
							CellValue[r][c] = CkCell.getCellFormula().toString();
							break;
						case BLANK:
							System.out.print("");
							break;
						default:
							System.out.print("There is no value");
							CellValue[r][c] = " ";
						}
					}
					else{
						CellValue[r][c] = " ";
					}
					if ((c + ColStart) != TotalcolNum) // if col does not start from begaining
					{
						c = c + 1;   		////Increase column
					}
				}
			}
			if (FinalFlag == 1 && (r + RowStart) != TotalrowNum) // if row does not strat from begaining
			{
				r = r + 1;			////Increase row
			}
			if (AllTC == 1 && FinalFlag == 1)		//if only one row data need then break loop
			{
				break;
			}
			if (AllTC == 2)
			{
				FinalFlag = 0;
			}
		}
		Reporter.log("******************************************Get Data From Excel Ended******************************************");
		System.out.println("******************************************Get Data From Excel Ended****************************************");
		WorkBook.close();
		return CellValue;
	}	
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: getExcelDataForTestNG
	*  Function Arg: String expectedSheetName, String excellFilePath,String expectedTestCaseData(User Can Get Number of Data Or Using TCID Or TCFlag)
	*  FunctionOutPut: It will Return User Defind Data From Excell Or All Data From Excell
	* 
	* ***************************************************************************************************************/
	
	public static Object[] getExcelDataForTestNG(String expectedSheetName, String excellFilePath,String expectedTestCaseData) throws IOException {
		Reporter.log("******************************************Get Data From Excel Started******************************************");
		System.out.println("******************************************Get Data From Excel Started****************************************");
		int TotalrowNum=0,TotalcolNum=0,FinalFlag=0,AllTC=0,ColStrat=2,ExRowStart=0,ExTCFlagColStart=0,AcTCID=0,AllCellDataLenth=0,AllCellDataArrayLenth=0 ;
		double ExTCId=0;
		String AcTCFlag="",AllRowValue="",ExColData="";
		Object CellValue="";
		String [] ExpTestCase = null;	//Declar Array Variable
		Object[]AllCellData;
		if (excellFilePath.isEmpty())																		//Check If File Path Is Empty 
		{
			String CurrentPath=System.getProperty("user.dir");
			//excellFilePath = "/"+ CurrentPath +"/src/DataFile/controller.xlsx";//Get System Dir
			excellFilePath = "/"+ CurrentPath +"/src/main/java/Data/controller.xlsx";
		}
		FileInputStream objFile = new FileInputStream(excellFilePath);
		XSSFWorkbook WorkBook = new XSSFWorkbook(objFile);
		/// Find Sheet Name
		int TotalSheet = WorkBook.getNumberOfSheets();
		for (int i = 0; i <= TotalSheet; i++)
		{
			String ActualSheetName =WorkBook.getSheetName(i); 
			if (expectedSheetName.isEmpty())
			{
				expectedSheetName=ActualSheetName;
				break;
			}
			else if (ActualSheetName.equalsIgnoreCase(expectedSheetName))
			{
				expectedSheetName=ActualSheetName;
				break;
			}
		}
		XSSFSheet objsheet = WorkBook.getSheet(expectedSheetName);
		XSSFRow row=objsheet.getRow(0);
		TotalrowNum = objsheet.getLastRowNum();
		TotalcolNum = row.getLastCellNum();
		if(!(expectedTestCaseData.isEmpty()))
		{
			ExpTestCase=expectedTestCaseData.split(",");
			
			if (ExpTestCase.length<2)																		//If User Provide Number Of Test Case from first  that User  Want Data
			{
				TotalrowNum=Integer.parseInt(ExpTestCase[0]);
				AllCellDataLenth=TotalrowNum;///Define The Array Size
				FinalFlag=1;
			}
			else if (ExpTestCase.length==2)
			{
				for(int i=0;i<TotalcolNum;i++)
				{
					ExColData=objsheet.getRow(0).getCell(i).getStringCellValue();
					if(ExpTestCase[0].toLowerCase().contains(ExColData.toLowerCase()))
					{
						if(ExColData.toLowerCase().contains(("tcid")))
						{
							ExTCId=Integer.parseInt(ExpTestCase[1]);
							AllTC=1;
							ExTCFlagColStart=i;
							ExRowStart=1;
							AllCellDataLenth=1; ///Define The Array Size
							break;
						}
						else
						{
							AllTC=2;
							ExTCFlagColStart=i;
							for(int j=0;j<TotalrowNum;j++)
							{	
								ExColData=objsheet.getRow(j).getCell(ExTCFlagColStart).getStringCellValue();
								if(ExpTestCase[1].toLowerCase().contains(ExColData.toLowerCase()))
								{
									AllCellDataLenth=AllCellDataLenth+1;
								}
							}
							break;
						}	
					}
				}
			}
		}
		else
		{
			FinalFlag=1;
			AllCellDataLenth=TotalrowNum+1;
		}
		AllCellData = new Object[AllCellDataLenth];
		for (int j =ExRowStart; j <=TotalrowNum-1; j++)
		{
			CellValue="";
			AllRowValue="";
			Cell CkCellEmpty=objsheet.getRow(j).getCell(ExTCFlagColStart);
			if(AllTC==1 && CkCellEmpty!=null)
			{
				AcTCID=(int) CkCellEmpty.getNumericCellValue();
				if(AcTCID==ExTCId)
				{
					FinalFlag=1;//If Flag value 1 then get data
				}
			}
			else if(AllTC==2 && CkCellEmpty!=null)
			{
				AcTCFlag=CkCellEmpty.getStringCellValue();
				
				if(AcTCFlag.toLowerCase().contains(ExpTestCase[1].toLowerCase()))
				{
					FinalFlag=1;
				}
			}	
			if(FinalFlag==1)
			{	
				for (int k =ColStrat; k<=TotalcolNum-1; k++)
				{
					Cell CkCell=objsheet.getRow(j).getCell(k);
					if(CkCell!=null)														//Validate if cell value is empty
					{	
						switch (CkCell.getCellType())
						{
							case BOOLEAN:
								CellValue=CkCell.getBooleanCellValue();
								break;
							case STRING:
								CellValue=CkCell.getRichStringCellValue();
								break;
							case NUMERIC:
								if (DateUtil.isCellDateFormatted(CkCell))
								{
									CellValue=CkCell.getDateCellValue();
								} 
								else 
								{
									CellValue=CkCell.getNumericCellValue();
								}
								break;
							case FORMULA:
								CellValue=CkCell.getCellFormula();
								break;
							case BLANK:
								System.out.print("");
								break;
							default:
								System.out.print("There is no value");
								CellValue=" ";
						}
					}
					else
					{
						CellValue=" ,";
					}
					AllRowValue=AllRowValue+CellValue+",";
				}
			}
			if(FinalFlag==1)											///Check if only one data retrieve or multiple 
			{
				AllRowValue=AllRowValue.substring(0,AllRowValue.length()-1);   //Remove last comma
				AllCellData[AllCellDataArrayLenth]=AllRowValue;
				System.out.println(AllCellData[AllCellDataArrayLenth]);
				AllCellDataArrayLenth=AllCellDataArrayLenth+1;
			}
			if(AllTC==1 && FinalFlag==1 )
			{
				break;
			}
			if(AllTC==2)
			{
				FinalFlag=0;
			}
		}
		Reporter.log("******************************************Get Data From Excel Ended******************************************");
		System.out.println("******************************************Get Data From Excel Ended****************************************");
		return AllCellData;
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: initilizeDriver
	*  Function Arg: Return WebDriver
	*  FunctionOutPut: It will initilize Driver
	* 
	* ***************************************************************************************************************/
	
	public static WebDriver initilizeDriver() throws IOException {
		String AcDriverPath;
		Reporter.log("**************************************** initilize Driver MEthod Started ******************************************");
		System.out.println("**************************************** initilize Driver MEthod Started ******************************************");
		String CurrentPath=System.getProperty("user.dir");
		String MavenbrowserName=System.getProperty("Browser");// check if maven send any browser
		if(MavenbrowserName!= null)
		{
			browserName=MavenbrowserName;
		}
		else
		{
			browserName=browserName;	
		}
		if(DriverPath.isEmpty())//if user does not provided driver path default will look from current directory 
		{
			AcDriverPath=CurrentPath.substring(0, CurrentPath.length());
		}
		else
		{//if user Provided Path then	 
			AcDriverPath=DriverPath.substring(0,DriverPath.indexOf("Driver",0)-1);
		}
		if(browserName.toLowerCase().contains("ie") || browserName.contains("internet"))
		{
			System.setProperty("webdriver.chrome.driver","/"+AcDriverPath+"/Driver/msedgedriver.exe");
			driver= new ChromeDriver();
		}
		else if(browserName.contains("firefox") || browserName.contains("ff"))
		{
			System.setProperty("webdriver.gecko.driver","/"+AcDriverPath+"/Driver/geckodriver.exe");
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"null");
			driver=new FirefoxDriver();
		}
		//if user want headless browser then you can use it 
		else if(browserName.contains("chromeheadless") || browserName.contains("headless"))
		{
			System.setProperty("webdriver.chrome.driver","/"+AcDriverPath+"/Driver/chromedriver.exe");
			System.setProperty("webdriver.chrome.silentOutput","true");//it will remove unnessary log
			ChromeOptions objoption=new ChromeOptions();
			objoption.addArguments("headless");
			driver=new ChromeDriver(objoption);
			browserName="chrome";
		}
		else if(browserName.contains("sslcertificatebrowser") || browserName.contains("securityalartbrowser"))
		{
			DesiredCapabilities objCap = DesiredCapabilities.chrome();
			objCap.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
			objCap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			ChromeOptions objOption = new ChromeOptions();
			objCap.merge(objOption);
			System.setProperty("webdriver.chrome.driver","/"+AcDriverPath+"/Driver/chromedriver.exe");
			driver = new ChromeDriver(objOption);
			browserName="chrome";
		}
		else
		{
			System.setProperty("webdriver.chrome.driver","/"+AcDriverPath+"/Driver/chromedriver.exe");
			System.setProperty("webdriver.chrome.silentOutput","true");//it will remove unnessary log.
			HashMap<String,Object> ohp=new HashMap<String,Object>();
			ohp.put("profile.defult_content_settings.popups",0);
			ohp.put("download.defult_directory",CurrentPath);  //if download any file it will save to current user dir 
			ChromeOptions oco=new ChromeOptions();
			oco.setExperimentalOption("prefs",ohp);
			driver = new ChromeDriver();
			driver=new ChromeDriver();
			browserName="chrome";
		}
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIME,TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT,TimeUnit.SECONDS);
		Reporter.log("**************************************** initilize Driver MEthod Ended ******************************************");
		System.out.println("**************************************** initilize Driver MEthod Ended ******************************************");
		return driver;
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: RemoteGrid
	*  Function Arg: BrowserName ==>Which Browser You want work for
	*  FunctionOutPut: 
	* 
	* ***************************************************************************************************************/
	
	/*public static void RemoteGrid(String BrowserName) throws MalformedURLException{
		
		DesiredCapabilities objRc=new DesiredCapabilities();
		if(BrowserName.isEmpty())
		{
			objRc.setBrowserName(BrowserName);
		}
		else
		{
			objRc.setBrowserName("chrome");
		}
		objRc.setPlatform(Platform.WINDOWS);
		driver=new RemoteWebDriver(new URL("http://localhost:4546/wd/hub"),objRc);
	}
	*/
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: OpenUrl
	*  Function Arg: expectedUrl ==>Which Url Or Domain You want work for
	*  FunctionOutPut: It will open Url That you want Automated
	* 
	* ***************************************************************************************************************/
		
	public static void openUrl(String expectedUrl){
		
		Reporter.log("******************************************Url Open Strated******************************************");
		System.out.println("******************************************Url Open Strated***************************************************");
		String urlAdd;
		if(expectedUrl.isEmpty())
			{
			urlAdd=urlAddress;
			}
		else
			{
			urlAdd=expectedUrl;
			}
		driver.get(urlAdd);
		Reporter.log("******************************************Url Open Ended******************************************");
		System.out.println("******************************************Url Open Ended*****************************************************");
	}
		
	////******************************   Validation Part   ******************************************************88
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: ValidateUrl
	*  Function Arg: ExpectedUrl(Which Url You want work for)
	*  FunctionOutPut: It will open Url That you want Automated
	* 
	* ***************************************************************************************************************/
		
	public static void validateUrl(){
	
		Reporter.log("******************************************Validate Url Strated******************************************");
		System.out.println("******************************************Validate Url Strated***********************************************");
		String actualUrl = driver.getCurrentUrl();
		int uIndex=urlAddress.indexOf("www");
		int aIndex=actualUrl.indexOf("www");
		if(uIndex>0 && aIndex<0 )
			{
				urlAddress=urlAddress.replace("www.","");	
			}
		else if(uIndex<0 && aIndex>0)
			{
				actualUrl=actualUrl.replace("www.","");
			} 
		urlAddress=urlAddress.trim();
		actualUrl=actualUrl.trim();
		int eplength = urlAddress.length();
		int aclength = actualUrl.length();
		int lengthDiffrent =eplength-aclength;
		if (actualUrl.equals(urlAddress))
			{
				System.out.println("Expected Url ****** " + urlAddress + " ******* Found And Validation of Url Successfully Passed");
				Assert.assertTrue(true,"Expected Url ****** " + urlAddress + " ******* Found And Validation of Url Successfully Passed");
				Log.info("Expected Url ****** " + urlAddress + " ******* Found And Validation of Url Successfully Passed");
			}
		else if (actualUrl.equalsIgnoreCase(urlAddress))
			{
				System.out.println("Expected Url ****** " + urlAddress+ " ******* Found And Validation of Url Successfully Passed but there is lower and upper case character does not match actual Url was****"+ actualUrl+" ****");
				Assert.assertTrue(true, "Expected Url ****** " + urlAddress+ " ******* Found And Validation of Url Successfully Passed but there is lower and upper case character does not match actual Url was****"+ actualUrl+" ****");
				Log.warn("Expected Url ****** " + urlAddress+ " ******* Found And Validation of Url Successfully Passed but there is lower and upper case character does not match actual Url was****"+ actualUrl+" ****");
			}
		else if (actualUrl.contains(urlAddress))
			{	
				System.out.println("Expected Url ****** " +urlAddress+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url current url is **** "+actualUrl+"****");
				Assert.assertTrue(true,"Expected Url ****** " +urlAddress+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url current url is **** "+actualUrl+"****");
				Log.warn("Expected Url ****** " +urlAddress+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url current url is **** "+actualUrl+"****");
			} 
		else if (actualUrl.contains(urlAddress.toLowerCase()))
			{
				System.out.println("Expected Url ****** " +urlAddress+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
				Assert.assertTrue(true, "Expected Url ****** " +urlAddress+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
				Log.warn("Expected Url ****** " +urlAddress+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
			}
		else if(lengthDiffrent>0 && lengthDiffrent<5)
			{
				if(urlAddress.toLowerCase().contains(actualUrl.toLowerCase()))
				{
					System.out.println("Expected Url ****** " +urlAddress+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and might be does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
					Assert.assertTrue(true, "Expected Url ****** " +urlAddress+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and might be does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
					Log.warn("Expected Url ****** " +urlAddress+ " ******* Found And Validation of Url Successfully Passed but Actual Url Contains expected Url and might be does not match upper and lower case letter acutal url was **** "+actualUrl+" ****");
				}
			}
		else 
			{
				Assert.assertFalse(false, "Expected Url ***** " + urlAddress+ " ***** Not Found And Validation of Url Are Failed " + "Actual Url Was **** " + actualUrl+" ****");
				System.out.println("Expected Url ***** " + urlAddress+ " ***** Not Found And Validation of Url Are Failed " + "Actual Url Was **** " + actualUrl+" ****");
				Log.error("Expected Url ***** " + urlAddress+ " ***** Not Found And Validation of Url Are Failed " + "Actual Url Was **** " + actualUrl+" ****");
			}
		Reporter.log("******************************************Validate Url Ended******************************************");
		System.out.println("******************************************Validate Url Ended*************************************************");
	}
	
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
    *  Function Name: validateText
	*  Function Arg: ExpectedText And Actual Text
	*  FunctionOutPut: It will Validate Expected Text And Actual Text
	* 
	* ***************************************************************************************************************/
		
	public static void validateText(String expectedText,String actualText) {
		
		Reporter.log("******************************************Validate Text Started******************************************");
		System.out.println("******************************************Validate Text Started**********************************************");
		String exText;	
		String  acText;
		int expLength,actLength;
		String[]expText=expectedText.split(",");
		String[]actText=actualText.split(",");	
		expLength=expText.length;
		actLength=actText.length;	
		if(expLength>actLength)
			{
				expText=Arrays.copyOf(expText,(actLength));	
			}
		if(expLength<actLength)
			{
				actText=Arrays.copyOf(actText,(expLength));	
			}
		expLength=expText.length;
		actLength=actText.length;	
		int j=0;
		for(int i=0;i<expLength;i++)
			{
				while(j<actLength)
				{
					exText=expText[i].trim();
					acText=actText[j].trim();
					if (exText.equals(acText))
						{
							System.out.println("Expected Text Element  ****** " + exText + " ******* Found And Validation of Text Successfully Passed");
							Assert.assertTrue(true,"Expected Text Element  ****** " + exText + " ******* Found And Validation of Text Successfully Passed");
							Log.info("Expected Text Element  ****** " + exText + " ******* Found And Validation of Text Successfully Passed");
						}
					else if (exText.equalsIgnoreCase(acText))
						{
							System.out.println("Expected Text Element ****** " +exText+ " ******* Found And Validation of Text Successfully Passed but there is lower and upper case character Does not match The Actual Text Was *** "+acText+" ***");
							Assert.assertTrue(true,"Expected Text Element ****** " +exText+ " ******* Found And Validation of Text Successfully Passed but there is lower and upper case character Does not match The Actual Text Was *** "+acText+" ***");
							Log.warn("Expected Text Element ****** " +exText+ " ******* Found And Validation of Text Successfully Passed but there is lower and upper case character Does not match The Actual Text Was *** "+acText+" ***");
						}
					else if (exText.contains(acText))
						{
							System.out.println("Expected Text Element  ****** " +exText+ " ******* Found From Actual Text but Actual Text Contains expected Text And Validation of Text Successfully Passed The Actual Text Was *** "+acText+" ***");
							Assert.assertTrue(true,"Expected Text Element  ****** " +exText+ " ******* Found From Actual Text but Actual Text Contains expected Text And Validation of Text Successfully Passed The Actual Text Was *** "+acText+" ***");
							Log.warn("Expected Text Element  ****** " +exText+ " ******* Found From Actual Text but Actual Text Contains expected Text And Validation of Text Successfully Passed The Actual Text Was *** "+acText+" ***");
						} 
					else if (exText.contains(acText.toLowerCase()))
						{
							System.out.println("Expected Text Element ****** " + exText+ " ******* Found From Actual Text but Actual Text Contains expected Text but there is lower and upper case character Does not match And Validation of Text SuccessfullyThe Actual Text Was *** "+acText+" ***");
							Assert.assertTrue(true,"Expected Text Element ****** " + exText+ " ******* Found From Actual Text but Actual Text Contains expected Text but there is lower and upper case character Does not match And Validation of Text Successfully The Actual Text Was *** "+acText+" ***");
							Log.warn("Expected Text Element ****** " + exText+ " ******* Found From Actual Text but Actual Text Contains expected Text but there is lower and upper case character Does not match And Validation of Text SuccessfullyThe Actual Text Was *** "+acText+" ***");
						}
						else
						{
							Assert.assertFalse(false, "Expected Text Element  ***** " + exText+ " *****  Not Found And Validation of Text element  Are Failed " + "The Actual Text Was *** " + acText+" ***");
							System.out.println("Expected Text Element  ***** " + exText+ " *****  Not Found And Validation of Text element  Are Failed " + "The Actual Text Was *** " + acText+" ***");
							Log.error("Expected Text Element  ***** " + exText+ " *****  Not Found And Validation of Text element  Are Failed " + "The Actual Text Was *** " + acText+" ***");
						}
					j++;
					break;
				}
			}
		Reporter.log("******************************************Validate Text Ended******************************************");
		System.out.println("******************************************Validate Text Ended************************************************");
	}
		
	/***************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: ValidateHeader
	*  Function Arg: actualHeader(Get From Application header),expectedHeader
	*  FunctionOutPut: It will Validate Expected Header and Actual Header
	* 
	* ***************************************************************************************************************/
		
	public static void validateHeader(String expectedHeader, String actualHeader) {
		
		Reporter.log("******************************************Validate Header Started******************************************");
		System.out.println("******************************************Validate Header Started*************************************************");
		actualHeader = driver.getTitle();
		if (actualHeader.contains(expectedHeader))
			{
				Assert.assertEquals(actualHeader, expectedHeader,"The Expected  "+actualHeader+" Found Test Case Pass Succefully");
				System.out.println("The Expected  "+actualHeader+" Found Test Case Pass Succefully");
			}
		else
			{
				System.out.println("The Expected  "+actualHeader+" Not Found Test Case Failed");
			}
		Reporter.log("******************************************Validate Header Ended******************************************");
		System.out.println("******************************************Validate Header Ended*************************************************");
	}
	
	/****************************************************************************************************************
    *  Author: Md Rezaul Karim 
	*  Function Name: ValidateClick
	*  Function Arg: expectedClick ==>Its Element sent from method,TextElement==>Text Element Name That Clicked
	*  FunctionOutPut: It will Validate Expected Element Clicked Or Not
	* 
	* ***************************************************************************************************************/
		
	public static void validateClick(WebElement expectedClick,String TextElement){
		
		Reporter.log("******************************************Validate Clicked Started******************************************");
		System.out.println("******************************************Validate Clicked Started*******************************************");
		if(! expectedClick.isDisplayed())
			{
				System.out.println("The Expected Element *** "+TextElement+" is Clicked Successfully");
				Assert.assertTrue(true,"The Expected Element *** "+TextElement+" is Clicked Successfully");
				Log.info("The Expected Element *** "+TextElement+" is Clicked Successfully");
			}
		else
			{
				System.out.println("The Expected Element *** "+TextElement+" does not Performed Clicked Successfully");
				Assert.assertTrue(false,"The Expected Element *** "+TextElement+" does not Performed Clicked Successfully");
				Log.error("The Expected Element *** "+TextElement+" does not Performed Clicked Successfully");
			}
		Reporter.log("******************************************Validate Clicked Ended******************************************");
		System.out.println("******************************************Validate Clicked Ended*********************************************");
	}
		
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: validateInputValue
	*  Function Arg: expectedEditElement Its Element sent from method,actualEditValue=>The Value That Will Set on Input Field
	*  FunctionOutPut: It will Validate Expected Input Value Set On Input Filed or Not
	* 
	* ***************************************************************************************************************/
	
	public static void validateInputValue(WebElement expectedEditElement,String actualEditValue) {
		
		Reporter.log("******************************************Validate Input Value Started******************************************");
		System.out.println("******************************************Validate Input Value Started***************************************");
		String EditValue=expectedEditElement.getAttribute("value");
		if((EditValue.trim()).equals(actualEditValue))
			{
				System.out.println("The Expected Input Value *** "+actualEditValue+" *** is Successfully Set on Input Box");
				Assert.assertTrue(true,"The Expected Input Value *** "+actualEditValue+" *** is Successfully Set on Input Box");
				Log.info("The Expected Input Value *** "+actualEditValue+" *** is Successfully Set on Input Box");
			}
		else
			{
				System.out.println("The Expected Input Value *** "+actualEditValue+" *** Does not Set on Input Box Actual Input Value Was  *** "+EditValue+" ***");
				Assert.assertTrue(false,"The Expected Input Value *** "+actualEditValue+" *** Does not Set on Input Box Actual Input Value Was  *** "+EditValue+" ***");
				Log.error("The Expected Input Value *** "+actualEditValue+" *** Does not Set on Input Box Actual Input Value Was  *** "+EditValue+" ***");
			}
		Reporter.log("******************************************Validate Input Value Ended******************************************");
		System.out.println("******************************************Validate Input Value Ended*****************************************");
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: validateDropValue
	*  Function Arg: expectedDropElement ==> Its Element sent from method, ActualSelectedValue==>The Value That Will Set on Input Field
	*  FunctionOutPut: It will Validate Expected Input Value Set On Input Filed or Not
	*
	**************************************************************************************************************/
	
	public static void validateDropValue(WebElement expectedDropElement,String ActualSelectedValue) throws InterruptedException {
		
		Reporter.log("****************************************** Validate Drop Value Started ******************************************");
		System.out.println("******************************************Validate Drop Value Started ***************************************");
		
		String SelectedValue=expectedDropElement.getAttribute("value");
		if((SelectedValue.trim()).equals(ActualSelectedValue.trim()))
			{
				System.out.println("The Expected Selected Input Value *** "+SelectedValue+" *** is Successfully Set on Drop Down List");
				Assert.assertTrue(true,"The Expected Selected Input Value *** "+SelectedValue+" *** is Successfully Set on Drop Down List");
				Log.info("The Expected Selected Input Value *** "+SelectedValue+" *** is Successfully Set on Drop Down List");
			}
		else
			{
				System.out.println("The Expected Selected Input Value *** "+ActualSelectedValue+" *** Does not Set on Drop Down List The Actual Selected Input Value Was  *** "+SelectedValue+" ***");
				Assert.assertTrue(false,"The Expected Selected Input Value *** "+ActualSelectedValue+" *** Does not Set on Drop Down List The Actual Selected Input Value Was  *** "+SelectedValue+" ***");
				Log.error("The Expected Selected Input Value *** "+ActualSelectedValue+" *** Does not Set on Drop Down List The Actual Selected Input Value Was  *** "+SelectedValue+" ***");
			}
		Reporter.log("******************************************Validate Drop Value Ended******************************************");
		System.out.println("******************************************Validate Drop Value Ended******************************************");
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: ValidateStringShort
	*  Function Arg: expectedEditElement Its Element sent from method,ActualEditValue=>The Value That Will Set on Input Field
	*  FunctionOutPut: It will Validate Expected Input Value Set On Input Filed or Not
	* ***************************************************************************************************************/
	
	public static void validateStringShort(String ExpectedValue[], String Locator) {
		
		Reporter.log("******************************************Validate String Short Started******************************************");
		System.out.println("******************************************Validate String Short Started**************************************");
		List<WebElement> objCol = driver.findElements(By.xpath(Locator));
		ArrayList<String> originalList = new ArrayList<String>();
		for (int i = 0; i < objCol.size(); i++)
			{
				originalList.add(objCol.get(i).getText());
			}
		ArrayList<String> copyList = new ArrayList<String>();
		for (int i = 0; i < originalList.size(); i++)
			{
				copyList.add(originalList.get(i));
			}
		Collections.sort(copyList);
		Assert.assertTrue(originalList.equals(copyList), "Expected Value Are Shorted:");
		Reporter.log("******************************************Validate String Short Ended******************************************");
		System.out.println("******************************************Validate String Short Ended****************************************");
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: ValidateBrookenLink
	*  Function Arg: expectedEditElement Its Element sent from method,ActualEditValue=>The Value That Will Set on Input Field
	*  FunctionOutPut: It will Validate Expected Input Value Set On Input Filed or Not
	* 
	* ***************************************************************************************************************/

	public static void validateBrookenLink(String Locator, int TotalLink) throws InterruptedException {

		Reporter.log("******************************************Validate Brooken Link Started ******************************************");
		System.out.println("******************************************Validate Brooken Link Started *************************************");
		List<WebElement> objLink = driver.findElements(By.tagName("a"));
		int totalLink = objLink.size();
		int j=0;
		String CtrLink = Keys.chord(Keys.CONTROL, Keys.ENTER);
		for (int i = 0; i < totalLink; i++)
		{
			objLink.get(i).sendKeys(CtrLink);
			Thread.sleep(2000);
			j++;
			if(TotalLink>0)
			{
				if(j==TotalLink)
				{
					break;
				}
			}
		}
		Set<String> objWindow = driver.getWindowHandles();
		java.util.Iterator<String> it = objWindow.iterator();
		while (it.hasNext()) 
		{
			String currentTitle = driver.switchTo().window(it.next()).getTitle();
			System.out.println(currentTitle);
		}
		Reporter.log("******************************************Validate Brooken Link Ended******************************************");
		System.out.println("******************************************Validate Brooken Link Ended****************************************");
	}

	////******************************   All Input And Random Data Function  ***********************************************************
	
	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name: getInput
	 *  Function Arg: No Arguments
	 *  FunctionOutPut: It will get input from keyboard
	 * 
	 * ***************************************************************************************************************/
	public static void  getInput() {
		
		Reporter.log("******************************************Get Input Stared******************************************");
		System.out.println("******************************************Get Input Stared***************************************************");
		Scanner objInputValue = new Scanner(System.in);
		String inputValue = "";
		while (!(inputValue.equalsIgnoreCase("Exit"))) {
			System.out.println(" Please Enter Your Value:(For Exit Please Enter Exit:)=>");
			inputValue = objInputValue.nextLine();
			if(!inputValue.equalsIgnoreCase("exit"))
				{
					System.out.println("You Enter :" + inputValue );
				}
		}
		System.out.println("Exit from input taker ");
		Reporter.log("******************************************Get Input Ended******************************************");
		System.out.println("******************************************Get Input Ended****************************************************");
	}
	
	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name:randomNumeric
	 *  Function Arg: StringSize how many digit do you want Number
	 *  FunctionOutPut: It will get input from function and return Random numeric Number String
	 * 
	 * ***************************************************************************************************************/
	
	public static String randomNumeric(int stringSize) {
		
		Reporter.log("******************************************Create Random Numeric Strated******************************************");
		System.out.println("******************************************Create Random Numeric Strated**************************************");
		String AlphaNumericString ="0123456789"; 
		StringBuilder objString = new StringBuilder(stringSize);
		for(int i=0;i<stringSize;i++)
		{
			// generate a random number between 0 to AlphaNumericString variable length
			int index=(int) (AlphaNumericString.length()*Math.random());
			objString.append(AlphaNumericString.charAt(index));
		}
		String randomNumeric=objString.toString();
		System.out.println(objString.toString());
		Reporter.log("******************************************Create Random Numeric Ended******************************************");
		System.out.println("******************************************Create Random Numeric Ended****************************************");
		return randomNumeric;
	}
	
	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name:RandomDecimal 
	 *  Function Arg: StringSize how many digit do you want Number
	 *  FunctionOutPut: It will get input from function and return Random numeric Number String
	 * 
	 * ***************************************************************************************************************/
	
	public static String randomDecimal  (int stringSize) {
		
		Reporter.log("******************************************Create Random Decimal Number Started******************************************");
		System.out.println("******************************************Create Random Decimal Number Started*******************************");
		String AlphaNumericString ="0123456789"; 
		StringBuilder objString = new StringBuilder(stringSize);
		for(int i=0;i<stringSize;i++)
		{
			// generate a random number between 0 to AlphaNumericString variable length
           	int index=(int) (AlphaNumericString.length()*Math.random());
			objString.append(AlphaNumericString.charAt(index));
		}
		String randomDecimal =objString.toString()+".49";
		System.out.println(objString.toString());
		Reporter.log("******************************************Create Random Decimal Number Ended******************************************");
		System.out.println("******************************************Create Random Decimal Number Ended****************************************");
		return randomDecimal ;
	}
	
	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name:RandomAlphaNumeric
	 *  Function Arg: StringSize how many digit do you want string
	 *  FunctionOutPut: It will get input from function and return Random Alpha numeric String
	 * 
	 * ************************************************************************************************************** */
	
	public static String randomAlphaNumeric(int stringSize) {
		
		Reporter.log("******************************************Create Random Alpha Numeric Started******************************************");
		System.out.println("******************************************Create Random Alpha Numeric Started********************************");
		
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789"+ "abcdefghijklmnopqrstuvxyz"; 
		StringBuilder objString = new StringBuilder(stringSize);
		for(int i=0;i<stringSize;i++)
		{
			// generate a random number between 0 to AlphaNumericString variable length
            int index=(int) (AlphaNumericString.length()*Math.random());
			objString.append(AlphaNumericString.charAt(index));
		}
		String randomAlphaNumeric=objString.toString();
		System.out.println(objString.toString());
		
		Reporter.log("******************************************Create Random Alpha Numeric Ended******************************************");
		System.out.println("******************************************Create Random Alpha Numeric Ended**********************************");
		return randomAlphaNumeric;
	}

	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name:RandomUpperLower
	 *  Function Arg: StringSize how many digit do you want string
	 *  FunctionOutPut: It will get input from function and return Random Alpha  character upper and lower case String
	 * 
	 * ***************************************************************************************************************/
	
	public static String randomUpperLower(int stringSize) {
		
		Reporter.log("******************************************Create Random Upper Lower Case String Started******************************************");
		System.out.println("******************************************Create Random Upper Lower Case String Started**********************");
		
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz"; 
		StringBuilder objString = new StringBuilder(stringSize);
		for(int i=0;i<stringSize;i++)
		{
			// generate a random number between 0 to AlphaNumericString variable length 
            int index=(int) (AlphaNumericString.length()*Math.random());
			objString.append(AlphaNumericString.charAt(index));
		}
		String randomUpperLower=objString.toString();
		System.out.println(objString.toString());
		
		Reporter.log("******************************************Create Random Upper Lower Case String Ended******************************************");
		System.out.println("******************************************Create Random Upper Lower Case String Ended************************");
		return randomUpperLower;
	}

	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name:RandomAlphaNumericSpeceal
	 *  Function Arg: StringSize how many digit do you want string
	 *  FunctionOutPut: It will get input from function and return Random Alpha numeric and special character String
	 * 
	 * ***************************************************************************************************************/
	
	public static String randomAlphaNumericSpeceal(int stringSize) {
		
		Reporter.log("******************************************Create Random Numeric Speceal String Started******************************************");
		System.out.println("******************************************Create Random Numeric Speceal String Started***********************");
		
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789"+"!@#$%^&*()_+<>?"+ "abcdefghijklmnopqrstuvxyz"; 
		StringBuilder objString = new StringBuilder(stringSize);
		for(int i=0;i<stringSize;i++)
		{
			// generate a random number between 0 to AlphaNumericString variable length
            int index=(int) (AlphaNumericString.length()*Math.random());
			objString.append(AlphaNumericString.charAt(index));
		}
		String randomAlphaNumericSpeceal=objString.toString();
		System.out.println(objString.toString());
		
		Reporter.log("******************************************Create Random Numeric Speceal String Ended******************************************");
		System.out.println("******************************************Create Random Numeric Speceal String Ended****************************************");
		
		return randomAlphaNumericSpeceal;
	}
	
	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name:setSelect
	 *  Function Arg: ExpSelect Expected Select Element Locator 
	 *  FunctionOutPut: It will create a object for select class
	 * 
	 * ***************************************************************************************************************/
	
	public static Select setSelect(WebElement ExpSelect){
		
		Select obs=new Select(ExpSelect);
		return obs;
	}
	
	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name:setAction
	 *  Function Arg: 
	 *  FunctionOutPut: It will create a object for Action class
	 * 
	 * **************************************************************************************************************/
	
	public static Actions setAction(){
	
		Actions objaction=new Actions(driver);
		return objaction;
	}

	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name:clickElementByJs
	 *  Function Arg: ExpElement element locator
	 *  FunctionOutPut: It will create a object for Action class
	 * 
	 * **************************************************************************************************************/
	
	public static void clickElementByJs(WebElement ExpElement){
		
		JavascriptExecutor objjs=((JavascriptExecutor)driver);
		objjs.executeScript("arguments[0].click()",ExpElement);
	}
	
	/****************************************************************************************************************
	 *  Author: Md Rezaul Karim 
	 *  Function Name:refreshByJs
	 *  Function Arg: ExpElement element locator
	 *  FunctionOutPut: It will create a object for Action class
	 * 
	 * **************************************************************************************************************/
	
	public static void refreshByJs(){
		
		JavascriptExecutor objjs=((JavascriptExecutor)driver);
		objjs.executeScript("history.go(0)");
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: SetClander
	*  Function Arg: String dateLocator, String monthLocator, String yearLocator, String nextLocator,String expectedDate
	*  FunctionOutPut: It will Select value from drop down when drop down is not able to select by select tag
	* 
	* ***************************************************************************************************************/
	
	public static void SetClander(String dateLocator, String monthLocator, String yearLocator, String nextLocator,String expectedDate) throws InterruptedException {
		
		Reporter.log("******************************************Set Clander Started******************************************");
		System.out.println("******************************************Set Clander Started************************************************");
		
		String expDate[] = expectedDate.split("/");
		String Month=expDate[0];
		String day=expDate[1];
		String years=expDate[2];
		List<WebElement> objDate = driver.findElements(By.xpath(dateLocator));
		if (years.length() < 3)
			{
			years = ("20" + years);
			}
		else 
			{
			years = years;
			}
		for (int i = 0; i < 11; i++)
		{
			WebElement objMonth = driver.findElement(By.xpath(monthLocator));
			WebElement objYear = driver.findElement(By.xpath(yearLocator));
			String month = objMonth.getText();
			String year = objYear.getText();
			if (month.toLowerCase().contains(Month.toLowerCase()))
				{
					if (year.toLowerCase().contains(years.toLowerCase()))
						{
							break;
						}
				} 
			else
				{
					driver.findElement(By.xpath(nextLocator)).click();
				}
		}
		List<WebElement> listDate = driver.findElements(By.xpath(dateLocator));
		int totalDate = listDate.size();
		for (int i = 0; i < totalDate; i++)
		{
			String actualDate = listDate.get(i).getText();
			String reActualDate = actualDate.trim();
			if (reActualDate.contains(day))
				{
					if(listDate.get(i).isEnabled())//check if date is enable 
					{
						listDate.get(i).click();
						break;
					}
					else
					{
						System.out.println("The Date you want select is Disable");
					}	
				}
		}
		Reporter.log("******************************************Set Clander Ended******************************************");
		System.out.println("******************************************Set Clander Ended******************************************************");
	}	
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: selectByJs
	*  Function Arg: WebElement expElement,String ExpValue
	*  FunctionOutPut: It will Select value from drop down when drop down is not able to select by select tag
	* 
	* ***************************************************************************************************************/
	
	public static void selectByJs(WebElement expElement,String ExpValue){
		Reporter.log("****************************************** Select By Js Strated ******************************************");
		System.out.println("****************************************** select By Js Strated ******************************************************");
	
		JavascriptExecutor js=(JavascriptExecutor)driver;
		js.executeScript("arguments[0].setAttribute('value','"+ExpValue+"');", expElement);
		Reporter.log("****************************************** Select By Js Ended ******************************************");
		System.out.println("****************************************** Select By Js Ended ******************************************************");
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: AutoSuggestDropDown
	*  Function Arg: String expSearchLocator  ==>Search Field Locator, String selectValue  ==> The Value user want from drop down 
	*  FunctionOutPut: It will Select value from drop down when drop down is not able to select by select tag
	* 
	* ***************************************************************************************************************/
	public static void AutoSuggestDropDown(String expSearchLocator, String selectValue) {
		
		Reporter.log("******************************************Auto Suggest Drop Down Started******************************************");
		System.out.println("******************************************Auto Suggest Drop Down Started*************************************");
		String getSearchValue;
		List<WebElement>	objElements=driver.findElements(By.xpath(expSearchLocator));
		int totalElementFromDrop=objElements.size();
		//JavascriptExecutor objJs = (JavascriptExecutor) driver;
		
		//String getText = (String) objJs.executeScript("return arguments[0].value;",ScriptLocator);
		for(int i = 0;i<totalElementFromDrop;i++)
		{
			getSearchValue=objElements.get(i).getText();
			if(getSearchValue.equalsIgnoreCase(selectValue))
			{
				objElements.get(i).click();
				System.out.println(getSearchValue);
				break;
			}
		}
		
		Reporter.log("******************************************Auto Suggest Drop Down Ended******************************************");
		System.out.println("******************************************Auto Suggest Drop Down Ended****************************************");
	}
	
	//*******************************************          Close                 *****************************************************//
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: CloseBrowser
	*  Function Arg: expCloseBrowser ==> Do You Want Close All Browser that open or just current browser if user does not provied any it will close all browser
	*  FunctionOutPut: It will close all browser or current browser
	* 
	* ***************************************************************************************************************/
	
	public static void closeBrowser(String expCloseBrowser) {
		
		Reporter.log("****************************************** Expected Browser Close Started ******************************************");
		System.out.println("****************************************** Expected Browser Close Started ***********************************");
		if((expCloseBrowser.toLowerCase()).contains("current"))
		{
			driver.close();
		}
		else
		{
			driver.quit();
		}
		
		driver=null;
		Reporter.log("******************************************Expected Browser Closed ******************************************");
		System.out.println("****************************************** Expected Browser Closed ******************************************");
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: closeExpectedWindow
	*  Function Arg: expWindowTabClose  ==> it will take number of child window tab that user want close
	*  FunctionOutPut: close child window
	* 
	* **************************************************************************************************************/
	
	public static void closeExpectedWindow(String expWindowTabClose){
		
		String[] ExTab=expWindowTabClose.split(",");
		int totalTab=ExTab.length;
		String PareantWindow=driver.getWindowHandle();
		System.out.println("No. of tabs: " + PareantWindow);
		Set<String> objWhandles = driver.getWindowHandles();
		ArrayList<String> objTab=new ArrayList<String>(objWhandles);
		int TotalWindow=objWhandles.size();
		for(int i=0;i<totalTab;i++)
			{
				driver.switchTo().window(objTab.get(Integer.parseInt(ExTab[i]))).close();
				System.out.println("No. of tabs: " +TotalWindow);	
			}
		driver.switchTo().window(PareantWindow);
	}
	
	/****************************************************************************************************************
	*  Author: Md Rezaul Karim 
	*  Function Name: CloseAllChildWindow
	*  Function Arg: it will close all  child window tab that user open
	*  FunctionOutPut: close all child window
	* 
	* ***************************************************************************************************************/
		
	public static void CloseAllChildWindow(){
		
		String PareantWindow=driver.getWindowHandle();
		System.out.println("No. of tabs: " + PareantWindow);
		Set<String> objWhandles = driver.getWindowHandles();
		int TotalWindow=objWhandles.size();
		for(String child:objWhandles)
			{
				if(!PareantWindow.equalsIgnoreCase(child))
				{
					driver.switchTo().window(child).close();
					System.out.println("No. of tabs: " +TotalWindow);
				}
			}
		driver.switchTo().window(PareantWindow);
	}
	
	
	
	
	
}
