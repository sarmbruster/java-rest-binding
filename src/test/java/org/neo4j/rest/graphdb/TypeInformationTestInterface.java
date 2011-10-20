package org.neo4j.rest.graphdb;


import org.neo4j.graphdb.Node;

import java.util.Collection;
import java.util.Map;

/**
 * User: KBurchardi
 * Date: 18.10.11
 * Time: 16:09
 */
public interface TypeInformationTestInterface {
    Node testSingleValueNode();
    String testSingleValueString();
    Iterable<Object> testIterableObject();
    Iterable<Node> testIterableNode();
    Collection<Object> testCollectionObject();
    Collection<Node> testCollectionNode();
    Map<String,Object> testMapStringObject();
    Map<Integer, Node> testMapIntegerNode();
}
