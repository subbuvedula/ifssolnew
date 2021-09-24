package com.kickass.ifssol.util.reflect;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class Reflector {
    private static final Logger LOGGER = LogManager.getLogger(Reflector.class);
    private Set<String> validPackages = new HashSet<>();
    private Map<String, Class> classMap = new HashMap<>();

    private DocTemplateMap docTemplateMap = new DocTemplateMap();

    //public boolean visited(Class clazz) {
    //    return docTemplateMap.containsKey(clazz.getSimpleName());
    //}

    public boolean visited(Class clazz) {
        DocTemplate docTemplate =  docTemplateMap.get(clazz);
        return (docTemplate != null) ;
    }

    public Reflector() {
        validPackages.add("org.openapplications.oagis.x9");
        validPackages.add("com.ibaset.solumina.oagis");
    }

    private boolean isPackageValid(Class clazz) {
        if (clazz == null) {
            LOGGER.error("Clazz is null while checking isPackageValid");
            return false;
        }
        for(String pkgName : validPackages) {
            if (clazz.getPackage() == null) {
                LOGGER.error("clazz.getPackage() is null while checking isPackageValid for class : " + clazz.getName() );
                return false;
            }
            if (clazz.getPackage().getName().startsWith(pkgName)) {
                LOGGER.info("Package is valid for : " + clazz.getName() );
                return true;
            }
        }
        return false;
    }

    public DocTemplate process(Class clazz) {
        return process(clazz, false);
    }

    public DocTemplate process(Class clazz, boolean root) {
        try {
            if (!isPackageValid(clazz)) {
                return null;
            }

            DocTemplate docTemplate =  docTemplateMap.get(clazz);
            if (docTemplate != null) {
                LOGGER.info("Already visited : " + clazz.getSimpleName());
                return  docTemplate;
            }

            //if (visited(clazz)) {
            //    LOGGER.info("Already visited : " + clazz.getSimpleName());
            //    return null;
            //}

            LOGGER.info("Visiting : " + clazz.getSimpleName());

            docTemplate = new DocTemplate(clazz, this, root, docTemplateMap);
            //docTemplateMap.put(clazz, docTemplate);
            classMap.put(clazz.getSimpleName(), clazz);
            Method[] methods = clazz.getMethods();
            for(Method m : methods) {
                if (m.getName().startsWith("add")) {
                    docTemplate.addAddMethod(m);
                }
                else if (m.getName().startsWith("get")) {
                    docTemplate.addGetMethod(m);
                }
                else if (m.getName().startsWith("set")) {
                    docTemplate.addSetMethod(m);
                }
            }
            docTemplate.done();
            return docTemplate;
        }
        catch (Exception ex) {
            LOGGER.error("process failed " , ex);
        }
        return null;
    }

    public DocTemplateMap process(String className) throws ClassNotFoundException {
        return process(className, true);
    }

    public DocTemplateMap process(String className, boolean root) throws ClassNotFoundException {
        DocTemplate docTemplate = null;
        Class clazz = Class.forName(className);
        process(clazz, root);
        System.out.println("All done");
        return docTemplateMap;
    }

    public static void main(String[] args) throws Exception {
        Reflector reflector = new Reflector();
        reflector.process("org.openapplications.oagis.x9.SyncLocationDocument");
    }
}
