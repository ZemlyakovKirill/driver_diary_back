package com.example.workingwithtokens.parsers;

import com.example.workingwithtokens.controllers.AbstractController;
import com.example.workingwithtokens.entities.News;
import com.example.workingwithtokens.repositories.UserNewsRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.java.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@Log
public class NewsParse extends AbstractController {

    @Autowired
    private UserNewsRepository newsRepository;


    private final Gson json = new GsonBuilder().setPrettyPrinting().create();
    public final Logger logger = LoggerFactory.getLogger(NewsParse.class);

    @Async("schedulePool1")
    @Scheduled(fixedRate = 20_000)
    public void updateNews() {
        try {
            logger.info("Parsing news...");
            HttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://newsapi.org/v2/top-headlines" +
                    "?apiKey=491232aa2bf24e649b0d8a0e7224682a" +
                    "&sortBy=popularity" +
                    "&country=ru");

            HttpResponse response = client.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();

            if (httpEntity != null) {
                try (InputStream instream = httpEntity.getContent()) {
                    JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(instream)).getAsJsonObject();
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
                    convertAndSendJSON("/topic/news","news");
                    newsRepository.saveAll(news);

                    logger.info("Parsing is over, parsed " + news.size() + " rows");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
