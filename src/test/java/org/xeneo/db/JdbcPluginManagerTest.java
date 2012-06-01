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
import org.xeneo.core.plugin.PluginDescriptor;
import org.xeneo.core.plugin.PluginManager;
import org.xeneo.db.testutils.PluginUtil;

/**
 *
 * @author Stefan Huber
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcPluginManagerTest {
    
    @Autowired
    PluginManager pm;
    
    @Autowired
    PluginUtil pUtil;
    
    private final int n = 10;
    
    Logger logger = LoggerFactory.getLogger(JdbcPluginManagerTest.class);
    
    public JdbcPluginManagerTest() {
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
        
        List<PluginDescriptor> pds = pUtil.createRandomPlugins(n,new String[]{PluginDescriptor.ACTIVITY_PLUGIN_TYPE});
        logger.info("Start adding Plugins: " + pds.size());
        
        Iterator<PluginDescriptor> it1 = pds.iterator();
        while(it1.hasNext()) {
            
            PluginDescriptor p = it1.next();
            
            logger.info("Try to add plugin: " + p.getPluginURI());
            pm.addPlugin(p);           
        }      
        
        Iterator<PluginDescriptor> it2 = pds.iterator();
        while(it2.hasNext()) {
            
            PluginDescriptor p = it2.next();
            
            logger.info("Try to update plugin: " + p.getPluginURI());
            pm.addPlugin(p);            
        }           
    }
    
    @Test
    public void testDeactivatePlugin() {
        String[] types = {PluginDescriptor.ACTIVITY_PLUGIN_TYPE};                
        List<PluginDescriptor> pds = pm.listAvailablePlugins(types);
        int before = pds.size();
        int after;
        
        Iterator<PluginDescriptor> it = pds.iterator();
        int i = 0;
        while (it.hasNext() && i < 3) {
            PluginDescriptor pd = it.next();
            logger.info("Try to deactivate Plugin with URI: " + pd.getPluginURI());
            pm.deactivatePlugin(pd.getPluginURI());
            
            i++;
        }
                
        pds = pm.listAvailablePlugins(types);
        after = pds.size();
        
        assertTrue(before > after);
    }
    
    @Test
    public void testListPlugins() {
        
        String[] types = {PluginDescriptor.ACTIVITY_PLUGIN_TYPE, PluginDescriptor.ACTIVITY_LISTENER_PLUGIN_TYPE};                
        List<PluginDescriptor> pds = pm.listAvailablePlugins(types);
        
        int before = pds.size();
        
        pm.deactivatePlugin(pds.get(0).getPluginURI());
        pds = pm.listAvailablePlugins(types);
        assertTrue(before > pds.size());
        
        before = pds.size();                
        pm.addPlugin(pUtil.createRandomPlugins(1, new String[]{PluginDescriptor.ACTIVITY_LISTENER_PLUGIN_TYPE}).get(0));  
        pds = pm.listAvailablePlugins(types);
        
        assertTrue(before < pds.size());
    }
    
    @Test
    public void testGetPluginDescriptor() {
        List<PluginDescriptor> pds = pUtil.createRandomPlugins(2, new String[]{PluginDescriptor.ACTIVITY_LISTENER_PLUGIN_TYPE});
        
        pm.addPlugin(pds.get(0));
        pm.addPlugin(pds.get(1));
        
        PluginDescriptor p1 = pm.getPluginDescriptor(pds.get(0).getPluginURI());
        
        logger.info(p1.getId() + " " + pds.get(0).getId());
        
        assertTrue(p1.getTitle().equals(pds.get(0).getTitle()));
        
        PluginDescriptor p2 = pm.getPluginDescriptor(pds.get(1).getPluginURI());
        assertTrue(p2.getTitle().equals(pds.get(1).getTitle()));
        
    }
}
