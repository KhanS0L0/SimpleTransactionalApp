package com.example.config;

import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(
        basePackages = "com.example.repository.customer",
        entityManagerFactoryRef = "customerEntityManager"
)
public class CustomerConfig {

    private final JpaVendorAdapter adapter;

    @Autowired
    public CustomerConfig(JpaVendorAdapter adapter) {
        this.adapter = adapter;
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(
                adapter,
                additionalJpaProperties(),
                null
        );
    }

    @Primary
    @Bean(name = "customerEntityManager")
    @DependsOn("transactionManager")
    public LocalContainerEntityManagerFactoryBean customerEntityManager() throws Throwable {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setJtaDataSource(customerDataSource(customerDataSourceProperties()));
        entityManager.setJpaVendorAdapter(adapter);
        entityManager.setPackagesToScan("com.example.domain.customer");
        entityManager.setPersistenceUnitName("customerPersistenceUnit");
        entityManager.setJpaPropertyMap(additionalJpaProperties());
        return entityManager;
    }

    @Primary
    @Bean("customerDataSourceProperties")
    @ConfigurationProperties(prefix = "customer.datasource")
    public DataSourceProperties customerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean("customerDataSource")
    public DataSource customerDataSource(@Qualifier("customerDataSourceProperties") DataSourceProperties customerDataSourceProperties) {
        PGXADataSource ds = new PGXADataSource();
        ds.setUrl(customerDataSourceProperties.getUrl());
        ds.setUser(customerDataSourceProperties.getUsername());
        ds.setPassword(customerDataSourceProperties.getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName("XACustomerDataSource");
        return xaDataSource;
    }

    private Map<String, ?> additionalJpaProperties() {
        Map<String, String> map = new HashMap<>();
        map.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        map.put("hibernate.show_sql", "true");
        map.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        map.put("hibernate.transaction.jta.platform", "com.atomikos.icatch.jta.hibernate4.AtomikosPlatform");
        map.put("javax.persistence.transactionType", "JTA");
        return map;
    }
}
