package org.thingsboard.gateway.extensions.kinesis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.thingsboard.gateway.extensions.kinesis.conf.KinesisConfiguration;
import org.thingsboard.gateway.service.GatewayService;
import org.thingsboard.gateway.util.ConfigurationTools;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ashvayka on 15.05.17.
 */
@Service
@ConditionalOnProperty(prefix = "kinesis", value = "enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class DefaultKinesisService {
    @Autowired
    private GatewayService service;

    @Value("${kinesis.configuration}")
    private String configurationFile;

    private Kinesis broker;

    @PostConstruct
    public void init() throws Exception {
        log.info("Initializing Kinesis service!");
        KinesisConfiguration configuration;
        try {
            configuration = ConfigurationTools.readConfiguration(configurationFile, KinesisConfiguration.class);
        } catch (Exception e) {
            log.error("Kinesis service configuration failed!", e);
            throw e;
        }

        try {
            broker = new Kinesis(service, configuration);
            broker.init();
        } catch (Exception e) {
            log.error("Kinesis service initialization failed!", e);
            throw e;
        }
    }

    @PreDestroy
    public void preDestroy() {
        if (broker != null) {
            broker.stop();
        }
    }

}
