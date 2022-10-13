package io.project.SpringTelegramGHActivityBot.service;

import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class IntegrationService {
    public String sendRequest(String URL, String method, Object... queryArgs) throws IOException  {
        log.info("[Integration Service] is invoked");
        java.net.URL url = new URL(String.format(URL, queryArgs));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        String inputLine;
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        con.disconnect();
        return response.toString();
    }
}