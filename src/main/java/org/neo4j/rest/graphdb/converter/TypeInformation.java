package org.neo4j.rest.graphdb.converter;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
* User: KBurchardi
* Date: 18.10.11
* Time: 17:16
*/
public class TypeInformation {

    Class type;
    Class[] genericArguments;

    public TypeInformation(Type type) {
        this.type = convertToClass(type);
        this.genericArguments = extractGenericArguments(type);
    }

    public TypeInformation(Object object){
        this.type = object.getClass();
        this.genericArguments = extractGenericArgumentsFromObject(object);
    }

    private Class convertToClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }else{
            return(Class)((ParameterizedType)type).getRawType();
        }
    }

    public boolean isSingleType(){
        return !isCollectionType();
    }

    public boolean isCollectionType() {
        return (isCollection() || isMap());
    }

    public boolean isMap() {
        return Map.class.isAssignableFrom(this.type);
    }

    public boolean isCollection() {
        return Iterable.class.isAssignableFrom(this.type);
    }

    private Class[] extractGenericArgumentsFromObject(Object object){
        if (isCollectionType()){
           if(isCollection()){
              if(((Iterable)object).iterator().hasNext()){
                 return  new Class[]{((Iterable)object).iterator().next().getClass()};
              }
           }else{
              if (!((Map)object).isEmpty()){
                  return  new Class[]{((Map)object).keySet().iterator().next().getClass(), ((Map)object).values().iterator().next().getClass()};
              }
           }
        }
        return null;
    }




    private Class[] extractGenericArguments(Type type) {

        if (!(type instanceof ParameterizedType)) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Class[]result = new Class[actualTypeArguments.length];
        for (int i = 0; i < actualTypeArguments.length; i++) {
            result[i] = convertToClass(actualTypeArguments[i]);
        }
        return result;
    }

    public boolean isInstance(Object resultObject, Class type) {
        return type.isInstance(resultObject);
    }

    public boolean isGraphEntity(Class classType) {
        return Node.class.isAssignableFrom(classType)|| Relationship.class.isAssignableFrom(classType);
    }

    public boolean isPath(Class classType){
        return Path.class.isAssignableFrom(classType);
    }

     public Class getType() {
        return type;
    }

    public Class[] getGenericArguments() {
        return genericArguments;
    }
}
