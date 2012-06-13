/*
 * Copyright 2012 XENEO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xeneo.db.recommendation;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.xeneo.core.security.User;
import java.lang.Throwable;
import org.springframework.beans.factory.annotation.Autowired;


/**
 *
 * @author SCHIPFLINGER Martin
 */
public class MarkovRecommendation implements RecommendationStrategy {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void calcRecommendations() throws InvalidResultSetAccessException{

        String sql = "select a.CreationDate, tc.TaskURI, c.CaseURI"
                + " from Activity as a"
                + " inner join TaskContext as tc"
                + " on a.ActivityURI = tc.ActivityURI"
                + " inner join `Case` as c"
                + " on tc.CaseURI = c.CaseURI"
                + " order by a.CreationDate";
        
        System.out.println(sql);
        
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);


        while (rs.next()) {
            String caseUri = rs.getString("CaseURI");
            String taskURI = rs.getString("TaskURI");
            Date creationDate = rs.getDate("CreationDate");
            System.out.println(caseUri);
        }








    }
}
