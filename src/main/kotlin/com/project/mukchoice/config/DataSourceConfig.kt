package com.project.mukchoice.config

import com.zaxxer.hikari.HikariDataSource
import lombok.RequiredArgsConstructor
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.util.*
import javax.sql.DataSource

@Configuration
@RequiredArgsConstructor
class DataSourceConfig(
    private val globalPropertySource: GlobalPropertySource
) {
    @Bean
    fun mukchoiceDatasource(): DataSource {
        return DataSourceBuilder
            .create()
            .type(HikariDataSource::class.java)
            .driverClassName(globalPropertySource.driverClassName)
            .url(globalPropertySource.url)
            .username(globalPropertySource.username)
            .password(globalPropertySource.password)
            .build()
    }

    private fun additionalProperties(): Properties {
        val properties = Properties()
        properties.setProperty("hibernate.hbm2ddl.auto", "validate")
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")

        val activeProfile = System.getProperty("spring.profiles.active")
        if (activeProfile == "prod") {
            properties.setProperty("hibernate.show_sql", "false")
            properties.setProperty("hibernate.format_sql", "false")
        } else {
            properties.setProperty("hibernate.show_sql", "true")
            properties.setProperty("hibernate.format_sql", "true")
        }
        properties.setProperty("hibernate.use_sql_comments", "false")
        return properties
    }

    @Bean
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.dataSource = mukchoiceDatasource()
        entityManagerFactoryBean.setPackagesToScan(*arrayOf("com.project.mukchoice.model/**/**"))
        entityManagerFactoryBean.persistenceUnitName = "mukChoicePersist"

        val vendorAdapter: JpaVendorAdapter = HibernateJpaVendorAdapter()
        entityManagerFactoryBean.jpaVendorAdapter = vendorAdapter
        entityManagerFactoryBean.setJpaProperties(additionalProperties())

        return entityManagerFactoryBean
    }
}