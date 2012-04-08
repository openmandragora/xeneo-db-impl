/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db.services;

import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xeneo.core.plugin.Plugin;
import org.xeneo.core.plugin.PluginFactory;

/**
 *
 * @author Stefan Huber
 */
public class JdbcPluginFactory extends PluginFactory {

    private static String PLUGIN_EXISTS = "Select count(*) from Plugin where PluginURI = ?";
    private static String ADD_PLUGIN = "insert into Plugin (PluginURI,Title,Description,UpdateInterval) values (?,?,?,?)";
    private static String UPDATE_PLUGIN = "update Plugin set Title = ?, Description = ? where PluginURI = ?";
    
    private JdbcTemplate jdbcTemplate;
    
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate.setDataSource(dataSource);
    }
    
    @Override
    public Plugin createPluginInstance(String pluginURI, Properties properties) {        
         throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public boolean pluginExists(String uri) {
        if (jdbcTemplate.queryForInt(PLUGIN_EXISTS, uri) > 0)
            return true;
        else
            return false;
    }

    @Override
    public void updatePlugin(Map<String, String> map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addPlugin(Map<String, String> map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    

}
