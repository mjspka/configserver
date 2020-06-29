package com.pmalla.configserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMq;
import io.arivera.oss.embedded.rabbitmq.EmbeddedRabbitMqConfig;

/**
 * @author pmalla
 *
 */
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
