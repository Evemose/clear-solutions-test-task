package org.users.core.validation;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;

/**
 * Configuration for validators.
 * This configuration is required to make custom validators be managed by Spring.
 */
@Configuration
public class ValidatorsConfig {
    @Bean
    public LocalValidatorFactoryBean defaultValidator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    @Lazy
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(LocalValidatorFactoryBean validator) {
        // This is required to make custom validators be managed by Spring.
        // We explicitly set the validator factory to be used by Hibernate.
        return (Map<String, Object> hibernateProperties) ->
                hibernateProperties.put("javax.persistence.validation.factory", validator);
    }
}
