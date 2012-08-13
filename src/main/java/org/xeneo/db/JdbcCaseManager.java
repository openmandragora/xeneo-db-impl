/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.xeneo.core.XeneoException;
import org.xeneo.core.security.UserManager;
import org.xeneo.core.task.Case;
import org.xeneo.core.task.CaseManager;
import org.xeneo.core.task.CaseType;
import org.xeneo.core.task.Task;
import org.xeneo.db.services.URIGenerator;

/**
 *
 * @author Stefan Huber
 */
public class JdbcCaseManager implements CaseManager {    
        
    @Autowired private UserManager userManager;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private URIGenerator uris;
    
    private static String CASE_BY_URI_QUERY = "select count(*) from `Case` where CaseURI = ?";
    
    // Case Types
    private static String GET_CASE_TYPES = "select * from `CaseType`";
    private static String UPDATE_CASE_TYPE = "update `CaseType` set Title = ?, Description = ? where CaseTypeURI = ?";
    private static String ADD_CASE_TYPE = "insert into `CaseType` (CaseTypeURI,Title,Description) values (?,?,?)";

    private static class CaseTypeRowMapper implements RowMapper {

        public CaseType mapRow(ResultSet rs, int i) throws SQLException {            
            String uri = rs.getString("CaseTypeURI");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            
            Assert.hasText(uri,"The CaseTypeURI is empty!");
            Assert.hasText(title,"The Title of the CaseType is empty!");  
            Assert.hasText(description,"The Description of the CaseType is empty!");
            
            return new CaseType(uri,title,description);            
        }        
    }     
    
    public boolean isExistingCase(String caseURI) {
        if (jdbcTemplate.queryForInt(CASE_BY_URI_QUERY, caseURI) > 0) {
            return true;
        }        
        return false;
    }
        
    public Case getCaseByURI(String caseURI) throws XeneoException {        
        if (isExistingCase(caseURI)) {            
            return new JdbcCase(jdbcTemplate,userManager,caseURI);
        }
              
        // TODO: think of a Exception Hierarchy for xeneo...
        throw new XeneoException("There is no Case for the URI: " + caseURI);        
    }

    public Task createTask(String title, String description) {
        return new JdbcTask(jdbcTemplate,title,description);
    }
    
    public CaseType createCaseType(String title, String description) {
        String uri = uris.generateURI("casetype");
        
        jdbcTemplate.update(ADD_CASE_TYPE, uri, title, description);
        
        return new CaseType(uri,title,description);        
    }
    
    public CaseType updateCaseType(CaseType caseType) {        
        jdbcTemplate.update(UPDATE_CASE_TYPE,caseType.getTitle(),caseType.getDescription(),caseType.getCaseTypeURI());
        return caseType;
    }

    public Case createCase(String caseTypeURI, String title, String description) {
        return new JdbcCase(jdbcTemplate,userManager,caseTypeURI,title,description);
    }

    public Collection<CaseType> getCaseTypes() {
        return jdbcTemplate.query(GET_CASE_TYPES, new CaseTypeRowMapper());
    }

}
