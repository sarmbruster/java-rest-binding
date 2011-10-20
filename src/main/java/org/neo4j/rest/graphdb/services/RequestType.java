package org.neo4j.rest.graphdb.services;

import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestRequest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.lang.reflect.Method;
import java.util.Map;

/**
* User: KBurchardi
* Date: 21.10.11
* Time: 00:57
*/
enum RequestType {
    PUT {
        @Override
        RequestResult makeRequest(String uri, Map<String, Object> requestParams, RestRequest restRequest) {
           return restRequest.put(uri, requestParams);
        }
    },
    POST {
        @Override
        RequestResult makeRequest(String uri, Map<String, Object> requestParams, RestRequest restRequest) {
            return restRequest.post(uri, requestParams);
        }
    },
    GET {
        @Override
        RequestResult makeRequest(String uri, Map<String, Object> requestParams, RestRequest restRequest) {
           return restRequest.get(uri, requestParams);
        }
    },
    DELETE {
        @Override
        RequestResult makeRequest(String uri, Map<String, Object> requestParams, RestRequest restRequest) {
             return restRequest.delete(uri);
        }
    };

    public static RequestType determineRequestType(Method method){
        if (method.isAnnotationPresent(GET.class)){
            return GET;
        }

        if (method.isAnnotationPresent(POST.class)){
            return POST;
        }

        if (method.isAnnotationPresent(PUT.class)){
            return PUT;
        }

        if (method.isAnnotationPresent(DELETE.class)){
            return DELETE;
        }
           throw new IllegalArgumentException("missing Annotation for the request type, e.g. @GET");
    }

    abstract RequestResult makeRequest(String uri, Map<String, Object> requestParams, RestRequest restRequest);

}
