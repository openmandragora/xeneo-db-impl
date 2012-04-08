/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.xeneo.db.JdbcCaseEngine;
import java.util.Date;
import org.apache.log4j.Logger;
import org.xeneo.core.task.Case;
// import org.apache
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stefan
 */
public class JdbcCaseTest {
    
    static final Logger logger = Logger.getLogger(JdbcCaseTest.class);
    
    private JdbcCaseEngine session;
    
    public JdbcCaseTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"test-config.xml"});
        session = (JdbcCaseEngine) context.getBean("session");
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
   
    @Test
    public void testCaseAttributes() {
        String case_1 = "http://stefanhuber.at/flower/test/case_1";
        
        Case myCase = session.getCaseByURI(case_1);        
        assertEquals(myCase.getCaseURI(),case_1);       
        assertTrue(!myCase.getTitle().isEmpty());
        assertTrue(!myCase.getDescription().isEmpty());
        
        Date d = myCase.getCreationDate();
        
        
        logger.info("CaseURI: " + myCase.getCaseURI() + ", Title: " + myCase.getTitle() + ", Description: " + myCase.getDescription() + ", Creation Date: " + d);
    }
    
    @Test 
    public void testNewCase() {
        Case myCase = session.createCase("http://stefanhuber.at/flower/test/ecommerce_case", "MyTitle");
        
        assertTrue(!myCase.getCaseURI().isEmpty());
        assertTrue(!myCase.getCaseTypeURI().isEmpty());
        assertTrue(myCase.getDescription().isEmpty());
        assertTrue(!myCase.getTitle().isEmpty());       
    }
    
    @Test
    public void testUpdateTitle() {
        String case_1 = "http://stefanhuber.at/flower/test/case_1";
        
        Case myCase = session.getCaseByURI(case_1);
        
        String old = myCase.getTitle();
        myCase.updateTitle("my new title");
        
        assertFalse(old.equals(myCase.getTitle()));                
    }
    
    @Test
    public void testUpdateDescription() {
        String case_1 = "http://stefanhuber.at/flower/test/case_1";
        
        Case myCase = session.getCaseByURI(case_1);
        
        String old = myCase.getDescription();
        myCase.updateDescription("my new description");
        
        assertFalse(old.equals(myCase.getDescription()));   
    }
}
