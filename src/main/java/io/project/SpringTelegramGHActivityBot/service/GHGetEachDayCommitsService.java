package io.project.SpringTelegramGHActivityBot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.project.SpringTelegramGHActivityBot.config.IntegrationConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class GHGetEachDayCommitsService extends IntegrationService {
    private Integer totalCommits;
    private final IntegrationConfig integrationConfig;

    public GHGetEachDayCommitsService(IntegrationConfig integrationConfig) {
        this.integrationConfig = integrationConfig;
    }

//    public int getTodayCommitsCount() {
//        this.todayCommits = 0;
//        int currentGHDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
//        this.eachDayCommits.forEach((n) -> {
//            if (n.get(0).equals(currentGHDayOfWeek)) this.todayCommits += n.get(2);
//        });
//        return todayCommits;
//    }

    public int getTotalCommitsCount() {
        return this.totalCommits;
    }

    public String getEachDayCommits(String owner, String repository) {
//        if (owner.isEmpty()) return "No owner";
//        if (repository.isEmpty()) return "No repository";
        String response = "";
        this.totalCommits = 0;
        try {
            response = sendRequest(integrationConfig.getEachDayCommitsURL(), "GET", owner, repository);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERR";
        }
        Type requestRepositoryListType = new TypeToken<ArrayList<ArrayList<Integer>>>() {
        }.getType();
        List<List<Integer>> eachDayCommits = new Gson().fromJson(response, requestRepositoryListType);
        eachDayCommits.forEach((n) -> this.totalCommits += n.get(2));
        return "OK";
    }
}