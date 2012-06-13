package org.xeneo.db;

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
import org.xeneo.core.plugin.PluginConfiguration;
import org.xeneo.core.plugin.PluginRepository;
import org.xeneo.core.plugin.PluginType;
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

    public JdbcPluginRepositoryTest() {
    }

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

    @Test
    public void testAddPlugin() {

        List<PluginConfiguration> pds = pUtil.createRandomPluginConfigurations(n, PluginType.ACTIVITY_PLUGIN);
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
        
        List<PluginConfiguration> pds = pUtil.createRandomPluginConfigurations(n, PluginType.ACTIVITY_PLUGIN);
        logger.info("Start configurint Plugins: " + pds.size());
        
        for (PluginConfiguration pc : pds) {
            pm.configurePlugin(pc);
        }
        
        
        
    }
    
    @Test
    public void testListPlugins() {
        
        List<PluginConfiguration> list = pm.listAvailablePlugins("http://someone.com", new PluginType[]{PluginType.ACTIVITY_PLUGIN});
        
        for (PluginConfiguration pc : list) {
            logger.info(pc.toString());
        }
        
    }
   
}
