package org.neo4j.rest.graphdb.converter;

import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.entity.RestEntity;

public class RestEntityPropertyRefresher implements RestResultConverter {
    
    private final RestEntity entity;
    
    public RestEntityPropertyRefresher(RestEntity entity){
        this.entity = entity;
    }
    
    @Override
    public Object convertFromRepresentation(RequestResult value) {         
         return this.entity;       
    }

}
