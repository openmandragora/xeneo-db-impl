
package org.xeneo.db;

import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xeneo.core.plugin.PluginConfiguration;
import org.xeneo.core.plugin.PluginInstanceManager;

/**
 *
 * @author Stefan Huber
 */
public class JdbcPluginInstanceManager implements PluginInstanceManager {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static String CREATE_PLUGIN_INSTANCE = "insert into PluginInstance (PluginURI, OwnerURI, Active, CronString) values (?,?,?,?)";
    private static String GET_PLUGIN_INSTANCE = "selent count(*) from PluginInstance where PluginURI = ? and OwnerURI = ?";
    private static String GET_PLUGIN_INSTANCE_ID = "select PluginInstanceID from PluginInstance where PluginURI = ? and OwnerURI = ?";
    
    private static String REMOVE_PLUGIN_INSTANCE = "delete from PluginInstance where PluginURI = ? and OwnerURI = ?";
    private static String REMOVE_PLUGIN_CONFIGURATION = "delete from PluginConfiguration where PluginConfigurationID = ?";    
    private static String ADD_PLUGIN_CONFIGURATION = "insert into PluginConfiguration (PluginURI,OwnerURI) values (?,?)";
    private static String UPDATE_PLUGIN_CONFIGURATION = "update PluginConfiguration set ";
    
    
    public void createPluginInstance(String pluginURI, String ownerURI) {
        if (!(jdbcTemplate.queryForInt(GET_PLUGIN_INSTANCE, pluginURI, ownerURI) > 0)) {
            jdbcTemplate.update(CREATE_PLUGIN_INSTANCE, pluginURI,ownerURI,true,"testCronString");
        }       
    }
    
    public void removePluginInstance(String pluginURI, String ownerURI) {
        jdbcTemplate.update(REMOVE_PLUGIN_INSTANCE, pluginURI, ownerURI);
    }
    
    public void addPluginConfiguration(String pluginURI, String ownerURI, PluginConfiguration pc) {
        createPluginInstance(pluginURI, ownerURI);
        
        Properties props = pc.getProperties();
        
        if(pc.getID() > 0) {
            removePluginConfiguration(pluginURI,ownerURI,pc.getID());
        } 
        
        jdbcTemplate.update(ADD_PLUGIN_CONFIGURATION, pluginURI, ownerURI);
                
    }
    
    public void removePluginConfiguration(String pluginURI, String ownerURI, int configuratinId) {
        jdbcTemplate.update(REMOVE_PLUGIN_CONFIGURATION,configuratinId);
    }    
    
    public List<PluginConfiguration> listPluginConfigurations(String pluginURI, String ownerURI) {
        
    }
    
}
