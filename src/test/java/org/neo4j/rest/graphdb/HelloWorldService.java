package org.neo4j.rest.graphdb;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * User: KBurchardi
 * Date: 20.10.11
 * Time: 13:21
 */
@Path( "/helloworld" )
public interface HelloWorldService {
    @GET
    @Produces( MediaType.TEXT_PLAIN )
    @Path( "/{nodeId}" )
    public Response hello( @PathParam( "nodeId" ) long nodeId );
}
