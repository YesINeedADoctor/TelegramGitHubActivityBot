package io.project.SpringTelegramGHActivityBot.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.project.SpringTelegramGHActivityBot.config.IntegrationConfig;
import io.project.SpringTelegramGHActivityBot.data.RequestRepository;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class GHGetRepositoriesService extends IntegrationService {
    private List<RequestRepository> foundRepositories;
    private RequestRepository foundRepository;
    private final IntegrationConfig integrationConfig;

    public GHGetRepositoriesService(IntegrationConfig IntegrationConfig) {
        this.integrationConfig = IntegrationConfig;
    }

    public List<RequestRepository> getFoundRepositories() {
        return this.foundRepositories;
    }

    public RequestRepository getFoundRepository() {
        return this.foundRepository;
    }

    public String getRepositoriesByOwner(String owner) {
//        if (owner.isEmpty()) return "No owner";
        String response = "";
        this.foundRepositories = new ArrayList<>();
        try {
            response = sendRequest(integrationConfig.getRepositoriesByOwnerURL(), "GET", owner);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERR";
        }
        Gson gson = new Gson();
        Type requestRepositoryListType = new TypeToken<ArrayList<RequestRepository>>() {
        }.getType();
        this.foundRepositories = gson.fromJson(response, requestRepositoryListType);
        return "OK";
    }

    public String getRepositoryByFullName(String fullName) {
        String response = "";
        this.foundRepository = null;
        try {
            response = sendRequest(integrationConfig.getRepositoriesByFullNameURL(), "GET", fullName);
        } catch (IOException e) {
            e.printStackTrace();
            return "ERR";
        }
        this.foundRepository = new Gson().fromJson(response, RequestRepository.class);
        return "OK";
    }
}