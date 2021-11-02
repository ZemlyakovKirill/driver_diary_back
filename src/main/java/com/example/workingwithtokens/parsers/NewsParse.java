package com.example.workingwithtokens.parsers;

import com.example.workingwithtokens.entities.News;
import com.example.workingwithtokens.repositories.UserNewsRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@Log
public class NewsParse {

    @Autowired
    private UserNewsRepository newsRepository;

    private final Gson json = new GsonBuilder().setPrettyPrinting().create();
    private final String targetURL = "https://newsapi.org/v2/top-headlines?apiKey=491232aa2bf24e649b0d8a0e7224682a&q=auto&sortBy=popularity&country=ru";
    private HttpURLConnection connection = null;
    public final Logger logger = LoggerFactory.getLogger(NewsParse.class);

    @Async("schedulePool1")
    @Scheduled(fixedRate = 86400000)
    public void updateNews() {
        try {
            logger.info("Parsing news...");
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            JsonObject jsonObject = new JsonParser().parse(content.toString()).getAsJsonObject();
            List<News> news = new ArrayList<>();
            jsonObject
                    .get("articles")
                    .getAsJsonArray()
                    .forEach(e -> {
                        try {
                            news.add(new News(
                                    e.getAsJsonObject().get("title").getAsString(),
                                    e.getAsJsonObject().get("description").getAsString(),
                                    e.getAsJsonObject().get("urlToImage").getAsString(),
                                    e.getAsJsonObject().get("source").getAsJsonObject().get("name").getAsString(),
                                    new SimpleDateFormat("yyyy-mm-dd'T'h:m:s'Z'")
                                            .parse(e.getAsJsonObject().get("publishedAt").getAsString())
                            ));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    });
            newsRepository.deleteAll();
            newsRepository.saveAll(news);
            logger.info("Parsing is over, parsed " + news.size() + " rows");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
