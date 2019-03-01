package com.satish.spectrum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@SpringBootApplication
@EnableWebSecurity
@ComponentScan(basePackages = { "com.satish.spectrum" })
@EnableAutoConfiguration
public class SpectrumServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpectrumServiceApplication.class, args);
	}

	@Bean
	@Autowired
	public CacheManager cacheManager() {
		return new EhCacheCacheManager(ehCacheCacheManager().getObject());
	}

	@Bean
	@Autowired
	public EhCacheManagerFactoryBean ehCacheCacheManager() {
		EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
		cmfb.setConfigLocation(new ClassPathResource("spectrum-cache-config.xml"));
		cmfb.setShared(true);
		return cmfb;
	}

	@Bean
	public FilterRegistrationBean fbTokenFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		FbTokenFilter filter = new FbTokenFilter();
		filterRegistrationBean.setFilter(filter);
		return filterRegistrationBean;
	}
}
