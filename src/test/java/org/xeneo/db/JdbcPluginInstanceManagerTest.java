/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xeneo.core.plugin.PluginConfiguration;
import org.xeneo.core.plugin.PluginDescriptor;
import org.xeneo.core.plugin.PluginInstanceManager;
import org.xeneo.core.plugin.PluginManager;
import org.xeneo.db.testutils.PluginUtil;

/**
 *
 * @author Stefan Huber
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcPluginInstanceManagerTest {

    @Autowired
    PluginInstanceManager pim;
    @Autowired
    PluginManager pm;
    @Autowired
    PluginUtil pUtils;
    Logger logger = LoggerFactory.getLogger(JdbcPluginInstanceManagerTest.class);

    public JdbcPluginInstanceManagerTest() {
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
    public void testPluginManager() {
        String[] types = {PluginDescriptor.ACTIVITY_PLUGIN_TYPE, PluginDescriptor.ACTIVITY_LISTENER_PLUGIN_TYPE};
        List<PluginDescriptor> pds = pm.listAvailablePlugins(types);

        logger.info("Plugins available: " + pds.size());

        if (pds.size() < 1) {
            logger.info("Create some random Plugins...");
            List<PluginDescriptor> l = pUtils.createRandomPlugins(50, types);
            Iterator<PluginDescriptor> it = l.iterator();
            while (it.hasNext()) {
                pm.addPlugin(it.next());
            }
        }
    }

    @Test
    public void testPluginInstances() throws Exception {
        String[] types = {PluginDescriptor.ACTIVITY_PLUGIN_TYPE, PluginDescriptor.ACTIVITY_LISTENER_PLUGIN_TYPE};
        List<PluginDescriptor> pds = pm.listAvailablePlugins(types);

        
        if (pds.size() < 1) {
            throw new Exception("no Plugins available!");
        }

        PluginDescriptor pd = pds.get(1);

        String owner = "http://stefanhuber.at/me";
        String plugin = pd.getPluginURI();

        pim.createPluginInstance(plugin, owner);
        pim.createPluginInstance(plugin, owner);
        pim.createPluginInstance(plugin, owner);
        pim.createPluginInstance(plugin, owner);
        pim.createPluginInstance(plugin, owner);
        pim.createPluginInstance(plugin, owner);

        pim.removePluginInstance(plugin, owner);

        pim.createPluginInstance(plugin, owner);

    }

    @Test
    public void testPluginStory() throws Exception {
        String[] types = {PluginDescriptor.ACTIVITY_PLUGIN_TYPE, PluginDescriptor.ACTIVITY_LISTENER_PLUGIN_TYPE};
        List<PluginDescriptor> pds = pm.listAvailablePlugins(types);

        if (pds.size() < 1) {
            throw new Exception("no Plugins available!");
        }

        PluginDescriptor pd = pds.get(1);

        String owner = "http://stefanhuber.at/me";
        String plugin = pd.getPluginURI();

        PluginConfiguration pc = pUtils.createRandomPluginConfiguration(plugin, owner);

        List<PluginConfiguration> pcs = pim.listPluginConfigurations(plugin, owner);
        Iterator<PluginConfiguration> it = pcs.iterator();
        while (it.hasNext()) {
            PluginConfiguration p = it.next();
            logger.info("Plugin Configuration ID: " + p.getID() + " to be removed!");
            pim.removePluginConfiguration(p.getID());
        }

        pcs = pim.listPluginConfigurations(plugin, owner);
        assertTrue(pcs.size() < 1);

        pim.addPluginConfiguration(pc);
        assertTrue(pc.getID() < 0);
        pcs = pim.listPluginConfigurations(plugin, owner);

        assertTrue(pcs.size() == 1);
        PluginConfiguration pc2 = pcs.get(0);

        Properties p1 = pc.getProperties();
        Properties p2 = pc2.getProperties();

        assertEquals(p1.size(), p2.size());

        Iterator<Object> it2 = p1.keySet().iterator();
        while (it2.hasNext()) {
            String key = (String) it2.next();
            assertEquals((String) p1.get(key), (String) p2.get(key));
        }

        pim.removePluginInstance(plugin, owner);

    }
}
