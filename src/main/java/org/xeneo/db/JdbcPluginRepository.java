package org.xeneo.db;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.xeneo.core.plugin.PluginConfiguration;
import org.xeneo.core.plugin.PluginProperty;
import org.xeneo.core.plugin.PluginPropertyType;
import org.xeneo.core.plugin.PluginRepository;
import org.xeneo.core.plugin.PluginType;

/**
 *
 * @author Stefan Huber
 */
public class JdbcPluginRepository implements PluginRepository {

    private static Logger logger = LoggerFactory.getLogger(JdbcPluginRepository.class);
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertPluginInstance;

    public void setJdbcTemplate(JdbcTemplate jt) {
        this.jdbcTemplate = jt;

        // create plugin instance insert
        insertPluginInstance = new SimpleJdbcInsert(jdbcTemplate);
        insertPluginInstance.withTableName("PluginInstance");
        insertPluginInstance.usingGeneratedKeyColumns("PluginInstanceID");
    }
    // Plugin Table
    private static final String PLUGIN_EXISTS = "select count(*) from Plugin where PluginURI = ?";
    private static final String PLUGIN_UPDATE = "update Plugin set PluginType=?, Title=?, Description=?, Classname=?, Active='1' where PluginURI = ?";
    private static final String PLUGIN_ADD = "insert into Plugin (PluginURI,PluginType,Title,Description,Classname,Active) values (?,?,?,?,?,'1')";
    private static final String PLUGIN_DEACTIVATE = "update Plugin set Active='0' where PluginURI = ?";
    // Plugin Property Table
    private static final String PLUGIN_PROPERTIES_ADD = "insert into PluginProperty (PluginURI,Name,Type) values %s";
    private static final String PLUGIN_PROPERTIES_DELETE = "delete from pluginproperty where PluginURI = ?";
    // CRAZY PLUGIN LISTING QUERY    
    
    private static final String PLUGIN_LIST = "select p.PluginURI, p.PluginType, p.Title, p.Description, p.Classname, pp.Name, pp.Type, pi.PluginInstanceID, pip.Value from Plugin p inner join PluginProperty pp on p.pluginURI  = pp.pluginURI left join (select * from PluginInstance where OwnerURI = ?) pi on p.pluginURI = pi.pluginURI left join PluginInstanceProperty pip on (pip.Name = pp.Name and pip.PluginInstanceID = pi.PluginInstanceID) where p.active = '1'";
    
    // Plugin Instance Table
    private static final String PLUGIN_INSTANCE_EXISTS = "select count(*) from PluginInstance where PluginURI = ? and OwnerURI = ?";
    private static final String PLUGIN_INSTANCE_ADD = "insert into PluginInstance (PluginURI,OwnerURI,Active) values (?,?,1)";
    private static final String PLUGIN_INSTANCE_UPDATE = "update PluginInstance set PluginURI=?,OwnerURI=?";
    // Plugin Instance Property Table
    private static final String PLUGIN_INSTANCE_PROPERTY_ADD = "insert into PluginInstanceProperty (PluginInstanceID,Name,Value) values %s";
    private static final String PLUGIN_INSTANCE_PROPERTY_DELETE = "delete from PluginInstanceProperty where PluginInstanceID = ?";

    public void addPlugin(PluginConfiguration configuration) {
        logger.info("Try to add plugin with URI: " + configuration.getPluginURI() + " and name: " + configuration.getTitle());

        boolean preDelete = false;

        if (jdbcTemplate.queryForInt(PLUGIN_EXISTS, configuration.getPluginURI()) > 0) {
            logger.info("update Plugin: " + configuration.getTitle());
            jdbcTemplate.update(PLUGIN_UPDATE, configuration.getPluginType().typeURI(), configuration.getTitle(), configuration.getDescription(), configuration.getPluginClass(), configuration.getPluginURI());
            preDelete = true;
        } else {
            logger.info("add Plugin: " + configuration.getTitle());
            jdbcTemplate.update(PLUGIN_ADD, configuration.getPluginURI(), configuration.getPluginType().typeURI(), configuration.getTitle(), configuration.getDescription(), configuration.getPluginClass());
        }

        addPluginProperties(configuration.getPluginURI(), configuration.getProperties(), preDelete);
    }

    public void addPluginProperties(String pluginURI, PluginProperty[] props, boolean preDelete) {
        if (preDelete) {
            jdbcTemplate.update(PLUGIN_PROPERTIES_DELETE, pluginURI);
        }

        String values = "";
        for (PluginProperty p : props) {
            values += ",('" + pluginURI + "','" + p.getName() + "','" + p.getType().name() + "')";
        }

        // substring 1 to strip the first ","
        String query = String.format(PLUGIN_PROPERTIES_ADD, values.substring(1));
        logger.info("INSERT INTO PLUGIN PROPERTIES QUERY: " + query);

        jdbcTemplate.update(query);
    }

    public void deactivatePlugin(String pluginURI) {
        logger.info("Try to deactivate Plugin with URI: " + pluginURI);
        jdbcTemplate.update(PLUGIN_DEACTIVATE, pluginURI);
    }

    public void configurePlugin(PluginConfiguration pc) {
        if (pc.getId() < 0) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("PluginURI", pc.getPluginURI());
            parameters.put("OwnerURI", pc.getOwnerURI());
            parameters.put("Active", true);
            Number n = insertPluginInstance.executeAndReturnKey(parameters);

            addPluginInstanceProperties(n.intValue(), pc.getProperties(), false);
        } else {
            // no update of teh Plugin Instance Table is done, because only PluginURI and OwnerURI, which remain the same throughout the lifetime of an instance
            addPluginInstanceProperties(pc.getId(), pc.getProperties(), true);
        }
    }

    public void addPluginInstanceProperties(int pluginInstanceId, PluginProperty[] props, boolean preDelete) {

        if (preDelete) {
            jdbcTemplate.update(PLUGIN_INSTANCE_PROPERTY_DELETE, pluginInstanceId);
        }

        String values = "";
        for (PluginProperty p : props) {
            values += ",('" + pluginInstanceId + "','" + p.getName() + "','" + p.getValue() + "')";
        }

        String query = String.format(PLUGIN_INSTANCE_PROPERTY_ADD, values.substring(1));
        logger.info("INSERT INTO PLUGIN PROPERTIES QUERY: " + query);

        jdbcTemplate.update(query);
    }

    public List<PluginConfiguration> listAvailablePlugins(String userURI, PluginType[] pts) {

        SqlRowSet set = jdbcTemplate.queryForRowSet(PLUGIN_LIST, userURI);
        Map<String, PluginConfiguration> output = new HashMap<String, PluginConfiguration>();

        while (set.next()) {

            String pluginURI = set.getString("PluginURI");
            String pluginType = set.getString("PluginType");
            String className = set.getString("Classname");
            String title = set.getString("Title");
            String desc = set.getString("Description");
            String paramName = set.getString("Name");
            String paramType = set.getString("Type");
            String paramValue = set.getString("Value");
            int pluginInstanceId = set.getInt("PluginInstanceID");

            if (!output.containsKey(pluginURI)) {

                PluginConfiguration pc = new PluginConfiguration();
                pc.setPluginURI(pluginURI);
                pc.setPluginType(PluginType.ACTIVITY_PLUGIN);
                pc.setPluginClass(className);
                pc.setOwnerURI(userURI);
                pc.setTitle(title);
                pc.setDescription(desc);

                if (pluginInstanceId > 0) {
                    pc.setId(pluginInstanceId);
                }

                // TODO: maybe find a better key for the map
                output.put(pluginURI, pc);
            }

            PluginProperty pp = new PluginProperty();
            pp.setName(paramName);
            pp.setType(PluginPropertyType.valueOf(paramType));

            if (paramValue != null && !paramValue.isEmpty()) {
                pp.setValue(paramValue);
            }

            PluginConfiguration pc = output.get(pluginURI);
            pc.addProperty(pp);

        }


        return new ArrayList<PluginConfiguration>(output.values());
    }
}
