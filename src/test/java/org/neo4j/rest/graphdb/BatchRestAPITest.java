package org.neo4j.rest.graphdb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.rest.graphdb.batch.BatchCallback;
import org.neo4j.rest.graphdb.entity.RestRelationship;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.neo4j.rest.graphdb.util.TestHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.neo4j.helpers.collection.MapUtil.map;

public class BatchRestAPITest extends RestTestBase {
    private RestAPI restAPI;
  
    // TODO transaction check, exception handling if an exception happened in the server

    @Before
    public void init(){
        this.restAPI = ((RestGraphDatabase)getRestGraphDb()).getRestAPI();
    }
    
    @Test
    public void testCreateNode(){
        TestBatchResult response =this.restAPI.executeBatch(new BatchCallback<TestBatchResult>() {
            
            @Override
            public TestBatchResult recordBatch(RestAPI batchRestApi) {
                TestBatchResult result=new TestBatchResult();
                result.n1 = batchRestApi.createNode(map("name", "node1"));              
                result.n2 = batchRestApi.createNode(map("name", "node2"));
                return result;
            }
        });       
        assertEquals("node1", response.n1.getProperty("name"));      
        assertEquals("node2", response.n2.getProperty("name"));
    }
   
    @Test(expected = IllegalStateException.class)
    public void testLeakedBatchApiWontWork() {
        RestAPI leaked =this.restAPI.executeBatch(new BatchCallback<RestAPI>() {
            @Override
            public RestAPI recordBatch(RestAPI batchRestApi) {
                return batchRestApi;
            }
        });
        leaked.createNode(map());
    }
    
    @Test
    public void testSetNodeProperties(){
        TestBatchResult response =this.restAPI.executeBatch(new BatchCallback<TestBatchResult>() {
            
            @Override
            public TestBatchResult recordBatch(RestAPI batchRestApi) {
                TestBatchResult result=new TestBatchResult();
                result.n1 = batchRestApi.createNode(map("name", "node1"));
                result.n1.setProperty("test", "true");
                result.n1.setProperty("test2", "stilltrue");
                
                return result;
            }
        });  
       
        assertEquals("node1", response.n1.getProperty("name"));
        assertEquals("true", response.n1.getProperty("test"));
        assertEquals("stilltrue",response.n1.getProperty("test2"));
        assertEquals("true", getGraphDatabase().getNodeById(response.n1.getId()).getProperty("test"));
        assertEquals("stilltrue", getGraphDatabase().getNodeById(response.n1.getId()).getProperty("test2"));
       
    }
    
    @Test  (expected = org.neo4j.graphdb.NotFoundException.class)
    public void testDeleteNode(){
        TestBatchResult response =this.restAPI.executeBatch(new BatchCallback<TestBatchResult>() {
            
            @Override
            public TestBatchResult recordBatch(RestAPI batchRestApi) {
                TestBatchResult result=new TestBatchResult();
                result.n1 = batchRestApi.createNode(map("name", "node1"));
                result.n1.delete();
                result.n2 = batchRestApi.createNode(map("name", "node2"));                
                return result;
            }
        });         
       getGraphDatabase().getNodeById(response.n1.getId());
      
    }
    
    @Test  
    public void testDeleteRelationship(){
        TestBatchResult r =this.restAPI.executeBatch(new BatchCallback<TestBatchResult>() {
            
            @Override
            public TestBatchResult recordBatch(RestAPI batchRestApi) {
                TestBatchResult result=new TestBatchResult();
                result.n1 = batchRestApi.createNode(map("name", "newnode1"));
                result.n2 = batchRestApi.createNode(map("name", "newnode2"));
                result.rel = batchRestApi.createRelationship(result.n1, result.n2, Type.TEST, map("name", "rel") );
                result.rel.delete();
                
                return result;
            }
        });         
        Relationship foundRelationship = TestHelper.firstRelationshipBetween( r.n1.getRelationships(Type.TEST, Direction.OUTGOING), r.n1, r.n2);
        Assert.assertNull("found relationship", foundRelationship);
      
    }

    @Test
    public void testCreateRelationship(){
        TestBatchResult r = this.restAPI.executeBatch(new BatchCallback<TestBatchResult>() {
            @Override
            public TestBatchResult recordBatch(RestAPI batchRestApi) {
                TestBatchResult result=new TestBatchResult();
                result.n1 = batchRestApi.createNode(map("name", "newnode1"));
                result.n2 = batchRestApi.createNode(map("name", "newnode2"));
                result.rel = batchRestApi.createRelationship(result.n1, result.n2, Type.TEST, map("name", "rel") );
                result.allRelationships = result.n1.getRelationships();
                return result;
            }
        });

        Relationship foundRelationship = TestHelper.firstRelationshipBetween( r.n1.getRelationships(Type.TEST, Direction.OUTGOING), r.n1, r.n2);
        Assert.assertNotNull("found relationship", foundRelationship);
        assertEquals("same relationship", r.rel, foundRelationship);
        assertEquals("rel", r.rel.getProperty("name"));

        assertThat(r.n1.getRelationships(Type.TEST, Direction.OUTGOING), new IsRelationshipToNodeMatcher(r.n1, r.n2));
        assertThat(r.n1.getRelationships(Direction.OUTGOING), new IsRelationshipToNodeMatcher(r.n1, r.n2));
        assertThat(r.n1.getRelationships(Direction.BOTH), new IsRelationshipToNodeMatcher(r.n1, r.n2));
        assertThat(r.n1.getRelationships(Type.TEST), new IsRelationshipToNodeMatcher(r.n1, r.n2));
        assertThat(r.allRelationships, new IsRelationshipToNodeMatcher(r.n1, r.n2));
    }

    @Test
    public void testAddToIndex() {
        final MatrixDataGraph matrixDataGraph = new MatrixDataGraph(getGraphDatabase());
        matrixDataGraph.createNodespace();
        final IndexHits<Node> heroes = restAPI.executeBatch(new BatchCallback<IndexHits<Node>>() {
            @Override
            public IndexHits<Node> recordBatch(RestAPI batchRestApi) {
                Node n1 = batchRestApi.createNode(map("name", "Apoc"));
                final Index<Node> index = batchRestApi.index().forNodes("heroes");               
                index.add(n1, "indexname", "Apoc");
                return index.query("indexname:Apoc");
            }
        });
        assertEquals("1 hero",1,heroes.size());        
        IndexManager index = getGraphDatabase().index();             
        Index<Node> goodGuys = index.forNodes("heroes");
        IndexHits<Node> hits = goodGuys.get( "indexname", "Apoc" );
        Node apoc = hits.getSingle();
        
        assertEquals("Apoc indexed",apoc,heroes.iterator().next());
    }
    
    @Test
    public void testQueryIndex() {
        final MatrixDataGraph matrixDataGraph = new MatrixDataGraph(getGraphDatabase());
        matrixDataGraph.createNodespace();
        final IndexHits<Node> heroes = restAPI.executeBatch(new BatchCallback<IndexHits<Node>>() {
            @Override
            public IndexHits<Node> recordBatch(RestAPI batchRestApi) {
                final Index<Node> index = batchRestApi.index().forNodes("heroes");
                return index.query("name:Neo");
            }
        });
        assertEquals("1 hero",1,heroes.size());
        assertEquals("Neo indexed",matrixDataGraph.getNeoNode(),heroes.iterator().next());
    }
    
    @Test
    public void testDeleteIndex() {
        final MatrixDataGraph matrixDataGraph = new MatrixDataGraph(getGraphDatabase());
        matrixDataGraph.createNodespace();
            restAPI.executeBatch(new BatchCallback<Void>() {
            @Override
            public Void recordBatch(RestAPI batchRestApi) {
                final Index<Node> index = batchRestApi.index().forNodes("heroes");
                index.delete();
                return null;
            }
        });
            IndexManager index = matrixDataGraph.getGraphDatabase().index();          
            Assert.assertFalse( index.existsForNodes("heroes"));
    }
    
    /**
    @Test
    public void testRemoveEntryFromIndexWithGivenNode(){
        TestBatchResult response =this.restAPI.executeBatch(new BatchCallback<TestBatchResult>() {
            
            @Override
            public TestBatchResult recordBatch(RestAPI batchRestApi) {
                TestBatchResult result=new TestBatchResult();
                result.n1 = batchRestApi.createNode(map("name", "node1"));              
                result.n2 = batchRestApi.createNode(map("name", "node2"));
                final Index<Node> index = batchRestApi.index().forNodes("testIndex");
                index.add( result.n1, "indexname", "Node1");
                index.add( result.n2, "indexname", "Node2");
                index.remove(result.n1);
                return result;
            }
        });       
        IndexManager index = getGraphDatabase().index(); 
        Index<Node> testIndex = index.forNodes("testIndex");
        IndexHits<Node> hits = testIndex.get( "indexname", "Node1" );
        Assert.assertEquals("found in index results", false, hits.hasNext());
        hits = testIndex.get( "indexname", "Node2" );
        Assert.assertEquals("found in index results", true, hits.hasNext());
    }*/
    
    
    
  
    static class TestBatchResult {
        Node n1;
        Node n2;
        RestRelationship rel;
        Iterable<Relationship> allRelationships;
    }
}
