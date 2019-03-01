package tech.dynamo.processor.record;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = BatchAutoConfiguration.class)
@EnableSwagger2
@ImportResource("classpath*:/*-context.xml")
@EnableCaching
@ComponentScan
public class App {

	public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
    }
	
	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors
						.basePackage("tech.dynamo.processor.record.controller"))
				.paths(regex("/.*")).build();

	}

	@Bean
	public ApiInfo metaData() {
		//redacted
		//apiInfo = /////;
		return null;
	}

}
