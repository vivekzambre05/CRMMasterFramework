package com.crm.testCases;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.crm.commonUtilities.CommonMethods;
import com.crm.pages.CRMLogin.LoginPage;
import com.crm.base.SetUp;



public class TC01VerifyCRMLogin extends SetUp
{
	public LoginPage login;
	@Test
	public void VerifyCRMLogin() throws Exception
	{
	
	//sheetName from Excel to pass the testdata
	String sheetName = "VerifyCRMLogin";
	  
	 //To check  testcase runmode from excel (Yes/No) if yes then launch Browser and execute script
	  if (!(CommonMethods.isTestRunnable("VerifyCRMLogin",sheetName))) {

			throw new SkipException(
					"Skipping the test VerifyCRMLogin as the Run mode is NO");
		}
	   		//login to CRM
				login = new LoginPage(driver);
				login.CRMLogin(sheetName);
				
				login.Logout();
	  
	  
	}
}
