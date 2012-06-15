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

/**
 *
 * @author SCHIPFLINGER Martin
 */
public class RecommendationObject {
    
    String caseTypeURI;
    String predecessor;
    String successor;
    double relevance;
    
    
    public RecommendationObject(String _caseTypeURI, String _predecessor, String _successor, double _relevance){
        caseTypeURI = _caseTypeURI;
        predecessor = _predecessor;
        successor = _successor;
        relevance = _relevance;
    }
    
}
