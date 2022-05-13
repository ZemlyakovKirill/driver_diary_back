package ru.themlyakov.driverdiary.parsers;

import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import ru.themlyakov.driverdiary.entities.News;
import ru.themlyakov.driverdiary.repositories.UserNewsRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Log
public class NewsParse {

    @Autowired
    SimpMessagingTemplate template;

    @Autowired
    private UserNewsRepository newsRepository;


    private final Gson json = new GsonBuilder().setPrettyPrinting().create();
    public final Logger logger = LoggerFactory.getLogger(NewsParse.class);
    private static final NewsApiClient newsApiClient=new NewsApiClient("491232aa2bf24e649b0d8a0e7224682a");
    private static final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'h:m:s'Z'");

    @Async("schedulePool1")
    @Scheduled(fixedRate = 1_200_000)
    public void updateNews() {
            logger.info("Parsing news...");
            newsApiClient.getTopHeadlines(
                    new TopHeadlinesRequest.Builder()
                            .q("vehicle")
                            .language("ru")
                            .build(),
                    new NewsApiClient.ArticlesResponseCallback() {
                        @Override
                        public void onSuccess(ArticleResponse articleResponse) {
                            List<News> newsList = new ArrayList<>();
                            for (Article a: articleResponse.getArticles()) {
                                try {
                                    newsList.add(
                                            new News(
                                                    a.getTitle(),
                                                    a.getDescription(),
                                                    a.getUrlToImage(),
                                                    a.getAuthor(),
                                                    dateFormat.parse(a.getPublishedAt())
                                            )
                                    );
                                } catch (ParseException e) {
                                    logger.warn("Date parse exception");
                                }
                            }
                            logger.info(newsList.toString());
                            newsRepository.deleteAll();
                            newsRepository.saveAll(newsList);
                            convertAndSendJSON("/topic/news", "news");
                            logger.info("Parsing is over, parsed " + newsList.size() + " rows");
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            logger.error("Parsing failure",throwable);
                        }
                    }
            );
    }
    private void convertAndSendJSON(String destination,Object payload){
        Map<String, Object> responseMap = new HashMap<>();
        Map<String,Object> headers=new HashMap<>();
        headers.put("status",200);
        responseMap.put("response",payload);
        template.convertAndSend(destination,json.toJson(responseMap),headers);
    }
}
