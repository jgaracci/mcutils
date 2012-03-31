package org.titanomachia.mclogcmdexec;

import org.junit.Test;

public class ApplicationContextTest {

	@Test
	public void test() {
		ApplicationContext.setFilePath("classes" + System.getProperty("file.separator") + getClass().getPackage().getName().replace(".", System.getProperty("file.separator")) + System.getProperty("file.separator") + "testContext.dat");
		
		ApplicationContext.load();
		
		ApplicationContext.setValue("test.value", "Test");
		
		ApplicationContextTestObject object = new ApplicationContextTestObject();
		object.setValue(10);
		
		ApplicationContext.setValue("test.problem", object);
		
		ApplicationContext.save();
	}

}
