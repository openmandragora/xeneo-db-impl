package org.xeneo.db.testutils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import org.xeneo.core.plugin.PluginConfiguration;
import org.xeneo.core.plugin.PluginDescriptor;

/**
 *
 * @author Stefan Huber
 */
public class PluginUtil {

    public List<PluginDescriptor> createRandomPlugins(int i, String[] types) {
        List<PluginDescriptor> list = new ArrayList<PluginDescriptor>();
        for (int j = 0; j < i; j++) {

            PluginDescriptor pd = new PluginDescriptor();
            pd.setTitle("Plugin Title " + j);
            pd.setDescription("Plugin Description " + j);
            pd.setPluginURI("http://test.com/plugin/" + Calendar.getInstance().getTimeInMillis() + "/" + j);
            pd.setPluginType(types[j % types.length]);
            pd.setID(Calendar.getInstance().getTimeInMillis());
            pd.setPluginClass("org.xeneo.Plugin" + j);
            list.add(pd);
        }

        return list;
    }

    public List<PluginConfiguration> createRandomPluginConfigurations(int i, String pluginURI, String ownerURI) {
        List<PluginConfiguration> pcs = new ArrayList<PluginConfiguration>();

        for (int j = 0; j < i; j++) {

            PluginConfiguration pc = new PluginConfiguration();
            pc.setOwnerURI(ownerURI);
            pc.setPluginURI(pluginURI);

            Properties props = new Properties();
            props.setProperty("testProperty1" + j, "something1");
            props.setProperty("testProperty2" + j, "something2");
            props.setProperty("testProperty3" + j, "something3");
            props.setProperty("testProperty4" + j, "something4");

            pc.setProperties(props);
            pc.setProperties(props);
            
            pcs.add(pc);
        }

        return pcs;
    }
    
    public PluginConfiguration createRandomPluginConfiguration(String pluginURI, String ownerURI) {
        return this.createRandomPluginConfigurations(1, pluginURI, ownerURI).get(0);
    }
}
