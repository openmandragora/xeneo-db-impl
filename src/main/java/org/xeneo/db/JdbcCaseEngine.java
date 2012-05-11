/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.xeneo.core.services.UserServices;
import org.xeneo.core.task.Case;
import org.xeneo.core.task.CaseEngine;
import org.xeneo.core.task.CaseType;
import org.xeneo.core.task.Task;

/**
 *
 * @author Stefan Huber
 */
public class JdbcCaseEngine extends JdbcDaoSupport implements CaseEngine {    
    
    private UserServices userServices;
    
    public void setUserServices(UserServices us) {
        this.userServices = us;
    }    
    
    private static String CASE_BY_URI_QUERY = "select count(*) from `Case` where CaseURI = ?";
        
    public Case getCaseByURI(String URI) {        
        int count = getJdbcTemplate().queryForInt(CASE_BY_URI_QUERY, URI);
        
        if (count == 1) {
            return new JdbcCase(this.getDataSource(),URI);
        }
              
        // TODO: think of a Exception Hierarchy for xeneo...
        throw new UnsupportedOperationException("Not supported yet.");        
    }
    
    public Case createCase(String caseTypeURI, String title) {
        return new JdbcCase(this.getDataSource(),caseTypeURI,title);
    }

    public Task createTask(String title) {
        return new JdbcTask(this.getDataSource(),title,"");
    }

    public Task createTask(String title, String description) {
        return new JdbcTask(this.getDataSource(),title,description);
    }
    
    public CaseType createCaseType(String title) {
        return new JdbcCaseType(this.getDataSource(),title,"");
    }
    
    public CaseType createCaseType(String title, String description) {
        return new JdbcCaseType(this.getDataSource(),title,description);
    }

    public Case createCase(String caseTypeURI, String title, String description) {
        return new JdbcCase(this.getDataSource(),caseTypeURI,title,description);
    }

}
