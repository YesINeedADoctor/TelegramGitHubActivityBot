package io.project.SpringTelegramGHActivityBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Data
@Repository
public class JdbcConfig {

    @Value("${spring.datasource.url}")
    String dbURL;

    @Value("${spring.datasource.username}")
    String dbUser;

    @Value("${spring.datasource.password}")
    String dbPassword;
}