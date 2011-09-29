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
    public String getIndexName() {
        return indexName;
    }

    protected final RestAPI restApi;

    RestIndex( RestRequest restRequest, String indexName, RestAPI restApi ) {
        this.restRequest = restRequest;
        this.indexName = indexName;
        this.restApi = restApi;
    }

   

    private String getTypeName() {
        return getEntityType().getSimpleName().toLowerCase();
    }

    public void add( T entity, String key, Object value ) {
       restApi.addToIndex(entity, this, key, value);
    }

    public String indexPath( String key, Object value ) {
        return "index/" + getTypeName() + "/" + indexName + (key!=null? "/" + ExecutingRestRequest.encode( key ) :"") + (value!=null ? "/" + ExecutingRestRequest.encode( value ):"");
    }
    private String queryPath( String key, Object value ) {
        return indexPath(key,null) + "?query="+ExecutingRestRequest.encode( value );
    }

    public void remove( T entity, String key, Object value ) {
       restApi.removeFromIndex(this, entity, key, value);
    }  

    public void remove(T entity, String key) {
       restApi.removeFromIndex(this, entity, key);
    }

    public void remove(T entity) {       
        restApi.removeFromIndex(this, entity);
    }

    public void delete() {
       restApi.delete(this);
    }
    
    public void deleteIndex(String indexPath) {
        restApi.deleteIndex(this, indexPath);
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
    
    public String getName() {
        return indexName;
    }



    public RestRequest getRestRequest() {
        return restRequest;
    }

}
