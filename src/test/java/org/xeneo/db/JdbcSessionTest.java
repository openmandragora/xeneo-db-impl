/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.db.JdbcSession;
import org.xeneo.core.Activity;
import java.util.Collection;
import java.util.ArrayList;
import org.xeneo.core.Task;
import java.util.Date;
import org.xeneo.core.CaseType;
import org.xeneo.core.Case;
import java.util.Calendar;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stefan
 */
public class JdbcSessionTest {
    
    JdbcSession workSession;
    
    public JdbcSessionTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"test-config.xml"});
        workSession = context.getBean("session", JdbcSession.class);
    }
    
    @Before
    public void setUp() {

    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testCreationStory() throws InterruptedException {
        
        CaseType ct = workSession.createCaseType("Ecommerce Case", "The Ecommcer Case represents an Ecommerce related Project...");
        
        Case cs = workSession.createCase(ct.getCaseTypeURI(), "Franzis Webshop", "Franzis Webshop needs to be built");
        
        Date date = cs.getCreationDate();
        
        Thread.sleep(1000);
        
        assertTrue(date.before(Calendar.getInstance().getTime()));
        
        Task t1 = workSession.createTask("My First Task", "What a shitty Task...");
        Task t2 = workSession.createTask("My Second Task","Oh....");
        
        ArrayList<String> taskList = new ArrayList<String>();
        taskList.add(t1.getTaskURI());
        taskList.add(t2.getTaskURI());
        
        assertFalse(t1.equals(t2));
        
        Activity act = workSession.createActivity("Somebody did something", "http://stefanhuber.at/user/stefan", date, cs.getCaseURI(), (Collection) taskList);
   
        Thread.sleep(1000);
        
        assertTrue(act.getCreationDate().before(Calendar.getInstance().getTime()));
        
    }
    
    @Test
    public void testGetCaseByURI() {
        
        String case_1 = "http://stefanhuber.at/flower/test/case_1";
        
        Case myCase = workSession.getCaseByURI(case_1);
        
        assertEquals(myCase.getCaseURI(),case_1);
    }
}
