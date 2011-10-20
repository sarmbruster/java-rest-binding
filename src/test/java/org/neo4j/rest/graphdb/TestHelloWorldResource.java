package org.neo4j.rest.graphdb;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.Response;

/**
 * User: KBurchardi
 * Date: 20.10.11
 * Time: 12:44
 */
@Ignore
public class TestHelloWorldResource extends RestTestBase {
     private RestAPI restAPI;

    @Before
    public void init(){
        this.restAPI = ((RestGraphDatabase)getRestGraphDb()).getRestAPI();
    }

    @Test
    public void testHelloWorldService() throws Exception {
        HelloWorldService hws = this.restAPI.getService(HelloWorldService.class, SERVER_ROOT_URI+"test");
        Response response = hws.hello(0);
    }
    /**
    @Test
    public void dummy() {
        RequestResult result = this.restAPI.getRestRequest().get("test/helloworld/0");
        assertEquals("Hello World, nodeId=0", result.getEntity());
    }  */
}
