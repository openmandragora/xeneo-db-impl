/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.core.Session;
import org.xeneo.core.Case;
import org.xeneo.core.Activity;
import org.xeneo.core.CaseType;
import org.xeneo.core.Task;
import org.xeneo.core.services.UserServices;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Stefan Huber
 */
public class JdbcSession extends JdbcDaoSupport implements Session {    
    
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
              
        // TODO: think of a Exception Hierarchy for flower...
        throw new UnsupportedOperationException("Not supported yet.");        
    }
    
    public Case createCase(String caseTypeURI, String title) {
        return new JdbcCase(this.getDataSource(),caseTypeURI,title);
    }

    public Activity createActivity(String title, String userURI) {
        return this.createActivity(title, userURI, Calendar.getInstance().getTime());
    }

    public Activity createActivity(String title, Date creationDate) {
        return this.createActivity(title, userServices.getCurrentUserURI(),creationDate);
    }

    public Activity createActivity(String title, String userURI, Date creationDate) {
        return new JdbcActivity(this.getDataSource(),title,userURI,creationDate);
    }

    public Activity createActivity(String title, String userURI, Date creationDate, String caseURI, Collection<String> taskURIs) {
        Activity act = this.createActivity(title, userURI, creationDate);        
        act.addTaskContexts(caseURI, taskURIs);        
        return act;
    }

    public Activity createActivity(String title, String userURI, Date creationDate, Map<String, Collection<String>> caseTaskURIs) {
        Activity act = this.createActivity(title, userURI, creationDate);        
        act.addTaskContexts(caseTaskURIs);        
        return act;
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

    public Activity createActivity(String title, String caseURI, Collection<String> taskURIs) {       
        Activity act = this.createActivity(title, userServices.getCurrentUserURI(), Calendar.getInstance().getTime());        
        act.addTaskContexts(caseURI, taskURIs);        
        return act;
    }

    public Activity createActivity(String title, Map<String, Collection<String>> caseTaskURIs) {       
        Activity act = this.createActivity(title, userServices.getCurrentUserURI(), Calendar.getInstance().getTime());        
        act.addTaskContexts(caseTaskURIs);        
        return act;
    }
}
