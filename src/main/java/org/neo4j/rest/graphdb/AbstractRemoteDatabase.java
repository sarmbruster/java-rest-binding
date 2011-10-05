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

import java.io.Serializable;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.kernel.AbstractGraphDatabase;

abstract class AbstractRemoteDatabase extends AbstractGraphDatabase {
    public Transaction beginTx() {
        return new Transaction() {
            public void success() {
            }

            public void finish() {

            }

            public void failure() {
            }
        };
    }

    public <T> TransactionEventHandler<T> registerTransactionEventHandler( TransactionEventHandler<T> tTransactionEventHandler ) {
        throw new UnsupportedOperationException();
    }

    public <T> TransactionEventHandler<T> unregisterTransactionEventHandler( TransactionEventHandler<T> tTransactionEventHandler ) {
        throw new UnsupportedOperationException();
    }

    public KernelEventHandler registerKernelEventHandler( KernelEventHandler kernelEventHandler ) {
        throw new UnsupportedOperationException();
    }

    public KernelEventHandler unregisterKernelEventHandler( KernelEventHandler kernelEventHandler ) {
        throw new UnsupportedOperationException();
    }
    
    public boolean enableRemoteShell() {
        throw new UnsupportedOperationException();
    }

    public boolean enableRemoteShell( Map<String, Serializable> config ) {
        throw new UnsupportedOperationException();
    }

    public Iterable<Node> getAllNodes() {
        throw new UnsupportedOperationException();
    }
  
    public Iterable<RelationshipType> getRelationshipTypes() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <T> T getManagementBean(Class<T> type) {
        return null;
    }
    
    public void shutdown() {
    }
    
}
