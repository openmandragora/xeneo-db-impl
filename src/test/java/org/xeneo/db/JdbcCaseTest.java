/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import java.util.Calendar;
import org.xeneo.db.JdbcCaseEngine;
import java.util.Date;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeneo.core.task.Case;
// import org.apache
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xeneo.core.task.CaseType;

/**
 *
 * @author Stefan Huber
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcCaseTest {
    
    static final Logger logger = LoggerFactory.getLogger(JdbcCaseTest.class);
    
    @Autowired
    private JdbcCaseEngine engine;

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
    public void testCaseStory() {
        
        String title = "case title"; String desc = "case desc";
        
        CaseType ct = engine.createCaseType("my test type", "description");
        
        Case c1 = engine.createCase(ct.getCaseTypeURI(), title, desc);
        
        assertEquals(ct.getCaseTypeURI(),c1.getCaseTypeURI());
        assertEquals(c1.getTitle(),title);
        assertEquals(c1.getDescription(),desc);
        
        assertTrue(c1.getCreationDate().before(Calendar.getInstance().getTime()));      
        
        c1.updateTitle("new title");
        assertFalse(title.equalsIgnoreCase(c1.getTitle()));
        
        c1.updateDescription("new desc");
        assertFalse(desc.equals(c1.getDescription()));
        
        
    }
    
   
    @Test
    public void testCaseAttributes() {
        String case_1 = "http://stefanhuber.at/flower/test/case_1";
        
        Case myCase = engine.getCaseByURI(case_1);        
        assertEquals(myCase.getCaseURI(),case_1);       
        assertTrue(!myCase.getTitle().isEmpty());
        assertTrue(!myCase.getDescription().isEmpty());
        
        Date d = myCase.getCreationDate();
        
        
        logger.info("CaseURI: " + myCase.getCaseURI() + ", Title: " + myCase.getTitle() + ", Description: " + myCase.getDescription() + ", Creation Date: " + d);
    }
    
    @Test 
    public void testNewCase() {
        Case myCase = engine.createCase("http://stefanhuber.at/flower/test/ecommerce_case", "MyTitle");
        
        assertTrue(!myCase.getCaseURI().isEmpty());
        assertTrue(!myCase.getCaseTypeURI().isEmpty());
        assertTrue(myCase.getDescription().isEmpty());
        assertTrue(!myCase.getTitle().isEmpty());       
    }
    
    @Test
    public void testUpdateTitle() {
        String case_1 = "http://stefanhuber.at/flower/test/case_1";
        
        Case myCase = engine.getCaseByURI(case_1);
        
        String old = myCase.getTitle();
        myCase.updateTitle("my new title" + Calendar.getInstance().getTime());
        
        assertFalse(old.equals(myCase.getTitle()));                
    }
    
    @Test
    public void testUpdateDescription() {
        String case_1 = "http://stefanhuber.at/flower/test/case_1";
        
        Case myCase = engine.getCaseByURI(case_1);
        
        String old = myCase.getDescription();
        myCase.updateDescription("my new description" + Calendar.getInstance().getTime());
        
        assertFalse(old.equals(myCase.getDescription()));   
    }
}
