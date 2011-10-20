package org.neo4j.rest.graphdb.services;

import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestRequest;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * User: KBurchardi
 * Date: 19.10.11
 * Time: 17:39
 */
public class ServiceInvocation implements RemoteInvocationStrategy{

    enum RequestType {
        PUT,
        POST,
        GET,
        DELETE
    }

    private RestAPI restAPI;
    private String baseUri;
    private Class<?> targetClass;

    public ServiceInvocation(RestAPI restAPI, Class<?> targetClass, String baseUri) {
        this.restAPI = restAPI;
        this.targetClass = targetClass;
        this.baseUri = baseUri;
    }

    @Override
    public RequestResult invoke(Method method, Object[] args) {

       return makeRequest(determineRequestType(method), method, args);
    }

    private RequestResult makeRequest(RequestType requestType, Method method, Object[] args){
        RestRequest restRequest = this.restAPI.getRestRequest();
        if (requestType.equals(RequestType.GET)){
           return restRequest.get(createUri(method,args), getRequestParams(method, args));
        }

        if (requestType.equals(RequestType.PUT)){
            return restRequest.put(createUri(method, args), getRequestParams(method, args));
        }

        if (requestType.equals(RequestType.POST)){
            return restRequest.post(createUri(method, args), getRequestParams(method, args));
        }

        if (requestType.equals(RequestType.DELETE)){
            return restRequest.delete(createUri(method, args));
        }

        throw new IllegalStateException("trying to make a request without a known request type");
    }


    private RequestType determineRequestType(Method method){
        if (method.isAnnotationPresent(GET.class)){
            return RequestType.GET;
        }

        if (method.isAnnotationPresent(POST.class)){
            return RequestType.POST;
        }

        if (method.isAnnotationPresent(PUT.class)){
            return RequestType.POST;
        }

        if (method.isAnnotationPresent(DELETE.class)){
            return RequestType.DELETE;
        }
           throw new IllegalArgumentException("missing Annotation for the request type, e.g. @GET");
    }

    private String getClassIdentifier(){
        String identifier;
        if (targetClass.isAnnotationPresent(Path.class)){
          identifier= targetClass.getAnnotation(Path.class).value();
        }else{
          identifier= "/"+targetClass.getSimpleName();
        }

        return identifier;
    }

    private String getMethodPathIdentifier(Method method){
        if (method.isAnnotationPresent(Path.class)){
             return method.getAnnotation(Path.class).value();
        }

        throw new IllegalArgumentException("missing @Path annotation on method "+method.getName());
    }

    private String createUri(Method method, Object[] args){
        String uri = this.baseUri;
        uri+= getClassIdentifier();
        uri+= getMethodPathIdentifier(method);
        uri = replaceMethodPathWithActualValue(method, uri, args);
        return uri;
    }

    private String replaceMethodPathWithActualValue(Method method, String uri, Object[] args){
        String newUri = uri;
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class[] parameterTypes = method.getParameterTypes();


        for (int i=0; i <parameterTypes.length; i++){
            for(Annotation annotation : parameterAnnotations[i]){
                if(annotation instanceof PathParam){
                    PathParam pathParam = (PathParam) annotation;
                    newUri = uri.replace("{"+pathParam.value()+"}", args[i].toString());
                }
            }
        }

        return newUri;
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
