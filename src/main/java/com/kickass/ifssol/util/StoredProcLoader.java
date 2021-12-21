package com.kickass.ifssol.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kickass.ifssol.config.SolIFSMappingConfigNew;
import com.kickass.ifssol.entity.SolNodesRoot;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class StoredProcLoader {

    private static Logger LOGGER = LogManager.getLogger(StoredProcLoader.class);
    private Map<String,String> storedProcMap = new HashMap<String,String>();
    @Autowired
    private Environment env;

    @PostConstruct
    private void load() {

        String storedProcFolderName = System.getProperty("stored.proc.folder");


        if (StringUtils.isEmpty(storedProcFolderName)) {
            storedProcFolderName = env.getProperty("stored.proc.folder");
        }


        if (StringUtils.isEmpty(storedProcFolderName)) {
            return;
        }

        if (!folderAccessible(storedProcFolderName)) {
            LOGGER.warn("WARNING!!!!!! " + storedProcFolderName + " Does not exist OR not readable");
            return;
        }

        ClassLoader cl = StoredProcLoader.class.getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        //Resource[] resources = resolver.getResources("classpath*:/*.json") ;
        Resource resources[] = null;
        try {
            resources = resolver.getResources("file:" + storedProcFolderName + "/*.sql");
        }
        catch (Exception ex) {
            LOGGER.error(ex);
            return;
        }
        for(Resource resource : resources) {
            String name = resource.getFilename();
            try {
                InputStream is = resource.getInputStream();
                byte[] data = new byte[is.available()];
                is.read(data);
                storedProcMap.put(name,new String(data));
            }
            catch (Exception ex) {
                LOGGER.error("Unable to load the resource " + name, ex);
            }
        }
    }

    private boolean folderAccessible(String folderName) {
        File dir = new File(folderName);
        if (dir.exists() && dir.canRead()) {
            return  true;
        }

        return false;
    }
    public String getStoredProc(SolNodesRoot root) {
        String storedProcName = root.getUpdateStoredproc();
        if (!StringUtils.isEmpty(storedProcName)) {
            return storedProcMap.get(storedProcName);
        }
        return root.getUpdateStatement();
    }
}
