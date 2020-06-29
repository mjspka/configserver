# Spring boot config server example
This repository covers the examples of spring-boot config server and config client

## Config server
This micro-service is designed to store the configurations of config clients. The following steps are required to make the spring boot application act as config server.

* Add the annotation, @EnableConfigServer to the spring-boot application class, ConfigServerApplication.

* Add the dependency called spring-cloud-config-server in the pom.xml File.

```
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

* Run the spring-boot application with the port 8888

> in application.yml file, add the following snippet

```
server:
  port: 8888
```

> If server port information is not provide, by default, the application runs on the port 8080.

* Provide the name for the micro-service in application.yml File

```
spring:
  application:
    name: configserver
```

* Enable File system as backend for config server. The configuration for this goes in application.yml file

```
spring:
  cloud:
    config:
      server:
        native:
          searchLocations: file://${user.home}/config-repo
```

> Everything together, the application.yml looks as follows,

```
server:
  port: 8888

spring:
  profiles:
    active: native
  application:
    name: configserver
  cloud:
    config:
      server:
        native:
          searchLocations: file://${user.home}/config-repo
```

## Config client

* Add the dependency called spring-cloud-starter-config in the pom.xml File.

```
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

* Add the dependency called spring-boot-starter-actuator in the pom.xml File.

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

* Enable @POST /actuator/refresh endpoint

> in bootstrap.yml file, add the following snippet

```
management:
  endpoints:
    web:
      exposure:
        include: info, health, env, refresh
```

* Provide the name for the micro-service in bootstrap.yml File

```
spring:
  application:
    name: configclient
```

* Point to config server in bootstrap.yml File

```
spring:
  cloud:
    config:
      uri: http://localhost:8888
```

> Everything together, it looks as follows

```
spring:
  application:
    name: configclient
  cloud:
    config:
      uri: http://localhost:8888

management:
  endpoints:
    web:
      exposure:
        include: info, health, env, refresh
```

* Add the configuration class to read from the configuration stored at config-server. If the configuration is stored at config-server is changed, one need to call refresh end point of config-client

```
@Component
@ConfigurationProperties(prefix="config.client")
public class Configuration {
	private String property;

	public void setProperty(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

}
```

## Config repo
This is a git repo to store the configuration at the GIT

# How to test?
* Start config ConfigServer application
** Go to URL, http://localhost:8888/configclient/default and check the configuration of config client called configclient
** If the configurations are stored in DB, go to URL, http://localhost:8888/configclient/dev/latest and check the configurations
* Start config client application
** Go to UTL, http://localhost:8080/property and check the value of the property

# How to change the back-end for config server?
* The config server has support for File based system, GIT, Database
```
spring:
  profiles:
    active: native
```
* The profiles
** native refers to File based system
```
spring:
  profiles:
    active: native
  application:
    name: configserver
  cloud:
    config:
      server:
        native:
          searchLocations: file://${user.home}/config-repo
```
** git refers to GIT based system
```
spring:
  profiles:
    active: git
  application:
    name: configserver
  cloud:
    config:
      server:
        git:
          uri: https://github.com/mjspka/configserver.git/config-repo
```
** db refers to jdbc based system
```
spring:
  profiles:
    active: jdbc
  application:
    name: configserver
  cloud:
    config:
      server:
        jdbc:
          sql: "SELECT PROPERTIES.PROP_KEY, PROPERTIES.VALUE from PROPERTIES where APPLICATION=? and PROFILE=? and LABEL=?"
  datasource:
    url: "jdbc:h2:file:./data/demo"
    username: sa
    password: password
    driverClassName: org.h2.Driver
  h2:
    console:
      enabled: true
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
```

> Add the following dependencies in config server pom.xml file to enable database in-memory data support

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>
```

# How to enable client bus?
* In config server micro-service

> Add the following dependency for embedded rabbit MQ server

```
<dependency>
  <groupId>io.arivera.oss</groupId>
  <artifactId>embedded-rabbitmq</artifactId>
  <version>1.4.0</version>
</dependency>
```

> Start embedded rabbit MQ server at the start of the application

```
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		EmbeddedRabbitMqConfig config = new EmbeddedRabbitMqConfig.Builder().build();
		EmbeddedRabbitMq rabbitMq = new EmbeddedRabbitMq(config);
		rabbitMq.start();
	}
}
```
* In config client micro-service
> Add the following dependencies in pom.xml File

```
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```
> Add the MQ server details in bootstrap.yml file
```
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
```
> The advantage of client bus is to propagate the event to refresh the configuration to all the instances of config client micro-service. All we need to do is call the /actuator/refresh event on one of the micro-service event.

# How to send refresh event to config client?

> Invoke the endpoint @POST /actuator/refresh for config client. This is to allow all the configuration with @RefreshScope annotation and @ConfigurationProperties annotation refreshed with the configuration stored at config server.

```
curl -X POST http://localhost:8080/actuator/refresh
```
