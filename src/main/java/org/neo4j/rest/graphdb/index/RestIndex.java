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
package org.neo4j.rest.graphdb.index;


import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.index.lucene.QueryContext;
import org.neo4j.rest.graphdb.*;
import org.neo4j.rest.graphdb.entity.RestEntity;

/**
 * @author mh
 * @since 24.01.11
 */
public abstract class RestIndex<T extends PropertyContainer> implements Index<T> {
    private final RestRequest restRequest;
    private final String indexName;
    protected final RestAPI restApi;

    RestIndex( RestRequest restRequest, String indexName, RestAPI restApi ) {
        this.restRequest = restRequest;
        this.indexName = indexName;
        this.restApi = restApi;
    }

    public String getName() {
        return indexName;
    }

    private String getTypeName() {
        return getEntityType().getSimpleName().toLowerCase();
    }

    public void add( T entity, String key, Object value ) {
        final RestEntity restEntity = (RestEntity) entity;
        final String indexPath = indexPath(key, value);
        try {
            addToIndex(restEntity, indexPath);
        } catch (Exception e) {
          throw new RuntimeException(String.format("Error adding element %d %s %s to index %s", restEntity.getId(), key, value, indexName));
        }
    }

    private void addToIndex(RestEntity restEntity, String indexPath) {
        String uri = restEntity.getUri();
        final RequestResult response = restRequest.post(indexPath, uri);
        if (response.getStatus() != 201) throw new RuntimeException("Error adding to index");
    }

    private String indexPath( String key, Object value ) {
        return "index/" + getTypeName() + "/" + indexName + (key!=null? "/" + ExecutingRestRequest.encode( key ) :"") + (value!=null ? "/" + ExecutingRestRequest.encode( value ):"");
    }
    private String queryPath( String key, Object value ) {
        return indexPath(key,null) + "?query="+ExecutingRestRequest.encode( value );
    }

    public void remove( T entity, String key, Object value ) {
        final String indexPath = indexPath(key, value) + "/" + ((RestEntity) entity).getId();
        deleteIndex(indexPath);
    }

    private void deleteIndex(String indexPath) {
        restRequest.delete(indexPath);
    }

    public void remove(T entity, String key) {
        deleteIndex(indexPath(key, null) + "/" + ((RestEntity) entity).getId());
    }

    public void remove(T entity) {
        deleteIndex(indexPath( null, null) + "/" + ( (RestEntity) entity ).getId());
    }

    public void delete() {
        deleteIndex(indexPath(null,null));
    }

    public org.neo4j.graphdb.index.IndexHits<T> get( String key, Object value ) {
        final String indexPath = indexPath(key, value);
        return restApi.queryIndex(indexPath,getEntityType());
    }


    public IndexHits<T> query( String key, Object value ) {
        final String indexPath = queryPath(key, value);
        return restApi.queryIndex(indexPath, getEntityType());
    }

    public org.neo4j.graphdb.index.IndexHits<T> query( Object value ) {
        if (value instanceof QueryContext) {
            value = ((QueryContext)value).getQueryOrQueryObject();
        }
        return query("null",value);
    }

}
