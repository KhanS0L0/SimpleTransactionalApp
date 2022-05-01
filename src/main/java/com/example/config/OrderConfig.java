package com.example.config;

import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@DependsOn("transactionManager")
@EnableJpaRepositories(
        basePackages = "com.example.repository.order",
        entityManagerFactoryRef = "orderEntityManager"
)
public class OrderConfig {

    private final JpaVendorAdapter adapter;

    @Autowired
    public OrderConfig(JpaVendorAdapter adapter) {
        this.adapter = adapter;
    }

    @Bean(name = "orderEntityManager")
    public LocalContainerEntityManagerFactoryBean orderEntityManager() throws Throwable {
        LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
        entityManager.setJtaDataSource(orderDataSource(orderDataSourceProperties()));
        entityManager.setJpaVendorAdapter(adapter);
        entityManager.setPackagesToScan("com.example.domain.order");
        entityManager.setPersistenceUnitName("orderPersistenceUnit");
        entityManager.setJpaPropertyMap(additionalJpaProperties());
        return entityManager;
    }

    @Bean("orderDataSourceProperties")
    @ConfigurationProperties(prefix = "order.datasource")
    public DataSourceProperties orderDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("orderDataSource")
    public DataSource orderDataSource(@Qualifier("orderDataSourceProperties") DataSourceProperties orderDataSourceProperties) {
        PGXADataSource ds = new PGXADataSource();
        ds.setUrl(orderDataSourceProperties.getUrl());
        ds.setUser(orderDataSourceProperties.getUsername());
        ds.setPassword(orderDataSourceProperties.getPassword());

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(ds);
        xaDataSource.setUniqueResourceName("XAOrderDataSource");
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
