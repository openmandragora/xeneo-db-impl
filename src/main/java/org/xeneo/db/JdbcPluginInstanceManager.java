package org.xeneo.db;

import java.util.*;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xeneo.core.plugin.PluginConfiguration;
import org.xeneo.core.plugin.PluginException;
import org.xeneo.core.plugin.PluginInstanceManager;

/**
 *
 * @author Stefan Huber
 */
public class JdbcPluginInstanceManager implements PluginInstanceManager {

    Logger logger = LoggerFactory.getLogger(JdbcPluginInstanceManager.class);
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jt) {
        this.jdbcTemplate = jt;
    }
    private static String CREATE_PLUGIN_INSTANCE = "insert into PluginInstance (PluginURI, OwnerURI, Active) values (?,?,?)";
    private static String GET_PLUGIN_INSTANCE = "select count(*) from PluginInstance where PluginURI = ? and OwnerURI = ?";
    private static String GET_PLUGIN_INSTANCE_ID = "select PluginInstanceID from PluginInstance where PluginURI = ? and OwnerURI = ?";
    private static String REMOVE_PLUGIN_INSTANCE = "delete from PluginInstance where PluginURI = ? and OwnerURI = ?";
    private static String REMOVE_PLUGIN_CONFIGURATION = "delete from PluginConfiguration where PluginConfigurationID = ?";
    private static String ADD_PLUGIN_CONFIGURATION = "insert into PluginConfiguration (PluginURI,OwnerURI) values (?,?)";
    private static String GET_PLUGIN_CONFIGURATION_ID = "select PluginConfigurationID from PluginConfiguration where PluginURI=? and OwnerURI=? order by PluginConfigurationID desc limit 1";
    private static String GET_PLUGIN_CONFIGURATION = "select c.PluginURI, c.OwnerURI, c.PluginConfigurationID, p.Name, p.Value from PluginConfiguration c inner join PluginConfigurationProperty p on c.PluginConfigurationID = p.PluginConfigurationID inner join PluginInstance i on c.PluginURI = i.PluginURI and c.OwnerURI = i.OwnerURI  where c.PluginURI=? and c.OwnerURI=?";
    private static String GET_PLUGIN_INSTANCE_PROPERTIES = "select p.Name, p.Value from PluginInstance i inner join PluginInstanceProperty p on i.PluginURI = p.PluginURI and i.OwnerURI = p.OwnerURI where i.PluginURI=? and i.OwnerURI=?";
    private static String ADD_PLUGIN_CONFIGURATION_PROPERTY = "insert into PluginConfigurationProperty (Value,Name,PluginConfigurationID) values (?,?,?)";
    private static String UPDATE_PLUGIN_CONFIGURATION_PROPERTY = "update PluginConfigurationProperty set Value=? where Name=? and PluginConfigurationID=?";
    private static String GET_PLUGIN_CONFIGURATION_PROPERTY = "select count(*) from PluginConfigurationProperty where Name=? and PluginConfigurationID = ? ";
    private static String GET_PLUGIN_INSTANCE_PROPERTY = "select count(*) from PluginInstanceProperty where PluginURI = ? and OwnerURI = ? and Name = ?";
    private static String ADD_PLUGIN_INSTANCE_PROPERTY = "insert into PluginInstanceProperty (Value,Name,OwnerURI,PluginURI) values (?,?,?,?)";
    private static String UPDATE_PLUGIN_INSTANCE_PROPERTY = "update PluginInstanceProperty set Value=? where Name=? and PluginURI=? and OwnerURI=?";

    public void createPluginInstance(String pluginURI, String ownerURI) {
        logger.info("Try to create new Plugin Instance if not already exists.");
        if (!isExistingPluginInstance(pluginURI, ownerURI)) {
            jdbcTemplate.update(CREATE_PLUGIN_INSTANCE, pluginURI, ownerURI, true);
            logger.info("Plugin Instance created");
        }
    }

    public boolean isExistingPluginInstance(String pluginURI, String ownerURI) {
        if (jdbcTemplate.queryForInt(GET_PLUGIN_INSTANCE, pluginURI, ownerURI) > 0) {
            return true;
        }
        return false;
    }

    public void removePluginInstance(String pluginURI, String ownerURI) {
        jdbcTemplate.update(REMOVE_PLUGIN_INSTANCE, pluginURI, ownerURI);
        logger.info("Removed Plugin Instance");
    }

    public void addPluginConfiguration(PluginConfiguration pc) {
        String pluginURI = pc.getPluginURI();
        String ownerURI = pc.getOwnerURI();

        // TODO: throw exception if parameters are missing
        // TODO: either try to create, or throw exception if not exists...
        // createPluginInstance(pluginURI, ownerURI);

        if (!isExistingPluginInstance(pc.getPluginURI(), pc.getOwnerURI())) {
            logger.error("Plugin Instance missing!");
            //TODO: throw exception
        }

        int pcId = pc.getID();
        Properties props = pc.getConfigurationProperties();

        // create a new Plugin Configuration if no one exists already, indicated by a negative pc.ID
        if (pcId < 0) {
            jdbcTemplate.update(ADD_PLUGIN_CONFIGURATION, pluginURI, ownerURI);
            pcId = jdbcTemplate.queryForInt(GET_PLUGIN_CONFIGURATION_ID, pluginURI, ownerURI);
        }

        Iterator<Object> keys = props.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();

            // if there is a configuratin property with key and configuration ID 
            if (jdbcTemplate.queryForInt(GET_PLUGIN_CONFIGURATION_PROPERTY, key, pcId) > 0) {
                // then update
                jdbcTemplate.update(UPDATE_PLUGIN_CONFIGURATION_PROPERTY, props.get(key), key, pcId);
            } else {
                // or insert
                jdbcTemplate.update(ADD_PLUGIN_CONFIGURATION_PROPERTY, props.get(key), key, pcId);
            }
        }
    }

    public void removePluginConfiguration(int configuratinId) {
        jdbcTemplate.update(REMOVE_PLUGIN_CONFIGURATION, configuratinId);
    }

    public List<PluginConfiguration> listPluginConfigurations(String pluginURI, String ownerURI) {
        // TODO: Plugins with no PluginConfigurationProperty set are not recognized by this query...
        // TODO: plugin instance properties need to be recognized
        List<Map<String, Object>> pc = jdbcTemplate.queryForList(GET_PLUGIN_CONFIGURATION, pluginURI, ownerURI);
        List<Map<String, Object>> pic = jdbcTemplate.queryForList(GET_PLUGIN_INSTANCE_PROPERTIES, pluginURI, ownerURI);

        logger.info(pc.size() + " Plugin Configuratin Properties found for Plugins with URI: " + pluginURI + " and defined for Owner: " + ownerURI);
        logger.info(pic.size() + " Plugin Instance Properties found for Plugins with URI: " + pluginURI + " and defined for Owner: " + ownerURI);

        Map<Integer, Properties> pcp = new HashMap<Integer, Properties>(); // Plugin Configuration Properties 
        
        List<PluginConfiguration> pcs = new ArrayList<PluginConfiguration>();

        // Plugin Configuration Properties
        Iterator<Map<String, Object>> it = pc.iterator();
        while (it.hasNext()) {
            Map<String, Object> map = it.next();
            if (map.containsKey("PluginConfigurationID") && map.containsKey("Name") && map.containsKey("Value")) {
                int id = (Integer) map.get("PluginConfigurationID");
                if (pcp.containsKey(id)) {
                    Properties p = pcp.get(id);
                    p.put(map.get("Name"), map.get("Value"));
                    pcp.put(id, p);
                } else {
                    Properties p = new Properties();
                    p.put(map.get("Name"), map.get("Value"));
                    pcp.put(id, p);
                }
            } else {
                // TODO: maybe throw an exception
                logger.info("The result map doesn't contain the respective entries.");
            }
        }

        // Plugin Instance Properties
        Iterator<Map<String, Object>> it2 = pic.iterator();
        Properties props = new Properties(); // Plugin Instance Properties
        while (it2.hasNext()) {
            Map<String, Object> map = it2.next();
            props.put(map.get("Name"), map.get("Value"));
        }

        if (pcp.size() < 1 && !props.isEmpty()) {
            PluginConfiguration ppp = new PluginConfiguration();
            ppp.setInstanceProperties(props);
            ppp.setPluginURI(pluginURI);
            ppp.setOwnerURI(ownerURI);
            pcs.add(ppp);
        } else {
            Iterator<Entry<Integer, Properties>> it3 = pcp.entrySet().iterator();
            while (it3.hasNext()) {
                Entry<Integer, Properties> e = it3.next();
                logger.info("add PluginConfiguration with ID: " + e.getKey());
                PluginConfiguration config = new PluginConfiguration(e.getKey());
                config.setConfigurationProperties(e.getValue());
                config.setInstanceProperties(props);
                config.setOwnerURI(ownerURI);
                config.setPluginURI(pluginURI);
                pcs.add(config);
            }
        }
        
        return pcs;
    }

    public void addPluginInstanceProperties(PluginConfiguration pc) {

        if (!isExistingPluginInstance(pc.getPluginURI(), pc.getOwnerURI())) {
            logger.error("Plugin Instance missing!");
            //TODO: throw exception
        }

        Iterator<Object> keys = pc.getInstanceProperties().keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            
            // if there is a configuratin property with key and configuration ID 
            if (jdbcTemplate.queryForInt(GET_PLUGIN_INSTANCE_PROPERTY, pc.getPluginURI(), pc.getOwnerURI(), key) > 0) {
                // then update
                jdbcTemplate.update(UPDATE_PLUGIN_INSTANCE_PROPERTY, pc.getInstanceProperties().getProperty(key), key, pc.getOwnerURI(), pc.getPluginURI());
                logger.info("Update Plugin Instance Property " + key + " value: " + pc.getInstanceProperties().getProperty(key));
            } else {
                // or insert
                jdbcTemplate.update(ADD_PLUGIN_INSTANCE_PROPERTY, pc.getInstanceProperties().getProperty(key), key, pc.getOwnerURI(), pc.getPluginURI());
                logger.info("Insert Plugin Instance Property " + key + " value: " + pc.getInstanceProperties().getProperty(key));
            }
        }
    }
}
