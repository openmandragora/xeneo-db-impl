/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db.services;

import org.xeneo.core.services.PluginServices;
import org.xeneo.core.services.UserServices;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.scheduling.TaskScheduler;

/**
 *
 * @author Stefan Huber
 */
public class JdbcPluginServices extends JdbcDaoSupport implements PluginServices {

    private static String GET_PLUGIN_COUNT_BY_URI = "select count(*) from Plugin where PluginURI = ?";
    private static String GET_PLUGIN_BY_URI = "select * from Plugin where PluginURI = ?";
    private static String ADD_PLUGIN = "insert into Plugin (PluginURI,Title,Description,Classname) values (?,?,?,?)";
    private static String UPDATE_PLUGIN = "update Plugin set Title = ?, Description = ?, Classname = ? where PluginURI = ?";
    
    @Autowired
    private TaskScheduler scheduler;
    
    @Autowired
    private UserServices userServices;
    
    public void addPlugin(String pluginURI, String title, String description, String pluginClass) {        
        if (getJdbcTemplate().queryForInt(GET_PLUGIN_COUNT_BY_URI, pluginURI) > 0) {
            updatePlugin(title,description,pluginClass,pluginURI);
        } else {
            getJdbcTemplate().update(ADD_PLUGIN, title,description,pluginClass,pluginURI);
        }        
    }    
    
    public void updatePlugin(String pluginURI, String title, String description, String pluginClass) {
        getJdbcTemplate().update(UPDATE_PLUGIN, title,description,pluginClass,pluginURI);
    }

    public void configurePluginInstance(int id, Properties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addPluginInstance(String pluginURI, Properties properties, Map<String,Collection<String>> taskContexts) {
        
        String userURI = userServices.getCurrentUserURI();
        
        
        
        // TODO: ClassLoader cl = new Classloader();
        
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removePluginInstance(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
