package com.kickass.ifssol.util.reflect;

import com.kickass.ifssol.util.StringUtility;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

public class DocTemplate {
    private static final Logger LOGGER = LogManager.getLogger(DocTemplate.class);

    private Class clazz;
    private boolean root;
    private Map<String,Method> addMethods = new HashMap<>();
    private Map<String,Method> setMethods = new HashMap<>();
    private Map<String,Method> getMethods = new HashMap<>();

    private Set<Class> clazzSet = new HashSet<>();
    private DocTemplateMap docTemplateMap;
    private Class factoryClazz;
    private Reflector reflector;
    private Object rootInstance;
    private DocTemplate parentDocTemplate;

    public DocTemplate(String clazzName,
                       Reflector reflector,
                       DocTemplateMap docTemplateMap) throws ClassNotFoundException {
        this.clazz = Class.forName(clazzName);
        this.reflector = reflector;
        this.root = false;
        docTemplateMap.put(clazz, this);
        this.docTemplateMap = docTemplateMap;
    }

    public DocTemplate(Class clazz,
                       Reflector reflector,
                       boolean root,
                       DocTemplateMap docTemplateMap) {
        this.clazz = clazz;
        this.reflector = reflector;
        this.root = root;
        if (root) {
            this.rootInstance = createRootInstance();
        }
        docTemplateMap.put(clazz, this);
        this.docTemplateMap = docTemplateMap;
    }

    public Class getClazz() {
        return clazz;
    }

    public void addAddMethod(Method method) {
        addMethods.put(method.getName(), method);
        clazzSet.add(method.getReturnType());
    }

    public void addSetMethod(Method method) {
        setMethods.put(method.getName(), method);
    }

    public void addGetMethod(Method method) {
        getMethods.put(method.getName(), method);
        clazzSet.add(method.getReturnType());
    }

    private Object createRootInstance() {
        /*
        Object instance = null;
        try {
            factoryClazz = Class.forName(clazz.getName() + "$Factory");
            Method m = factoryClazz.getMethod("newInstance");
            instance = m.invoke(null);
        } catch (Exception e) {
            LOGGER.error("Unable to create the Factory for " + clazz.getName(), e);
        }
        return instance;
         */
        return createInstanceUsingFactoryMethod(clazz.getName());
    }

    //TODO should throw TemplateCreationException
    public Object createInstanceUsingFactoryMethod(String name) {
        Object instance = null;
        try {
            Class factoryClazz = Class.forName(name + "$Factory");
            Method m = factoryClazz.getMethod("newInstance");
            instance = m.invoke(null);
        } catch (Exception e) {
            LOGGER.error("Unable to create the Factory for " + name, e);
        }
        return instance;
    }


    public void done() {
        for (Class clazz : clazzSet) {
            reflector.process(clazz);
        }
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public Map<String, Method> getAddMethods() {
        return addMethods;
    }

    public Method getAddMethod(String name) {
        return addMethods.get(name);
    }

    public Method getSetMethod(String name) {
        return setMethods.get(name);
    }

    public Method getGetMethod(String name) {
        return getMethods.get(name);
    }

    public Method getGetMethodReturnType(String name) {

        Method method =  getMethods.get("get" + StringUtility.makeFirstLetterCap(name));
        if (method == null) {
            getMethods.get("addNew" + StringUtility.makeFirstLetterCap(name));
        }

        return null;
    }

    public Method getSetMethodByVarName(String name) {
        return setMethods.get("set"+name);
    }


    public void setAddMethods(Map<String, Method> addMethods) {
        this.addMethods = addMethods;
    }

    public Map<String, Method> getSetMethods() {
        return setMethods;
    }

    public void setSetMethods(Map<String, Method> setMethods) {
        this.setMethods = setMethods;
    }

    public Map<String, Method> getGetMethods() {
        return getMethods;
    }

    public void setGetMethods(Map<String, Method> getMethods) {
        this.getMethods = getMethods;
    }

    public Object getRootInstance() {
        //return rootInstance;
        return createRootInstance();
    }

    public void setRootInstance(Object rootInstance) {
        this.rootInstance = rootInstance;
    }

    public boolean isRoot() {
        return root;
    }

    public Reflector getReflector() {
        return  reflector;
    }

    public DocTemplateMap getDocTemplateMap() {
        return docTemplateMap;
    }

    public DocTemplate getParentDocTemplate() {
        return parentDocTemplate;
    }

    public void setParentDocTemplate(DocTemplate parentDocTemplate) {
        this.parentDocTemplate = parentDocTemplate;
    }
}