package org.neo4j.rest.graphdb;

import org.neo4j.server.plugins.Name;

import javax.ws.rs.PathParam;
import java.util.Map;

/**
 * User: KBurchardi
 * Date: 13.10.11
 * Time: 11:41
 */
public interface CypherPlugin {
    @Name( "execute_query" )
     Iterable<Object> executeScript(
            @PathParam("query") final String query,
            @PathParam( "params") Map parameters,
            @PathParam( "format") final String format);

     Iterable<Object> execute_query(
            @PathParam("query") final String query,
            @PathParam( "params") Map parameters,
            @PathParam( "format") final String format);
}
