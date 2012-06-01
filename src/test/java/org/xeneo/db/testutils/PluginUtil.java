package org.xeneo.db.testutils;

import java.util.*;
import org.xeneo.core.plugin.PluginConfiguration;
import org.xeneo.core.plugin.PluginDescriptor;

/**
 *
 * @author Stefan Huber
 */
public class PluginUtil {

    public List<PluginDescriptor> createRandomPlugins(int i, String[] types) {
        Set<String> props = new HashSet<String>();
        
        // Some Test Properties
        props.add("Name");
        props.add("FeedUrl");
        props.add("Bonsai");
        props.add("JoshiUrl");
        props.add("Blub");        
        
        List<PluginDescriptor> list = new ArrayList<PluginDescriptor>();
        for (int j = 0; j < i; j++) {

            PluginDescriptor pd = new PluginDescriptor();
            pd.setTitle("Plugin Title " + j);
            pd.setDescription("Plugin Description " + j);
            pd.setPluginURI("http://test.com/plugin/" + Calendar.getInstance().getTimeInMillis() + "/" + j);
            pd.setPluginType(types[j % types.length]);
            pd.setId(Calendar.getInstance().getTimeInMillis());
            pd.setPluginClass("org.xeneo.Plugin" + j);
                      
            Iterator<String> it = props.iterator();
            while (it.hasNext()) {
                pd.addProperty(it.next());
            }         
            
            list.add(pd);
        }

        return list;
    }

    public List<PluginConfiguration> createRandomPluginConfigurations(int i, String pluginURI, String ownerURI) {
        List<PluginConfiguration> pcs = new ArrayList<PluginConfiguration>();

        for (int j = 0; j < i; j++) {

            PluginConfiguration pc = new PluginConfiguration();
            
           

            Properties props = new Properties();
            props.setProperty("testProperty1" + j, "something1");
            props.setProperty("testProperty2" + j, "something2");
            props.setProperty("testProperty3" + j, "something3");
            props.setProperty("testProperty4" + j, "something4");

            // pc.setConfigurationProperties(props);
            
            pcs.add(pc);
        }

        return pcs;
    }
    
    public PluginConfiguration createRandomPluginConfiguration(String pluginURI, String ownerURI) {
        return this.createRandomPluginConfigurations(1, pluginURI, ownerURI).get(0);
    }
}
