package com.example.workingwithtokens.parsers;

import com.example.workingwithtokens.entities.AcceptedMark;
import com.example.workingwithtokens.enums.SearchTypeMarks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SearchMarks {
    private final String targetUrl = "https://search-maps.yandex.ru/v1/";

    private String type;
    private Float lat;
    private Float lon;

    public SearchMarks(String type, Float lat, Float lon) {
        this.type = type;
        this.lat = lat;
        this.lon = lon;
    }

    public List<AcceptedMark> search() throws ValidationException {
        Optional<String> json = fetch();
        if (json.isPresent()) {
            JsonObject main = new JsonParser().parse(json.get()).getAsJsonObject();
            List<AcceptedMark> marks = new ArrayList<>();

            main.get("features").getAsJsonArray().forEach(e -> {
                marks.add(new AcceptedMark(
                        type,
                        e.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsFloat(),
                        e.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray().get(1).getAsFloat(),
                        e.getAsJsonObject().get("properties").getAsJsonObject().get("name").getAsString()
                ));
            });
            return marks;
        }
        return new ArrayList<>();
    }

    private Optional<String> fetch() throws ValidationException {
        String parameters =
                "?text=" + type +
                        "&ll=" + lat + "," + lon +
                        "&spn=2,2" +
                        "&lang=ru_RU" +
                        "&apikey=74fea2d6-b1de-4347-be2d-d13609fd2292";

        HttpURLConnection connection = null;
        try {
            SearchTypeMarks.valueOf(type);
            URL url = new URL(targetUrl + parameters);
            System.out.println(targetUrl+parameters);
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
            return Optional.of(content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Optional.empty();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return Optional.empty();
    }
}
