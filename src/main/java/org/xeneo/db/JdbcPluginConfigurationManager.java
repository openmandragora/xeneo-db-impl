/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import java.util.List;
import org.xeneo.core.plugin.PluginConfiguration;
import org.xeneo.core.plugin.PluginConfigurationManager;
import org.xeneo.core.plugin.PluginType;

/**
 *
 * @author Stefan Huber
 */
public class JdbcPluginConfigurationManager implements PluginConfigurationManager {

    private final String GET_PLUGININSTANCES_WITH_CONFIGURATION = "Select PI.PluginInstanceID, PI.PluginURI, PI.PluginConfigurationID, PP.Name, PP.Type, PV.Value from PluginInstanceDataView PV LEFT JOIN PluginProperty PP ON PP.PluginPropertyID = PV.PluginPropertyID LEFT JOIN PluginInstance PI ON PI.PluginInstanceID = PV.PluginInstanceID";
    
    public List<PluginConfiguration> listAvailablePlugins(String userURI, PluginType[] pluginTypes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void configurePlugin(PluginConfiguration pc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
