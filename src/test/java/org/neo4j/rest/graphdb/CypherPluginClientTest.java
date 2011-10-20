package org.neo4j.rest.graphdb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResultBuilder;


/**
 * User: KBurchardi
 * Date: 13.10.11
 * Time: 14:26
 */
public class CypherPluginClientTest extends RestTestBase {
    private RestCypherQueryEngine queryEngine;
    private RestAPI restAPI;
    private MatrixDataGraph embeddedMatrixdata;
    private MatrixDataGraph restMatrixData;


    @Before
    public void init(){
        embeddedMatrixdata = new MatrixDataGraph(getGraphDatabase()).createNodespace();
        restMatrixData = new MatrixDataGraph(getRestGraphDb());
        this.restAPI = ((RestGraphDatabase)getRestGraphDb()).getRestAPI();
        queryEngine = new RestCypherQueryEngine(restAPI);
    }

    @Test
    public void testPluginClient(){
        CypherPlugin proxy = this.restAPI.getPlugin(CypherPlugin.class);
        Iterable<Object> result = proxy.execute_query("start n=node({reference}) return n", MapUtil.map("reference", 0), null);
        Node resultNode = (Node)new QueryResultBuilder(result).to(Node.class).single();
        final String queryString = "start n=node({reference}) return n";
        final Node resultNodeCypher = (Node) queryEngine.query(queryString, MapUtil.map("reference",0)).to(Node.class).single();
        Assert.assertEquals(resultNodeCypher, resultNode);

    }
}
