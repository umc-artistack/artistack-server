package com.artistack;

import com.artistack.config.YamlPropertySourceFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
@EnableJpaAuditing
@SpringBootApplication
@PropertySource(value = "classpath:aws.yml", factory = YamlPropertySourceFactory.class)
@PropertySource("classpath:application.properties")
public class ArtistackApplication {

	public static final String APPLICATION_LOCATIONS = "spring.config.location="
		+"classpath:application.properties,"
		+"classpath:aws.yml";

	public static void main(String[] args) {
		new SpringApplicationBuilder(ArtistackApplication.class)
			.properties(APPLICATION_LOCATIONS)
			.run(args);
	}
}
