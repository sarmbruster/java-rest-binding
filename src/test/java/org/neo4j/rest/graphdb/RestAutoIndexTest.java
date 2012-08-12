/**
 * Copyright (c) 2002-2012 "Neo Technology,"
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

import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.AutoIndexer;
import org.neo4j.graphdb.index.RelationshipAutoIndexer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RestAutoIndexTest extends RestTestBase {

    @Test
    public void testEnableDisableAutoIndexerNode() {
        AutoIndexer<Node> indexer = getRestGraphDb().index().getNodeAutoIndexer();
        testEnableDisableAutoIndexer(indexer);
    }

    @Test
    public void testEnableDisableAutoIndexerRelationship() {
        RelationshipAutoIndexer indexer = getRestGraphDb().index().getRelationshipAutoIndexer();
        testEnableDisableAutoIndexer(indexer);
    }

    private void testEnableDisableAutoIndexer(AutoIndexer<? extends PropertyContainer> indexer) {
        assertFalse(indexer.isEnabled());
        indexer.setEnabled(true);
        assertTrue(indexer.isEnabled());
        indexer.setEnabled(false);
        assertFalse(indexer.isEnabled());
    }

}
