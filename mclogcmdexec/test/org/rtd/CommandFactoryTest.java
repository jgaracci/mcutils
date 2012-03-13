package org.rtd;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.rtd.command.Command;
import org.rtd.command.D20;

public class CommandFactoryTest {
    @Test
    public void testCommandMatching() {
        Map<String, CommandMetaData> COMMANDS = new HashMap<String, CommandMetaData>();
        CommandMetaData commandMetaData = new CommandMetaData(D20.class, new HashSet<String>());
        COMMANDS.put("D20", commandMetaData);
        
        CommandFactory factory = new CommandFactory(COMMANDS);
        Command command = factory.getCommand("10:00:00 CONSOLE <testuser> d20 batman");
        Assert.assertNotNull(command);
        
        command = factory.getCommand("10:00:00 CONSOLE <testuser> batman d20");
        Assert.assertNull(command);
    }
    @Test
    public void testCommandAuthorization() {
        Map<String, CommandMetaData> COMMANDS = new HashMap<String, CommandMetaData>();
        CommandMetaData commandMetaData = new CommandMetaData(D20.class, new HashSet<String>(Arrays.asList("testuser")));
        COMMANDS.put("D20", commandMetaData);
        
        CommandFactory factory = new CommandFactory(COMMANDS);
        Command command = factory.getCommand("10:00:00 CONSOLE <testuser> d20 batman");
        Assert.assertNotNull(command);
        
        command = factory.getCommand("10:00:00 CONSOLE <testuser> batman d20");
        Assert.assertNull(command);
        
        command = factory.getCommand("10:00:00 CONSOLE <badtestuser> d20 batman");
        Assert.assertNull(command);
    }
}
