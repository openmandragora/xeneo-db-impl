package org.xeneo.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.xeneo.core.activity.*;
import org.xeneo.core.activity.Object;

public class JdbcActivityStream extends JdbcDaoSupport implements ActivityStream {

	private static Logger logger = LoggerFactory.getLogger(JdbcActivityStream.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	// SQL queries
	private static String GET_ACTIVITIES_BY_TASK_AND_CASE = "select * from Activity a inner join TaskContext c on a.ActivityURI = c.ActivityURI inner join Object o on a.ObjectURI = o.ObjectURI inner join Object t on a.TargetURI = t.ObjectURI inner join Actor ac on a.ActorURI = ac.ActorURI inner join ActivityPlugin actp on a.ActivityPluginURI = actp.ActivityPluginURI WHERE c.TaskURI = '%s' and c.CaseURI = '%s' ORDER BY CreationDate LIMIT 0, %s";
	private static String GET_ACTIVITIES_BY_CASE = "select * from Activity a inner join TaskContext c on a.ActivityURI = c.ActivityURI inner join Object o on a.ObjectURI = o.ObjectURI inner join Object t on a.TargetURI = t.ObjectURI inner join Actor ac on a.ActorURI = ac.ActorURI inner join ActivityPlugin actp on a.ActivityPluginURI = actp.ActivityPluginURI WHERE c.CaseURI = '%s' ORDER BY a.CreationDate LIMIT 0, %s";

	
	public List<Activity> getActivities(String caseURI, String taskURI, int limit) {			
			
		String query = "";
		if (taskURI == null) {
			query = String.format(GET_ACTIVITIES_BY_CASE,caseURI,limit);
		} else {
			query = String.format(GET_ACTIVITIES_BY_TASK_AND_CASE,taskURI,caseURI,limit);
		}
		
		List<Activity> activities = jdbcTemplate.query(query,
		        new RowMapper<Activity>() {
		            public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
		                Activity act = new Activity();
		                		                
		                act.setActivityURI(rs.getString("ActivityURI"));
		                //act.setActorURI(rs.getString("UserURI"));
		                act.setActionURI(rs.getString("ActionURI"));
		                act.setCreationDate(rs.getDate("CreationDate"));
		                act.setSummary(rs.getString("Summary"));
		                act.setDescription(rs.getString("Description"));
		                
                                ActivityProvider ap = new ActivityProvider();
                                ap.setActivityProviderName(rs.getString("actp.ActivityProviderName"));
                                ap.setActivityProviderType(rs.getString("actp.ActivityProviderType"));
                                ap.setActivityProviderURI(rs.getString("ActivityProviderURI"));
                                
                                Actor acto = new Actor();
                                acto.setActorName(rs.getString("ac.ActorName"));
                                acto.setActorURI(rs.getString("ac.ActorURI"));
                                acto.setUserURI(rs.getString("ac.UserURI"));
                                acto.setActivityProviderURI(rs.getString("ActivityProviderURI"));
                                
		                Object obj = new Object();
		                obj.setObjectName(rs.getString("o.Name"));
		                obj.setObjectTypeURI(rs.getString("o.ObjectTypeURI"));
		                obj.setObjectURI(rs.getString("ObjectURI"));
		                act.setObject(obj);
		                
		                String targetURI = rs.getString("TargetURI");
		                if (!targetURI.isEmpty()) {
		                	Object tar = new Object();
		                	tar.setObjectURI(targetURI);
		                	tar.setObjectName(rs.getString("t.Name"));
		                	tar.setObjectTypeURI(rs.getString("t.ObjectTypeURI"));
		                	act.setTarget(tar);
		                }		                
		                
		                return act;
		            }
		        });
		
		return activities;
	}

	public List<Activity> getActivities(String caseURI, int limit) {
		return getActivities(caseURI,null,limit);
	}

	public List<Activity> getActivities(Filter filter) {
		// TODO Auto-generated method stub
		return null;
	}
}
