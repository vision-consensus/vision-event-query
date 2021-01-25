package org.vision.eventquery;

import static org.vision.common.utils.LogConfig.LOG;
import static org.vision.eventquery.contractevents.ContractEventController.isRunRePushThread;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;


@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@ServletComponentScan("org.vision.eventquery.filter") //Scan Filter
@PropertySource(value = {"file:./config.conf"}, ignoreResourceNotFound = true)
public class VisionEventApplication {

  public static void main(String[] args) {
    SpringApplication.run(VisionEventApplication.class, args);
    shutdown();
  }
  
  @Bean
  public MongoTemplate mongoTemplate(
      @Value("${mongo.host}")String mongodbIp, @Value("${mongo.dbname}")String mongodbDbName,
      @Value("${mongo.connectionsPerHost}")int connectionsPerHost,
      @Value("${mongo.threadsAllowedToBlockForConnectionMultiplier}")
          int threadsAllowedToBlockForConnectionMultiplier,
      @Value("${mongo.port}")int port, @Value("${mongo.username}")String username,
      @Value("${mongo.password}")String password
  ) {
    MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(connectionsPerHost)
        .threadsAllowedToBlockForConnectionMultiplier(
            threadsAllowedToBlockForConnectionMultiplier).build();
    String host = mongodbIp;
    ServerAddress serverAddress = new ServerAddress(host, port);
    List<ServerAddress> addrs = new ArrayList<ServerAddress>();
    addrs.add(serverAddress);
    MongoCredential credential = MongoCredential.createScramSha1Credential(username, mongodbDbName,
        password.toCharArray());
    List<MongoCredential> credentials = new ArrayList<MongoCredential>();
    credentials.add(credential);
    MongoClient mongo = new MongoClient(addrs, credential, options);

    return new MongoTemplate(mongo, mongodbDbName);

  }

  @Bean
  public ConfigurableServletWebServerFactory webServerFactory() {
    TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
    factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
      @Override
      public void customize(Connector connector) {
        connector.setProperty("relaxedQueryChars", "|{}[]");
      }
    });
    return factory;
  }

  public static void shutdown() {
    Runnable stopThread =
        () -> isRunRePushThread.set(false);
    LOG.info("********register application shutdown hook********");
    Runtime.getRuntime().addShutdownHook(new Thread(stopThread));
  }
}
