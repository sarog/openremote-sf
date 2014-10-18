 //Error reading included file Templates/UnitTests/Templates/Licenses/license-OpenRemote.txt
package org.openremote.controller.protocol.ictprotege;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.openremote.controller.ICTProtegeConfiguration;
import org.openremote.controller.command.Command;
import org.openremote.controller.protocol.ictprotege.network.ProtegeConnectionManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


/**
 * 
 * @author Adam Mcnabb
 */
@RunWith(PowerMockRunner.class)
@MockPolicy(MockICTProtegeConfigPolicy.class)
public class ProtegeCommandBuilderTest {
    
    private static Element idOne;
    private static Element idTwo;
    private static Element idThree;
    private static Element propertyIndex, propertyCommand, propertyValue, propertyType;
    private static ProtegeCommandBuilder commandBuilderTest;
    private static ProtegeConnectionManager connectionManager;
    
    public ProtegeCommandBuilderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
            
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    @Before
    public void setUp() {
        idOne = new Element("command");
        idOne.setAttribute("id", "1");        
        
        propertyIndex = new Element("property");
        propertyIndex.setAttribute("name", "record-index");
        propertyIndex.setAttribute("value", "1");
        propertyCommand = new Element("property");
        propertyCommand.setAttribute("name", "record-command");
        propertyCommand.setAttribute("value", "Door Lock");
        propertyValue = new Element("property");
        propertyValue.setAttribute("name", "record-value");
        propertyValue.setAttribute("value", "1");
        propertyType = new Element("property");
        propertyType.setAttribute("name", "record-type");
        propertyType.setAttribute("value", "Door");
        idOne.addContent(propertyIndex);
        idOne.addContent(propertyCommand);
        idOne.addContent(propertyType);
        
        idTwo = new Element("command");
        idTwo.setAttribute("id", "2");
        propertyIndex = new Element("property");
        propertyIndex.setAttribute("name", "record-index");
        propertyIndex.setAttribute("value", "1");
        propertyCommand = new Element("property");
        propertyCommand.setAttribute("name", "record-command");
        propertyCommand.setAttribute("value", "Door Lock");
        propertyValue = new Element("property");
        propertyValue.setAttribute("name", "record-value");
        propertyValue.setAttribute("value", "1");
        propertyType = new Element("property");
        propertyType.setAttribute("name", "record-type");
        propertyType.setAttribute("value", "Door");
        idTwo.addContent(propertyIndex);
        idTwo.addContent(propertyCommand);
        idTwo.addContent(propertyType);
        
        idThree = new Element("command");
        idThree.setAttribute("id", "3");
        propertyIndex = new Element("property");
        propertyIndex.setAttribute("name", "record-index");
        propertyIndex.setAttribute("value", "1");
        propertyCommand = new Element("property");
        propertyCommand.setAttribute("name", "record-command");
        propertyCommand.setAttribute("value", "Door Lock");
        propertyValue = new Element("property");
        propertyValue.setAttribute("name", "record-value");
        propertyValue.setAttribute("value", "1");
        propertyType = new Element("property");
        propertyType.setAttribute("name", "record-type");
        propertyType.setAttribute("value", "Door");
        idThree.addContent(propertyIndex);
        idThree.addContent(propertyCommand);
        idThree.addContent(propertyType);
        
        commandBuilderTest = PowerMockito.spy(new ProtegeCommandBuilder());
        try {
            PowerMockito.doNothing().when(commandBuilderTest, "loadConfiguration");
        } catch (Exception ex) {
            Logger.getLogger(ProtegeCommandBuilderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
        idOne = null;
        idTwo = null;
        idThree = null;
        connectionManager = null;
        commandBuilderTest = null;
    }

    /**
     * Test of build method, of class ProtegeCommandBuilder.
     */
    @Test
    public void testBuild() {
        Command resultOne = commandBuilderTest.build(idOne);
        assertNotNull(resultOne);
        Command resultTwo = commandBuilderTest.build(idTwo);
        assertNotNull(resultTwo);
        assertNotSame(resultTwo, resultOne);
        Command resultThree = commandBuilderTest.build(idThree);
        assertNotNull(resultThree);
        verify(commandBuilderTest).build(idOne);
        verify(commandBuilderTest).build(idTwo);
        verify(commandBuilderTest).build(idThree);
        assertNotSame(resultTwo, resultThree);
        assertNotSame(resultOne, resultThree);
        assertTrue(resultOne instanceof Command);
        assertTrue(resultTwo instanceof Command);
        assertTrue(resultThree instanceof Command);
    }
}
