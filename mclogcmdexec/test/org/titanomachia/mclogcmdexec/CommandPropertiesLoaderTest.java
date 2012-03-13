package org.titanomachia.mclogcmdexec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.titanomachia.mclogcmdexec.CommandMetaData;
import org.titanomachia.mclogcmdexec.CommandPropertiesLoader;

public class CommandPropertiesLoaderTest {

	@Test
	public void testLoadProperties() {
		CommandPropertiesLoader propertiesLoader = new CommandPropertiesLoader() {
			@Override
			void loadProperties(Properties properties) throws IOException, FileNotFoundException {
				properties.put("C", "org.rtd.command.TestCommand");
				properties.put("D", "org.rtd.command.TestCommand");
				properties.put("D.authorizedUsers", "testuser");
			}
		};
		Map<String, CommandMetaData> commandMetaDataByName = propertiesLoader.loadCommands();
		System.out.println(commandMetaDataByName);
	}
}
