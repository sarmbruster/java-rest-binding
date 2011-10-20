package org.neo4j.rest.graphdb.services;

import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestRequest;
import org.neo4j.server.plugins.Name;

import javax.ws.rs.PathParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * User: KBurchardi
 * Date: 19.10.11
 * Time: 17:21
 */
public class PluginInvocation implements RemoteInvocationStrategy{

    private RestAPI restAPI;
    private String baseUri;
    private Class targetClass;

    public PluginInvocation(RestAPI restAPI, Class targetClass) {
        this.restAPI = restAPI;
        this.targetClass = targetClass;
        this.baseUri =  createBaseUri();
    }

     private String createBaseUri() {
        return "ext/"+this.targetClass.getSimpleName()+"/graphdb/";
    }

    @Override
    public RequestResult invoke(Method method, Object[] args) {
        RestRequest restRequest = this.restAPI.getRestRequest();
        return restRequest.get(this.baseUri + getUriSuffix(method), getRequestParams(method, args));
    }

     private String getUriSuffix(Method method){
        String suffix;
        if (method.isAnnotationPresent(Name.class)) {
           suffix = method.getAnnotation(Name.class).value();
        }else{
            suffix = method.getName();
        }

        return suffix;
    }

     private Map<String,Object> getRequestParams(Method method, Object[] args){
        Map<String,Object> requestParams = new HashMap<String, Object>(args.length);
        Class<?>[] paramTypes =  method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i=0; i <paramTypes.length; i++){
           for(Annotation annotation : parameterAnnotations[i]){
                if(annotation instanceof PathParam){
                    PathParam pathParam = (PathParam) annotation;
                    requestParams.put(pathParam.value(), args[i]);
                }
            }

        }
        return requestParams;
    }
}
