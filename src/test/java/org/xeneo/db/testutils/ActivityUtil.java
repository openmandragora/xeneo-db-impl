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
package org.xeneo.db.testutils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.xeneo.core.activity.Activity;
import org.xeneo.core.activity.Object;

/**
 *
 * @author Stefan Huber
 */
public class ActivityUtil {   
    
    /*
     * Method for Random Activity Creation for testing purpouses. option specifies either activities with
     * a target attachted (1), without target (2), wither alternating no target / target (3)
     */
    public static List<Activity> createRandomActivities(String baseURI, int n, int option) {
        
        List<Activity> acts = new ArrayList<Activity>();

        Activity a;
        for (int i = 0; i < n; i++) {
            Long mill = Calendar.getInstance().getTimeInMillis();
            
            Object tar = null;
            if (option == 1 || (option == 3 && ((Math.random() * 100) % 2) == 0)){
                tar = new Object();
                tar.setObjectURI(baseURI + "/target/" + mill + "a");
                tar.setObjectName("Target Name " + mill + "b");
                tar.setObjectTypeURI(baseURI + "/targettype/" + mill + "c");
            }

            a = new Activity.Builder()
                    .setActivityURI(baseURI + "/activity/" + mill + "/uri/" + i)
                    .setActionURI(baseURI + "/someaction/" + i)
                    .setCreationDate(Calendar.getInstance().getTime())
                    .setSummary("some Summary for ... ")
                    .setDescription("some Description for ... ")
                    .setActivityProvider(baseURI + "/provider/testProvider", "Test Provider", "http://someblub.com/DMS")
                    .setObject(baseURI + "/object/" + mill + "/uri/" + i, "Object Name " + i, "http://someobject.com/sometype")
                    .setTarget(tar)
                    .setActor("http://xeneo.org/someActor", "Actor Someone", baseURI + "/provider/testProvider")
                    .build();
        
           

            acts.add(a);
        }

        return acts;
    }
    
    public static boolean equals(Activity a, Activity b) {
        return a.equals(b);
    }
    
}
