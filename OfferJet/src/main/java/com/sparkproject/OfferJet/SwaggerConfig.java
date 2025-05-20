package com.sparkproject.OfferJet;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(getInfo())
        	.select()
            .apis(RequestHandlerSelectors.basePackage("com.sparkproject.sparkapp2.controller"))
            .paths(PathSelectors.any())
            .build();
    }

	private ApiInfo getInfo() {
		return new ApiInfo(
				"Dynamic Offer Assignment System with Apache Spark & Spring Batch", 
				"Designed and implemented a scalable customer segmentation engine that dynamically creates batch jobs using Spring Batch and Apache Spark to apply targeted promotional offers based on real-time order data.", 
				"1.0", 
				"Terms of service", 
				new Contact("Ramzan Khan", "www.ramzan.com", "hiramzankhan@gmail.com"), 
				"License of APIs", 
				"API License URL", 
				Collections.emptyList());
	}
    
}
