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


import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


/**
 *
 * @author SCHIPFLINGER Martin
 */
public class MarkovRecommendation implements RecommendationStrategy {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    //static final Logger logger = (Logger) LoggerFactory.getLogger(MarkovRecommendation.class);

    public void calcRecommendations() throws InvalidResultSetAccessException {

        List<String> caseTypeList = new ArrayList<String>();
        List<RecommendationObject> recObjectList = new ArrayList<RecommendationObject>();
        
        //retrieve the needed fields from the database
        String sql = "select a.CreationDate, tc.TaskURI, c.CaseTypeURI"
                + " from Activity as a"
                + " inner join TaskContext as tc"
                + " on a.ActivityURI = tc.ActivityURI"
                + " inner join `Case` as c"
                + " on tc.CaseURI = c.CaseURI"
                //+ " where a.CreationDate > '2012-06-13 15:44:42.0'"
                + " order by a.CreationDate";

        System.out.println(sql);
        
        //mapps the returned row of the sql select to a list of DBEntries
        List<DBEntry> entryList = jdbcTemplate.query(sql, new DBEntryRowMapper());
        
        //Iterate through the list of entries to extract all different CaseTypes
        //logger.info("extracting CaseTypes ...");
        for (DBEntry entry : entryList) {
            if (!caseTypeList.contains(entry.getCaseTypeUri())) {
                caseTypeList.add(entry.getCaseTypeUri());
            }
        }
        
        //logger.info("extracting recommendations ...");
        for(String caseType : caseTypeList){
            
            List<DBEntry> tmpList = new ArrayList<DBEntry>();
            System.out.println("---------------------------"+caseType+"---------------------------------");
            //seperate each CaseType in a List
            for(DBEntry entry : entryList){
                if(entry.getCaseTypeUri().equals(caseType)){
                    tmpList.add(entry);
                }
            }
            //create RecommendationObjects for each CaseType
            for(int i = 0; i<tmpList.size()-1;i++){
                String pre = tmpList.get(i).getTaskUri();
                String suc = tmpList.get(i+1).getTaskUri();
                double rel = tmpList.get(i+1).getCreationDate().getTime()/10000000000000.0;
                recObjectList.add(new RecommendationObject(caseType,pre,suc,rel));
                //System.out.println(caseType+";"+pre+"; "+suc+"; "+rel);
            }
            
        }
        //insert RecommendationObjects in database
        for (RecommendationObject rec : recObjectList){
            String caseTypeURI = rec.caseTypeURI;
            String predecessor = rec.predecessor;
            String successor = rec.successor;
            double relevance = rec.relevance;
            
            String sql1 = "select * from Recommendation"
                    +" WHERE CaseTypeURI = '"+caseTypeURI+"' AND"
                    +" Predecessor = '"+predecessor+"' AND"
                    +" Successor = '"+successor+"'";
            SqlRowSet rs = jdbcTemplate.queryForRowSet(sql1);
            
            if(rs.next()){
                double oldrelevance = rs.getDouble("Relevance");
                double newrelevance = oldrelevance + rec.relevance;
                String updatesql = "update Recommendation"
                        +" SET Relevance = "+newrelevance+""
                        +" WHERE CaseTypeURI = '"+caseTypeURI+"' AND"
                        +" Predecessor = '"+predecessor+"' AND"
                        +" Successor = '"+successor+"'";
                jdbcTemplate.update(updatesql);
                //update
            }
            else
            {
                String insertsql = "insert into Recommendation(CaseTypeURI, Predecessor, Successor, Relevance)"
                        +" values('"+caseTypeURI+"','"+predecessor+"','"+successor+"',"+relevance+")";
                jdbcTemplate.execute(insertsql);
                //insert
            }
        }
        
        

    }
}
