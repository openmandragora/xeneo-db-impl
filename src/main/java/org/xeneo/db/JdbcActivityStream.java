package org.xeneo.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.xeneo.core.activity.Activity;
import org.xeneo.core.activity.ActivityStream;
import org.xeneo.core.activity.Object;
import org.xeneo.core.activity.Filter;

public class JdbcActivityStream extends JdbcDaoSupport implements ActivityStream {

	private static Logger logger = Logger.getLogger(JdbcActivityStream.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	// SQL queries
	private static String GET_ACTIVITIES_BY_TASK_AND_CASE = "select * from Activity a inner join TaskContext c on a.ActivityURI = c.ActivityURI WHERE c.TaskURI = %s and c.CaseURI = %s ORDER BY CreationDate LIMIT %s";
	private static String GET_ACTIVITIES_BY_CASE = "select * from Activity a inner join TaskContext c on a.ActivityURI = c.ActivityURI WHERE c.CaseURI = %s ORDER BY CreationDate LIMIT %s";

	
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
		                act.setActorURI(rs.getString("ActorURI"));
		                act.setActionURI(rs.getString("ActionURI"));
		                act.setCreationDate(rs.getDate("CreationDate"));
		                act.setSummary(rs.getString("Summary"));
		                act.setContent(rs.getString("Content"));
		                
		                Object obj = new Object();
		                obj.setObjectName(rs.getString("ObjectName"));
		                obj.setObjectTypeURI(rs.getString("ObjectTypeURI"));
		                obj.setObjectURI(rs.getString("ObjectURI"));
		                act.setObject(obj);
		                
		                String targetURI = rs.getString("TargetURI");
		                if (!targetURI.isEmpty()) {
		                	Object tar = new Object();
		                	tar.setObjectURI(targetURI);
		                	tar.setObjectName(rs.getString("TargetName"));
		                	tar.setObjectTypeURI(rs.getString("TargetTypeURI"));
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
