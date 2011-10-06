/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.rest.graphdb;


import org.neo4j.graphdb.*;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.RestConfig;
import org.neo4j.rest.graphdb.index.RestIndexManager;
import java.net.URI;
import java.util.Collection;


public class RestGraphDatabase extends AbstractRemoteDatabase {   
    private RestAPI restAPI;

    
    public RestGraphDatabase( RestAPI api){
    	this.restAPI = api;    	
    }
    
    public RestGraphDatabase( String uri ) {     
        this( new ExecutingRestRequest( uri ));
    }

    public RestGraphDatabase( String uri, String user, String password ) {        
        this(new ExecutingRestRequest( uri, user, password ));
    }
    
    public RestGraphDatabase( RestRequest restRequest){
    	this(new RestAPI(restRequest)); 	
    } 
    
    
    public RestAPI getRestAPI(){
    	return this.restAPI;
    }
    
    
    public RestIndexManager index() {
       return this.restAPI.index();
    }

    public Node createNode() {
    	return this.restAPI.createNode(null);
    }
  
    public Node getNodeById( long id ) {
    	return this.restAPI.getNodeById(id);
    }

    public Node getReferenceNode() {
        return this.restAPI.getReferenceNode();
    }

    public Relationship getRelationshipById( long id ) {
    	return this.restAPI.getRelationshipById(id);
    }    

  
    public RestRequest getRestRequest() {
        return this.restAPI.getRestRequest();
    }

    public long getPropertyRefetchTimeInMillis() {
        return this.restAPI.getPropertyRefetchTimeInMillis();
	}
    @Override
    public String getStoreDir() {
        return this.restAPI.getStoreDir();
    }

    @Override
    public Config getConfig() {
        return new RestConfig(this);
    }

    @Override
    public <T> Collection<T> getManagementBeans(Class<T> tClass) {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }
}
