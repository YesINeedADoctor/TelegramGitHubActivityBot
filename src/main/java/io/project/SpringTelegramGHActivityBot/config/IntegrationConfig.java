package io.project.SpringTelegramGHActivityBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class IntegrationConfig {

    @Value("${integration.eachdaycommitsservice.url}")
    String eachDayCommitsURL;

    @Value("${integration.repositoriesbyownerservice.url}")
    String repositoriesByOwnerURL;

    @Value("${integration.repositoriesbyfullnameservice.url}")
    String repositoriesByFullNameURL;

    @Value("${integration.weeklyparticipationservice.url}")
    String weeklyParticipationURL;

    @Value("${integration.pullrequestsservice.url}")
    String pullRequestsURL;

    @Value("${integration.lastpullrequestservice.url}")
    String lastPullRequestURL;
}
