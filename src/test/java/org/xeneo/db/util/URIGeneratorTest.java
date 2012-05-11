/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xeneo.db.util;

import org.xeneo.db.services.URIGenerator;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.*;
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
@ContextConfiguration(locations="/test-config.xml")
public class URIGeneratorTest {
    
    private Logger logger = LoggerFactory.getLogger(URIGeneratorTest.class);
    
    @Autowired
    private URIGenerator gen;
    
    public URIGeneratorTest() {        
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
    @Ignore
    public void getPossibleStrings() {
        Stack<Character> st = new Stack<Character>();
        String s = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int l = s.length();
        String h;
        for(int i=0;i<100000;i++) {
            h="";
            st.clear();
            int x = i;
            int y = 0;
            
            while (i >= (Math.pow(l, y) <= 1 ? -1 : Math.pow(l, y))) {         
                // logger.info(x%l);                
                st.add(s.charAt(x%l));
                x /= l;
                y++;
            }

            while (!st.isEmpty()) {
                h += st.pop();
            }            
                        
            logger.info(i + ": " + h);
        }
        
    }
     
    public void check (long n, String r) {
        logger.info("Check " + n + " return: " + gen.getStringRepresentation(n) + " should be: " + r);
        assertTrue(gen.getStringRepresentation(n).equals(r));
    }

    @Test
    public void testGetStringRepresentation() {
        
        check(0,"0");
        check(1,"1");
        check(10,"a");
        check(61,"Z");
        check(62,"10");
        check(63,"11");        
        check(124,"20");
        check(999,"g7");
        check(3843,"ZZ");
        check(3844,"100");
        check(99999,"q0T");
    }
    
    @Test
    public void testURIs() {
                
        logger.info(gen.generateURI());
        logger.info(gen.generateURI("activity"));
        
        for (int i = 0; i<100;i++)
            gen.generateURI();
        
        logger.info(gen.generateURI());
        logger.info(gen.generateURI("ecommerce/activity"));        
    }
    
    
}
