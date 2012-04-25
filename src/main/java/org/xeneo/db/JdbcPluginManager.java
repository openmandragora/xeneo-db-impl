/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.xeneo.core.plugin.PluginDescriptor;
import org.xeneo.core.plugin.PluginManager;

/**
 *
 * @author Stefan Huber
 */
public class JdbcPluginManager implements PluginManager {

    private static Logger logger = LoggerFactory.getLogger(JdbcPluginManager.class);
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jt) {
        this.jdbcTemplate = jt;
    }
    private static final String PLUGIN_EXISTS = "select count(*) from Plugin where PluginURI = ?";
    private static final String PLUGIN_SELECT = "select * from Plugin where PluginURI = %s";
    private static final String PLUGIN_ADD = "insert into Plugin (PluginURI,PluginType,Title,Description,Classname,BundleID,Active) values (?,?,?,?,?,?,?)";
    private static final String PLUGIN_UPDATE = "update Plugin set PluginType=?, Title=?, Description=?, Classname=?,BundleID=?, Active=1 where PluginURI = ?";
    private static final String PLUGIN_DEACTIVATE = "update Plugin set Active=0 where PluginURI = ?";
    private static final String PLUGIN_LIST = "select PluginURI,PluginType,Title,Description,Classname,BundleID from Plugin where Active=1 and PluginType in(%s)";

    public void addPlugin(PluginDescriptor descriptor) {
        int i = jdbcTemplate.queryForInt(PLUGIN_EXISTS, descriptor.getPluginURI());
        if (i > 0) {
            logger.info("update Plugin: " + descriptor.getTitle());
            jdbcTemplate.update(PLUGIN_UPDATE, descriptor.getPluginType(), descriptor.getTitle(), descriptor.getDescription(), descriptor.getPluginClass(), descriptor.getID(), descriptor.getPluginURI());
        } else {
            logger.info("add Plugin: " + descriptor.getTitle());
            jdbcTemplate.update(PLUGIN_ADD, descriptor.getPluginURI(), descriptor.getPluginType(), descriptor.getTitle(), descriptor.getDescription(), descriptor.getPluginClass(), descriptor.getID(), true);
        }
    }

    public void deactivatePlugin(String pluginURI) {
        logger.info("Try to deactivate Plugin with URI: " + pluginURI);
        jdbcTemplate.update(PLUGIN_DEACTIVATE, pluginURI);
    }

    public List<PluginDescriptor> listAvailablePlugins(String[] types) {
        String t = "";
        for (int i = 0; i < types.length; i++) {
            t += i > 0 ? ",'" + types[i] + "'" : "'" + types[i] + "'";
        }

        return queryForPluginDescriptors(String.format(PLUGIN_LIST, t));
    }

    private List<PluginDescriptor> queryForPluginDescriptors(String query) {
        return jdbcTemplate.query(query, new RowMapper<PluginDescriptor>() {

            public PluginDescriptor mapRow(ResultSet rs, int rowNum) throws SQLException {
                PluginDescriptor pd = new PluginDescriptor();
                pd.setPluginURI(rs.getString("PluginURI"));
                pd.setPluginType(rs.getString("PluginType"));
                pd.setTitle(rs.getString("Title"));
                pd.setID(rs.getLong("BundleID"));
                pd.setDescription(rs.getString("Description"));
                pd.setPluginClass(rs.getString("Classname"));

                return pd;
            }
        });
    }

    public PluginDescriptor getPluginDescriptor(String pluginURI) {
        List<PluginDescriptor> pds = this.queryForPluginDescriptors(String.format(PLUGIN_SELECT, "'" + pluginURI + "'"));
        
        if (pds.size() > 0) {
            return pds.get(0);
        } else {
            // TODO: trhow exception
            return null;
        }
    }
}
