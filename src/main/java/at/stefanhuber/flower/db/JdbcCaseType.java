/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.stefanhuber.flower.db;

import at.stefanhuber.flower.core.CaseType;
import java.util.Calendar;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * 
 * @author Stefan Huber
 */
public class JdbcCaseType extends JdbcDaoSupport implements CaseType {

    private static String CASE_TYPE_ATTRIBUTES_QUERY = "select * from `CaseType` where CaseTypeURI = ?";
    private static String CASE_TYPE_ATTRIBUTES_UPDATE = "update `CaseType` set Title = ?, Description = ? where CaseTypeURI = ?";
    private static String CREATE_NEW_CASE_TYPE = "insert into `CaseType` (CaseTypeURI,Title,Description) values (?,?,?)";
    private String caseTypeURI;
    // updateMe flag indicates iff the Case data should be updated!
    private boolean updateMe = true;
    private String title;
    private String description;

    public JdbcCaseType(DataSource dataSource, String title, String description) {
        this.setDataSource(dataSource);
        this.title = title;
        this.description = description;

        createCaseType();

        this.updateMe = false;
    }

    public JdbcCaseType(DataSource dataSource, String caseTypeURI) {
        this.setDataSource(dataSource);
        this.caseTypeURI = caseTypeURI;

        this.updateMe = true;
    }

    private void createCaseType() {
        if (caseTypeURI == null) {

            // TODO: make it better
            caseTypeURI = "http://stefanhuber.at/test/" + Calendar.getInstance().getTimeInMillis() + "/case-type/" + title.toLowerCase().trim().replace(' ', '-');

            getJdbcTemplate().update(CREATE_NEW_CASE_TYPE, caseTypeURI, title, description);
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
        Map<String, Object> map = getJdbcTemplate().queryForMap(CASE_TYPE_ATTRIBUTES_QUERY, this.caseTypeURI);
        if (!map.isEmpty()) {
            this.title = map.containsKey("Title") ? (String) map.get("Title") : "";
            this.description = map.containsKey("Description") ? (String) map.get("Description") : "";
        }

        updateMe = false;
    }

    public void updateTitle(String title) {
        this.title = title;
        getJdbcTemplate().update(CASE_TYPE_ATTRIBUTES_UPDATE,title,description,caseTypeURI); 
    }

    public void updateDescription(String description) {
        this.description = description;
        getJdbcTemplate().update(CASE_TYPE_ATTRIBUTES_UPDATE,title,description,caseTypeURI); 
    }
}
