package org.xeneo.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xeneo.core.plugin.*;
import org.xeneo.db.testutils.PluginUtil;

/**
 *
 * @author Stefan Huber
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/plugin-test-config.xml")
public class JdbcPluginRepositoryTest {

    @Autowired
    PluginRepository pm;
    @Autowired
    PluginUtil pUtil;
    private final int n = 10;
    Logger logger = LoggerFactory.getLogger(JdbcPluginRepositoryTest.class);
/*
    public JdbcPluginRepositoryTest() {
        
    }
*/
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    public List<PluginConfiguration> createRandomPluginConfigurations(int i, PluginType pt) {        
        List<PluginConfiguration> list = new ArrayList<PluginConfiguration>();
        
        PluginProperty[] pp = new PluginProperty[3];
        pp[0] = new PluginProperty();
        pp[0].setName("feedUrl");
        pp[0].setType(PluginPropertyType.URI);
        pp[0].setValue("somevalue");
        pp[1] = new PluginProperty();
        pp[1].setName("folderName");
        pp[1].setType(PluginPropertyType.TEXT);
        pp[1].setValue("some folder name");
        pp[2] = new PluginProperty();
        pp[2].setName("email");
        pp[2].setType(PluginPropertyType.EMAIL);
        pp[2].setValue("some@something.at");
        
        for(int j = 0; j < i; j++) {
            
            PluginConfiguration pc = new PluginConfiguration();
            pc.setTitle("Plugin Title " + j + Calendar.getInstance().getTimeInMillis());
            pc.setDescription("Plugin Description " + j + Calendar.getInstance().getTimeInMillis());
            pc.setPluginURI("http://something.com/plugin/" + j);
            pc.setOwnerURI("http://someone.com");
            pc.setPluginType(pt);
            
            pc.setPluginClass("my.classes.Something");
            pc.addProperties(pp);
           
            list.add(j, pc);
            
        }
        
        return list;
    }
    
    @Test
    public void testAddPlugin() {

        List<PluginConfiguration> pds = createRandomPluginConfigurations(n, PluginType.ACTIVITY_PLUGIN);
        logger.info("Start adding Plugins: " + pds.size());
        
        for (PluginConfiguration pc : pds) {
            pm.addPlugin(pc);
            
        }

        for (PluginConfiguration pc : pds) {
            pm.addPlugin(pc);
        }

        for (PluginConfiguration pc : pds) {
            pm.addPlugin(pc);
        }
    }
    
    
    @Test
    public void testConfigurePlugin() {
        
        List<PluginConfiguration> pds = createRandomPluginConfigurations(n, PluginType.ACTIVITY_PLUGIN);
        logger.info("Start configurint Plugins: " + pds.size());
        
        for (PluginConfiguration pc : pds) {
            pm.configurePlugin(pc);
        }
        
        
    }
    
    @Test
    public void testListPlugins() {
        
        List<PluginConfiguration> list = pm.listAvailablePlugins("", new PluginType[]{PluginType.ACTIVITY_PLUGIN});
        for (PluginConfiguration pc : list) {
            
            logger.info(pc.toString());
        }
        
    }
   
}
