package com.crm.base;

import java.util.List;
import java.util.Properties;
import org.testng.TestNG;
import org.testng.collections.Lists;

public class TestRunner {
	public static Properties config = new Properties();


	static TestNG testng;
	public static void main(String[] args) throws ClassNotFoundException {
		// TODO Auto-generated method stub

		config = SetUp.loadConfig();

		TestNG testng = new TestNG();

		List<String> suites = Lists.newArrayList();

		System.out.println(System.getProperty("user.dir") + config.getProperty("TestNgPath"));

		try {

			suites.add( System.getProperty("user.dir") + config.getProperty("TestNgPath"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testng.setTestSuites(suites);
		testng.run();
	}

}
