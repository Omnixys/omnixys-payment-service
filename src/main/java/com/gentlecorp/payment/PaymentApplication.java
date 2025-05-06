package com.gentlecorp.payment;

import com.gentlecorp.payment.config.AppProperties;
import com.gentlecorp.payment.config.ApplicationConfig;
import com.gentlecorp.payment.dev.DevConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.graphql.data.federation.FederationSchemaFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static com.gentlecorp.payment.util.Banner.TEXT;

@SpringBootApplication(proxyBeanMethods = false)
@Import({ApplicationConfig.class, DevConfig.class})
@EnableConfigurationProperties({AppProperties.class})
@EnableJpaRepositories
@EnableWebSecurity
@EnableMethodSecurity
@EnableAsync
@SuppressWarnings({"ClassUnconnectedToPackage"})
public class PaymentApplication {

	public static void main(String[] args) {
		new Env();
		final var app = new SpringApplication(PaymentApplication.class);
		app.setBanner((_, _, out) -> out.println(TEXT));
		app.run(args);
	}

	@Bean
	public GraphQlSourceBuilderCustomizer customizer(FederationSchemaFactory factory) {
		return builder -> builder.schemaFactory(factory::createGraphQLSchema);
	}

	@Bean
	FederationSchemaFactory federationSchemaFactory() {
		return new FederationSchemaFactory();
	}
}
