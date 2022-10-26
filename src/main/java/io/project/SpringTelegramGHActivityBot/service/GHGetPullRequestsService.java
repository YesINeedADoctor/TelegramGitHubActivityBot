package io.project.SpringTelegramGHActivityBot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.project.SpringTelegramGHActivityBot.config.IntegrationConfig;
import io.project.SpringTelegramGHActivityBot.data.PullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class GHGetPullRequestsService extends IntegrationService {

    private final IntegrationConfig config;

    private PullRequest LastPullRequest;

    private Integer PullRequestsNumber;

    @Autowired
    public GHGetPullRequestsService(IntegrationConfig config) {
        this.config = config;
    }

//    public List<PullRequest> getFoundPullRequests() {
//        return this.foundPullRequests;
//    }

//    public void tstPR() {
//        PullRequest kek = new PullRequest.Builder(0L).setTitle()
//                .setUSER_LOGIN()
//                .setHTML_URL()
//                .setBody()
//                .setCREATED_AT()
//                .setUSER_LOGIN()
//    }

    public PullRequest getLastPullRequestDetails() {
        return this.LastPullRequest;
    }

    public Integer getPullRequestsCount() {
        return this.PullRequestsNumber;
    }

    public String getLastPullRequest(String owner, String repository) {
//        if (owner.isEmpty()) return "No owner";
//        if (repository.isEmpty()) return "No repository";
//        now the limit is 1 - only last pr needed
        String response = "";
        LastPullRequest = null;
        try {
            response = sendRequest(config.getLastPullRequestURL(), "GET", owner, repository);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERR";
        }
        Type requestPullRequestListType = new TypeToken<ArrayList<PullRequest>>() {
        }.getType();
        List<PullRequest> tempList = new Gson().fromJson(response, requestPullRequestListType);
        if (tempList.size() > 0) {
            LastPullRequest = tempList.get(0);
        }
        return "OK";
    }

    public String getPullRequests(String owner, String repository) {
        //        if (owner.isEmpty()) return "No owner";
//        if (repository.isEmpty()) return "No repository";
        String response = "";
        try {
            response = sendRequest(config.getPullRequestsURL(), "GET", owner, repository);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERR";
        }
        Gson gson = new Gson();
        Type requestPullRequestListType = new TypeToken<ArrayList<PullRequest>>() {
        }.getType();
        List<PullRequest> tempList = gson.fromJson(response, requestPullRequestListType);
        this.PullRequestsNumber = tempList.size();
        return "OK";
    }
}