/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.db.JdbcCaseEngine;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import org.xeneo.core.task.Case;
import org.xeneo.core.task.CaseType;
import org.xeneo.core.task.Task;

import java.util.Calendar;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Stefan Huber
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcCaseEngineTest {
    
    @Autowired
    JdbcCaseEngine engine;
        
    @Before
    public void setUp() {

    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testCreationStory() throws InterruptedException {
        
        CaseType ct = engine.createCaseType("Ecommerce Case", "The Ecommcer Case represents an Ecommerce related Project...");
        
        Case cs = engine.createCase(ct.getCaseTypeURI(), "Franzis Webshop", "Franzis Webshop needs to be built");
        
        Date date = cs.getCreationDate();
        
        Thread.sleep(1000);
        
        assertTrue(date.before(Calendar.getInstance().getTime()));
        
        Task t1 = engine.createTask("My First Task", "What a shitty Task...");
        Task t2 = engine.createTask("My Second Task","Oh....");
        
        ArrayList<String> taskList = new ArrayList<String>();
        taskList.add(t1.getTaskURI());
        taskList.add(t2.getTaskURI());
        
        assertFalse(t1.equals(t2));
        
   
        Thread.sleep(1000);
        
        
    }
    
    @Test
    public void testGetCaseByURI() {
        
        String case_1 = "http://stefanhuber.at/flower/test/case_1";
        
        Case myCase = engine.getCaseByURI(case_1);
        
        assertEquals(myCase.getCaseURI(),case_1);
    }
}
