package io.project.SpringTelegramGHActivityBot.service;

import io.project.SpringTelegramGHActivityBot.config.IntegrationConfig;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GHGetWeeklyParticipationService extends IntegrationService {

    public Integer lastWeekCommitsNumber;
    private final IntegrationConfig integrationConfig;

    @Autowired
    public GHGetWeeklyParticipationService(IntegrationConfig integrationConfig) {
        this.integrationConfig = integrationConfig;
    }

    public int getLastWeekCommitsCount() {
        return this.lastWeekCommitsNumber;
    }

    public String getWeeklyParticipation(String owner, String repository) {
//        if (owner.isEmpty()) return "No owner";
//        if (repository.isEmpty()) return "No repository";
//        String response = sendRequest("https://api.github.com/repos/%s/%s/stats/participation", "GET", owner, repository);
        String response = "";
        this.lastWeekCommitsNumber = 0;
        try {
            response = sendRequest(integrationConfig.getWeeklyParticipationURL(), "GET", owner, repository);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERR";
        }
        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.has("all") && jsonObject.getJSONArray("all").length() > 0) {
            this.lastWeekCommitsNumber = (int) jsonObject.getJSONArray("all")
                    .get(jsonObject.getJSONArray("all").length() - 1);
        }
        return "OK";
    }
}
