
package org.xeneo.db;

import org.xeneo.core.task.CaseType;
import org.xeneo.db.services.URIGenerator;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * 
 * @author Stefan Huber
 */
public class JdbcCaseType implements CaseType {

    private static String CASE_TYPE_ATTRIBUTES_QUERY = "select * from `CaseType` where CaseTypeURI = ?";
    private static String CASE_TYPE_ATTRIBUTES_UPDATE = "update `CaseType` set Title = ?, Description = ? where CaseTypeURI = ?";
    private static String CREATE_NEW_CASE_TYPE = "insert into `CaseType` (CaseTypeURI,Title,Description) values (?,?,?)";
    private String caseTypeURI;
    // updateMe flag indicates iff the Case data should be updated!
    private boolean updateMe = true;
    private String title;
    private String description;
    
    private JdbcTemplate jdbcTemplate;

    public JdbcCaseType(JdbcTemplate jdbcTemplate, String title, String description) {
        this.jdbcTemplate = jdbcTemplate;
        this.title = title;
        this.description = description;

        createCaseType();

        this.updateMe = false;
    }

    public JdbcCaseType(JdbcTemplate jdbcTemplate, String caseTypeURI) {
        this.jdbcTemplate = jdbcTemplate;
        this.caseTypeURI = caseTypeURI;

        this.updateMe = true;
    }

    private void createCaseType() {
        if (caseTypeURI == null) {
            caseTypeURI = URIGenerator.getInstance().generateURI("casetype");
            jdbcTemplate.update(CREATE_NEW_CASE_TYPE, caseTypeURI, title, description);
        }
    }

    public String getCaseTypeURI() {
        return caseTypeURI;
    }

    public String getTitle() {
        if (updateMe) {
            updateMe();
        }

        return title;
    }

    public String getDescription() {
        if (updateMe) {
            updateMe();
        }

        return description;
    }

    private void updateMe() {
        Map<String, Object> map = jdbcTemplate.queryForMap(CASE_TYPE_ATTRIBUTES_QUERY, this.caseTypeURI);
        if (!map.isEmpty()) {
            this.title = map.containsKey("Title") ? (String) map.get("Title") : "";
            this.description = map.containsKey("Description") ? (String) map.get("Description") : "";
        }

        updateMe = false;
    }

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
        jdbcTemplate.update(CASE_TYPE_ATTRIBUTES_UPDATE,title,description,caseTypeURI);
    }
}
