/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xeneo.core.XeneoException;
import org.xeneo.core.security.UserManager;
import org.xeneo.core.task.Case;
import org.xeneo.core.task.CaseManager;
import org.xeneo.core.task.CaseType;
import org.xeneo.core.task.Task;

/**
 *
 * @author Stefan Huber
 */
public class JdbcCaseManager implements CaseManager {    
        
    @Autowired private UserManager userManager;
    @Autowired private JdbcTemplate jdbcTemplate;
    
    private static String CASE_BY_URI_QUERY = "select count(*) from `Case` where CaseURI = ?";
    
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
        return new JdbcCaseType(jdbcTemplate,title,description);
    }

    public Case createCase(String caseTypeURI, String title, String description) {
        return new JdbcCase(jdbcTemplate,userManager,caseTypeURI,title,description);
    }

}
