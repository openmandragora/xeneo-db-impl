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
package org.xeneo.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.xeneo.core.activity.Object;
import org.xeneo.core.activity.*;

/**
 *
 * @author Stefan Huber
 */
public class JdbcActivityRepository implements ActivityRepository {

    public List<Activity> getActivitys(String userURI, int lowerLimit, int upperLimit) {
        return jdbcTemplate.query(GET_ACTIVITIES_BY_USER_URI, new ActivityRowMapper(),userURI,lowerLimit,upperLimit);
    }
    
    private static class ActivityRowMapper implements RowMapper {
        public Activity mapRow(ResultSet rs, int i) throws SQLException {
            
            String targetURI = rs.getString("TargetURI");
            Object tar = null;            
            if (targetURI != null) {
                tar = new org.xeneo.core.activity.Object();
                tar.setObjectURI(targetURI);
                tar.setObjectName(rs.getString("TargetName"));
                tar.setObjectTypeURI(rs.getString("TargetTypeURI"));
            }

            return new Activity.Builder()
                    .setActivityURI(rs.getString("ActivityURI"))
                    .setActionURI(rs.getString("ActionURI"))
                    .setCreationDate(rs.getDate("CreationDate"))
                    .setSummary(rs.getString("Summary"))
                    .setDescription(rs.getString("Description"))
                    .setActivityProvider(rs.getString("ActivityProviderURI"), rs.getString("ActivityProviderName"), rs.getString("ActivityProviderType"))
                    .setObject(rs.getString("ObjectURI"), rs.getString("ObjectName"), rs.getString("ObjectTypeURI"))
                    .setTarget(tar)
                    .setActor(rs.getString("ActorURI"), rs.getString("ActorName"), rs.getString("ActivityProviderURI"))
                    .build();
        }
    }
    
    private static final Logger logger = LoggerFactory.getLogger(JdbcActivityRepository.class);
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }    
    
    private static final String GET_ACTIVITY_BY_URI = "select count(*) from Activity where ActivityURI = ?";
    private static final String GET_OBJECT_BY_URI = "select count(*) from Object where ObjectURI = ?";
    private static final String GET_ACTIVITYPROVIDER_BY_URI = "select count(*) from ActivityProvider where ActivityProviderURI = ?";
    private static final String GET_ACTOR_BY_URI = "select count(*) from Actor where ActorURI = ?";

    private static final String GET_ACTIVITIES_BY_USER_URI = "select * from ActivityView a inner join Participant p on a.CaseURI = p.CaseURI WHERE p.UserURI = ? ORDER BY CreationDate LIMIT ?,?";
    private static final String GET_ACTIVITIES_BY_FILTER = "select * from ActivityView %s";
    
    private static final String ADD_ACTIVITY = "insert into Activity (ActivityURI,CreationDate,ActorURI,ActionURI,ObjectURI,TargetURI,Description,Summary, ActivityProviderURI) values (?,?,?,?,?,?,?,?,?)";
    private static final String ADD_OBJECT = "insert into Object (ObjectURI,Name,ObjectTypeURI) values (?,?,?)";
    private static final String ADD_ACTIVITYPROVIDER = "insert into ActivityProvider (ActivityProviderURI, ActivityProviderName, ActivityProviderType) values(?,?,?)";
    private static final String ADD_ACTOR = "insert into Actor (ActorURI, ActorName, ActivityProviderURI) values(?,?,?)";

    private static final String UPDATE_OBJECT_BY_URI = "update Object set Name = ?, ObjectTypeURI = ? where ObjectURI = ?";
    private static final String UPDATE_ACTIVITYPROVIDER_BY_URI = "update ActivityProvider set ActivityProviderName = ?, ActivityProviderType = ? where ActivityProviderURI = ?";
    private static String UPDATE_ACTOR_BY_URI = "update Actor set ActorName = ?, ActivityProviderURI = ? where ActorURI = ?";
    
    public void addActivity(Activity a) {
        
        Object obj = a.getObject();
        Assert.notNull(obj, "The Activity Object must not be null!");
        
        // Target might be null...
        Object tar = a.getTarget();
        
        Actor acto = a.getActor();
        Assert.notNull(acto, "The Actor must not be null!");
        
        ActivityProvider ap = a.getActivityProvider();
        Assert.notNull(ap, "The ActivityProvider must not be null!");

        // check if object exists otherwise create one
        if (isExistingObject(obj.getObjectURI())) {
            jdbcTemplate.update(UPDATE_OBJECT_BY_URI, obj.getObjectName(), obj.getObjectTypeURI(), obj.getObjectURI());
        } else {
            jdbcTemplate.update(ADD_OBJECT, obj.getObjectURI(), obj.getObjectName(), obj.getObjectTypeURI());
        }

        // check if target exists otherwise create one
        if (tar != null) 
            if(isExistingObject(tar.getObjectURI())) {            
                jdbcTemplate.update(UPDATE_OBJECT_BY_URI, tar.getObjectName(), tar.getObjectTypeURI(), tar.getObjectURI());
            } else {
                jdbcTemplate.update(ADD_OBJECT, tar.getObjectURI(), tar.getObjectName(), tar.getObjectTypeURI());
            }

        // check if ActivityProvider exists otherwise create one
        if (isExistingActivityProvider(ap.getActivityProviderURI())) {
            jdbcTemplate.update(UPDATE_ACTIVITYPROVIDER_BY_URI, ap.getActivityProviderName(), ap.getActivityProviderType(), ap.getActivityProviderURI());
        } else {
            jdbcTemplate.update(ADD_ACTIVITYPROVIDER, ap.getActivityProviderURI(), ap.getActivityProviderName(), ap.getActivityProviderType());
        }

        // check if actor exists otherwise create one
        if (isExistingActor(acto.getActorURI())) {
            jdbcTemplate.update(UPDATE_ACTOR_BY_URI, acto.getActorName(), acto.getActivityProviderURI(), acto.getActorURI());
        } else {
            jdbcTemplate.update(ADD_ACTOR, acto.getActorURI(), acto.getActorName(), acto.getActivityProviderURI());
        }
        
        jdbcTemplate.update(ADD_ACTIVITY, a.getActivityURI(), a.getCreationDate(), acto.getActorURI(), a.getActionURI(), obj.getObjectURI(), tar == null ? null : tar.getObjectURI(), a.getDescription(), a.getSummary(), ap.getActivityProviderURI());
    
    }

    public boolean isExistingActivity(String activityURI) {
        if (jdbcTemplate.queryForInt(GET_ACTIVITY_BY_URI, activityURI) > 0) {
            return true;
        }

        return false;
    }
    
    protected boolean isExistingObject(String objectURI) {
        if (jdbcTemplate.queryForInt(GET_OBJECT_BY_URI, objectURI) > 0) {
            return true;
        }

        return false;
    }

    protected boolean isExistingActivityProvider(String activityProviderURI) {
        if (jdbcTemplate.queryForInt(GET_ACTIVITYPROVIDER_BY_URI, activityProviderURI) > 0) {
            return true;
        }

        return false;
    }

    protected boolean isExistingActor(String actorURI) {
        if (jdbcTemplate.queryForInt(GET_ACTOR_BY_URI, actorURI) > 0) {
            return true;
        }

        return false;
    }

    public List<Activity> getActivities(Filter filter) {
        String query = String.format(GET_ACTIVITIES_BY_FILTER, filter.generateSQL());        
        logger.info("Activities are queried according to the following filter query: " + query);
        
        return jdbcTemplate.query(query,new ActivityRowMapper());
    }
    
    
    
}
