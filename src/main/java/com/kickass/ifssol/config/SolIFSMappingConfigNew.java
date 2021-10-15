package com.kickass.ifssol.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.mapper.GenericDataMapperNew;
import com.kickass.ifssol.messaging.MessagePublisher;
import com.kickass.ifssol.service.CronJobNew;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import com.kickass.ifssol.util.reflect.Reflector;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolIFSMappingConfigNew {
    private static Logger LOGGER = LogManager.getLogger(SolIFSMappingConfigNew.class);

    @Autowired
    private Environment env;

    private List<SolNodesRoot> outgoingSolNodesRootList = new ArrayList<>();
    private List<SolNodesRoot> incomingSolNodesRootList = new ArrayList<>();
    private Map<String,SolNodesRoot> incomingSolNodesRootsByQueueNameMap = new HashMap<>();
    private Map<String,SolNodesRoot> solNodesRootsByNameMap = new HashMap<>();


    private ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    @Autowired
    private GenericDataMapperNew genericDataMapper;

    @Autowired
    private CommonDataAccessor commonDataAccessor;

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    private Reflector reflector;

    @PostConstruct
    void setup() {
        try {
            configureThreadPoolTaskScheduler();
            loadMappings();
            schedule();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load the transaction.properties", ex);
        }
    }

    private void configureThreadPoolTaskScheduler() {
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        threadPoolTaskScheduler.initialize();
    }

    void loadMappings() throws  IOException {
        String incomingFolderName = System.getProperty("incoming.mappings.folder");
        String outgoingFolderName = System.getProperty("outgoing.mappings.folder");

        if (StringUtils.isEmpty(incomingFolderName)) {
            incomingFolderName = env.getProperty("incoming.mappings.folder");
        }
        if (StringUtils.isEmpty(outgoingFolderName)) {
            outgoingFolderName = env.getProperty("outgoing.mappings.folder");
        }

        if (StringUtils.isEmpty(incomingFolderName)) {
            LOGGER.warn("No incoming.mappings.folder specified, use -Dincoming.mappings.folder option to speficy the location of incoming mapping jsons");
        }
        if (StringUtils.isEmpty(outgoingFolderName)) {
            LOGGER.warn("No outgoing.mappings.folder specified, use -Doutgoing.mappings.folder option to speficy the location of incoming mapping jsons");
        }

        loadMappings(outgoingFolderName, outgoingSolNodesRootList);

        loadMappings(incomingFolderName, incomingSolNodesRootList);

        for(SolNodesRoot incoming : incomingSolNodesRootList) {
            incomingSolNodesRootsByQueueNameMap.put(incoming.getReceiveQueue(), incoming);
            solNodesRootsByNameMap.put(incoming.getName(), incoming);

        }

        for(SolNodesRoot outgoing : outgoingSolNodesRootList) {
            solNodesRootsByNameMap.put(outgoing.getName(), outgoing);
        }
    }

    public SolNodesRoot getByName(String name) {
        return solNodesRootsByNameMap.get(name);
    }

    public SolNodesRoot getByQueueName(String queueName) {
       return incomingSolNodesRootsByQueueNameMap.get(queueName);
    }

    public List<SolNodesRoot> getOutgoingSolNodesRootList() {
        return  outgoingSolNodesRootList;
    }

    public List<SolNodesRoot> getIncomingSolNodesRootList() {
        return  incomingSolNodesRootList;
    }

    void loadMappings(String folderName, List<SolNodesRoot> solNodesRootList) throws IOException {
        ClassLoader cl = SolIFSMappingConfigNew.class.getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        //Resource[] resources = resolver.getResources("classpath*:/*.json") ;

        Resource resources[] = resolver.getResources("file:"+ folderName + "/*.json");
        for(Resource resource : resources) {
            String name = resource.getFilename();
            try {
                InputStream is = resource.getInputStream();
                ObjectMapper objectMapper = new ObjectMapper();
                SolNodesRoot solNodesRoot = objectMapper.readValue(is, SolNodesRoot.class);
                solNodesRootList.add(solNodesRoot);
            }
            catch (Exception ex) {
                LOGGER.error("Unable to load the resource " + name, ex);
            }
        }

    }



    private void schedule() {
        for(SolNodesRoot solNodesRoot : outgoingSolNodesRootList) {
            String cronExpression = solNodesRoot.getCronExpression();

            if (!solNodesRoot.isEnabled() || !solNodesRoot.isScheduleJob()) {
                continue;
            }

            DocTemplateMap docTemplateMap = null;

            try {
                docTemplateMap = reflector.process(solNodesRoot.getRootClass());
            } catch (ClassNotFoundException e) {
                LOGGER.error("Unable to schedule the job for " + solNodesRoot.getName(), e);
                throw new RuntimeException("Unable to schedule the job for " + solNodesRoot.getName() + ",Class Not Found for : " + solNodesRoot.getRootClass(), e);
            }

            if (!StringUtils.isEmpty(cronExpression)) {
                CronTrigger cronTrigger = new CronTrigger(cronExpression);
                CronJobNew cronJob = new CronJobNew(commonDataAccessor, messagePublisher, solNodesRoot, docTemplateMap, genericDataMapper);
                LOGGER.info("Scheduling " + solNodesRoot.getName() + ", cron expression " + cronExpression);
                threadPoolTaskScheduler.schedule(cronJob, cronTrigger);
            }
        }
    }

}
