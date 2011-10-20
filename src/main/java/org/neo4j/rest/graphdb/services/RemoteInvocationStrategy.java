package org.neo4j.rest.graphdb.services;

import org.neo4j.rest.graphdb.RequestResult;

import java.lang.reflect.Method;

/**
 * User: KBurchardi
 * Date: 19.10.11
 * Time: 17:15
 */
public interface RemoteInvocationStrategy {

     public RequestResult invoke(Method method, Object[] args);
}
