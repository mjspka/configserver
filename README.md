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
