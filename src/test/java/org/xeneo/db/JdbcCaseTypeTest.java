/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeneo.core.task.CaseType;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xeneo.core.task.CaseManager;

/**
 *
 * @author Stefan Huber
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-config.xml")
public class JdbcCaseTypeTest {
    
    @Autowired
    private JdbcCaseManager engine;
    
    static final Logger logger = LoggerFactory.getLogger(JdbcTaskTest.class);
    
    public JdbcCaseTypeTest() {        
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
    public void testCreateCaseType() {
        
        String title = "My Case Type"; String description = "My Case Type Description";
        
        CaseType ct = engine.createCaseType(title, description);
        
        logger.info("CaseType created with URI: " + ct.getCaseTypeURI() + ", Title: " + ct.getTitle() + " and Description: " + ct.getDescription());
        
        assertTrue(!ct.getCaseTypeURI().isEmpty() && !ct.getTitle().isEmpty() && !ct.getDescription().isEmpty());
    }
    
    @Test
    public void testUpdateFields() {
        
        String title = "My Second Case Type"; String description = "My Second Case Type Description";
        
        CaseType ct = engine.createCaseType(title, description);
        
        logger.info("CaseType created with URI: " + ct.getCaseTypeURI() + ", Title: " + ct.getTitle() + " and Description: " + ct.getDescription());
        
        assertTrue(!ct.getCaseTypeURI().isEmpty() && !ct.getTitle().isEmpty() && !ct.getDescription().isEmpty());
        
        ct.setTitle("new CaseType Title");
        ct.setDescription("new CaseType description");
        
        CaseType a = engine.updateCaseType(ct);        
                
        assertFalse(title.equalsIgnoreCase(ct.getTitle()) || description.equalsIgnoreCase(ct.getDescription()));
        
    }
    
    @Test
    public void testGetCaseTypes() {
        
        CaseType a = engine.createCaseType("title1","desc1");
        CaseType b = engine.createCaseType("title2","desc2");
        
        int i = 0;
        for(CaseType x : engine.getCaseTypes()) {
            
            logger.info(x.getCaseTypeURI() + " - " + x.getTitle() + " - " + x.getDescription());
            
            if (a.getCaseTypeURI().equalsIgnoreCase(x.getCaseTypeURI()) && a.getTitle().equalsIgnoreCase(x.getTitle()) && a.getDescription().equalsIgnoreCase(x.getDescription())) {
                i++;
            }
            if (b.getCaseTypeURI().equalsIgnoreCase(x.getCaseTypeURI()) && b.getTitle().equalsIgnoreCase(x.getTitle()) && b.getDescription().equalsIgnoreCase(x.getDescription())) {
                i++;
            }
        }
        
        assertTrue(i>1);
    }
}
