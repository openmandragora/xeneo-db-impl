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
import org.xeneo.core.plugin.PluginDescriptor;
import org.xeneo.core.plugin.PluginManager;

/**
 *
 * @author Stefan Huber
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcPluginManagerTest {
    
    @Autowired
    PluginManager pm;
    
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
    
    public List<PluginDescriptor> createRandomPlugins(int i, String[] types) {
        List<PluginDescriptor> list = new ArrayList<PluginDescriptor>();
        
        for (int j = 0; j < i; j++) {            
                        
            PluginDescriptor pd = new PluginDescriptor();
            pd.setTitle("Plugin Title " + j);
            pd.setDescription("Plugin Description " + j);
            pd.setPluginURI("http://test.com/plugin/" + Calendar.getInstance().getTimeInMillis() + "/" + j);
            pd.setPluginType(types[j%types.length]);
            pd.setID(Calendar.getInstance().getTimeInMillis());
            pd.setPluginClass("org.xeneo.Plugin" + j);
            list.add(pd);
        }        
        
        return list;
    }

    @Test
    public void testAddPlugin() {
        
        List<PluginDescriptor> pds = createRandomPlugins(n,new String[]{PluginDescriptor.ACTIVITY_PLUGIN_TYPE});
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
        pm.addPlugin(createRandomPlugins(1, new String[]{PluginDescriptor.ACTIVITY_LISTENER_PLUGIN_TYPE}).get(0));  
        pds = pm.listAvailablePlugins(types);
        
        assertTrue(before < pds.size());
    }
    
    @Test
    public void testGetPluginDescriptor() {
        List<PluginDescriptor> pds = createRandomPlugins(2, new String[]{PluginDescriptor.ACTIVITY_LISTENER_PLUGIN_TYPE});
        
        pm.addPlugin(pds.get(0));
        pm.addPlugin(pds.get(1));
        
        PluginDescriptor p1 = pm.getPluginDescriptor(pds.get(0).getPluginURI());
        
        logger.info(p1.getID() + " " + pds.get(0).getID());
        
        assertTrue(p1.getTitle().equals(pds.get(0).getTitle()));
        
        PluginDescriptor p2 = pm.getPluginDescriptor(pds.get(1).getPluginURI());
        assertTrue(p2.getTitle().equals(pds.get(1).getTitle()));
        
    }
}
