package org.neo4j.rest.graphdb.services;

import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.converter.ResultTypeConverter;
import org.neo4j.rest.graphdb.converter.TypeInformation;
import org.neo4j.rest.graphdb.util.JsonHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * User: KBurchardi
 * Date: 13.10.11
 * Time: 14:03
 */
public class RestInvocationHandler implements InvocationHandler{

    private RestAPI restAPI;
    private RemoteInvocationStrategy invocationStrategy;
    private ResultTypeConverter resultTypeConverter;

    public RestInvocationHandler(RestAPI restAPI, RemoteInvocationStrategy invocationStrategy) {
        this.restAPI = restAPI;
        this.resultTypeConverter = new ResultTypeConverter(this.restAPI);
        this.invocationStrategy = invocationStrategy;
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final RequestResult requestResult = invocationStrategy.invoke(method,args);
        Object obj = JsonHelper.readJson(requestResult.getEntity());
        TypeInformation typeInfo = new TypeInformation( method.getGenericReturnType());
        return this.resultTypeConverter.convertToResultType(obj, typeInfo);

    }


   public static <T> T getInvocationProxy(Class<T> type, RestAPI restAPI, RemoteInvocationStrategy invocationStrategy){
      return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new RestInvocationHandler(restAPI, invocationStrategy));
   }




}
