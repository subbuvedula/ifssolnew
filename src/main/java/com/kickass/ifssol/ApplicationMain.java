package com.kickass.ifssol;


import com.kickass.ifssol.mapper.response.SyncLocationResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
@PropertySource("application-${spring.profiles.active}.properties")
@SpringBootApplication(exclude = {
		EmbeddedServletContainerAutoConfiguration.class,
		WebMvcAutoConfiguration.class}
)
@EnableScheduling
@EnableJms
public class ApplicationMain {
	private  final static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationMain.class);

	@Autowired
	private SyncLocationResponseMapper syncLocationMapper;

	@PostConstruct
	private void init() {
		//RecordCollection recordCollection = locationSyncDataAccessor.getData();
		//SyncLocationType map = syncLocationMapper.map(recordCollection);
	}

	private final static Object[] CONFIGS = { // @formatter:off
			ApplicationMain.class,
	};
	
	

	//@Override
	//protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
	//	return application.sources(CONFIGS).initializers(new MyApplicationContextInitializer());
	//}

	public static void main(final String[] args) {
		try {
			final SpringApplication springApplication = new SpringApplication(CONFIGS);
			springApplication.setWebEnvironment(false);
			springApplication.addInitializers(new MyApplicationContextInitializer());
			springApplication.run(args);
		}
		catch(Throwable t) {
			log.error("Unable to start the server " ,t );
		}
	}

}
