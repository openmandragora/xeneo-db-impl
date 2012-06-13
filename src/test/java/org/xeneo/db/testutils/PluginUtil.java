package org.xeneo.db.testutils;

import java.util.*;
import org.xeneo.core.plugin.PluginConfiguration;
import org.xeneo.core.plugin.PluginProperty;
import org.xeneo.core.plugin.PluginPropertyType;
import org.xeneo.core.plugin.PluginType;

/**
 *
 * @author Stefan Huber
 */
public class PluginUtil {

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
        }
        
        return list;
    }
}
