/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.rest.graphdb;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class RestTestBase {

    private GraphDatabaseService restGraphDb;
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 7474;
    private static LocalTestServer neoServer = new LocalTestServer(HOSTNAME,PORT).withPropertiesFile("neo4j-server.properties");
    private static final String SERVER_ROOT_URI = "http://" + HOSTNAME + ":" + PORT + "/db/data/";
    private static final String SERVER_CLEANDB_URI = "http://" + HOSTNAME + ":" + PORT + "/cleandb/secret-key";
    private static final String CONFIG = RestTestBase.class.getResource("/neo4j-server.properties").getFile();

    @BeforeClass
    public static void startDb() throws Exception {
        neoServer.start();
    }

    @Before
    public void setUp() throws URISyntaxException {
        neoServer.cleanDb();
        restGraphDb = new RestGraphDatabase(SERVER_ROOT_URI);
    }

    @After
    public void tearDown() throws Exception {
        restGraphDb.shutdown();
    }

    @AfterClass
    public static void shutdownDb() {
        neoServer.stop();

    }

    protected Relationship relationship() {
        Iterator<Relationship> it = node().getRelationships(Direction.OUTGOING).iterator();
        if (it.hasNext()) return it.next();
        return node().createRelationshipTo(restGraphDb.createNode(), Type.TEST);
    }

    protected Node node() {
        return restGraphDb.getReferenceNode();
    }
    
    protected GraphDatabaseService getGraphDatabase() {
    	return neoServer.getGraphDatabase();
    }

	protected GraphDatabaseService getRestGraphDb() {
		return restGraphDb;
	}
	
}
