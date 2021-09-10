package com.kickass.ifssol.util.reflect;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

public class DocTemplateMap<K,V> extends ConcurrentHashMap<Class, DocTemplate> {
    private DocTemplate rootDocTemplate;
    private static final Logger LOGGER = LogManager.getLogger(DocTemplateMap.class);

    public DocTemplate getRootDocTemplate() {
        return rootDocTemplate;
    }

    public void setRootDocTemplate(DocTemplate rootDocTemplate) {
        this.rootDocTemplate = rootDocTemplate;
    }

    public DocTemplate put(Class clazz, DocTemplate docTemplate) {
        if (rootDocTemplate == null) {
            rootDocTemplate = docTemplate;
        }
        return super.put(clazz, docTemplate);
    }
    public DocTemplate getByClassName(String className) {
        try {
            Class clazz = Class.forName(className);
            return get(clazz);
        }
        catch(ClassNotFoundException e) {
            LOGGER.error("Unable to getByClassName for " + className, e);
            return null;
        }
    }
    public DocTemplate get(Class clazz) {

        DocTemplate docTemplate = super.get(clazz);
        if (docTemplate == null) {
            Class[] interfaces = clazz.getInterfaces();
            for(Class parentInterface : interfaces) {
                docTemplate = this.get(parentInterface);
                if (docTemplate != null) {
                    break;
                }
            }
        }

        return docTemplate;
    }
}
