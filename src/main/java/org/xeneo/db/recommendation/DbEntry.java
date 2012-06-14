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

/**
 *
 * @author SCHIPFLINGER Martin
 */
class DBEntry {
    private String caseTypeUri;
    private String taskUri;
    private Date creationDate;

    /**
     * @return the caseTypeUri
     */
    public String getCaseTypeUri() {
        return caseTypeUri;
    }

    /**
     * @param caseTypeUri the caseTypeUri to set
     */
    public void setCaseTypeUri(String caseTypeUri) {
        this.caseTypeUri = caseTypeUri;
    }

    /**
     * @return the taskUri
     */
    public String getTaskUri() {
        return taskUri;
    }

    /**
     * @param taskUri the taskUri to set
     */
    public void setTaskUri(String taskUri) {
        this.taskUri = taskUri;
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    
   
    
}
