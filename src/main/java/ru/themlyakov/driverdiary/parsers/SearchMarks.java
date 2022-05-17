package ru.themlyakov.driverdiary.parsers;

import ru.themlyakov.driverdiary.entities.AcceptedMark;
import ru.themlyakov.driverdiary.enums.SearchTypeMarks;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import javax.xml.bind.ValidationException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SearchMarks {
    private final String targetUrl = "https://search-maps.yandex.ru/v1/";

    private SearchTypeMarks type;
    private Float lat;
    private Float lon;

    public SearchMarks(SearchTypeMarks type, Float lat, Float lon) {
        this.type = type;
        this.lat = lat;
        this.lon = lon;
    }

    public List<AcceptedMark> search() throws ValidationException {
        Optional<InputStreamReader> json = fetch();
        if (json.isPresent()) {
            JsonObject main = new JsonParser().parse(json.get()).getAsJsonObject();
            List<AcceptedMark> marks = new ArrayList<>();

            main.get("features").getAsJsonArray().forEach(e -> {
                marks.add(new AcceptedMark(
                        type,
                        e.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray().get(1).getAsFloat(),
                        e.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsFloat(),
                        e.getAsJsonObject().get("properties").getAsJsonObject().get("name").getAsString()
                ));
            });
            return marks;
        }
        return new ArrayList<>();
    }

    private Optional<InputStreamReader> fetch() throws ValidationException {
        try {
        String parameters =
                "?text=" + URLEncoder.encode(type.search(),"UTF-8") +
                        "&ll=" + lon + "," + lat +
                        "&spn=2,2" +
                        "&lang=ru_RU" +
                        "&apikey=74fea2d6-b1de-4347-be2d-d13609fd2292";
            HttpClient httpClient=HttpClients.createDefault();
            System.out.println(targetUrl+parameters);
            HttpGet httpGet=new HttpGet(targetUrl+parameters);
            HttpResponse response=httpClient.execute(httpGet);
            HttpEntity entity= response.getEntity();
            return Optional.of(new InputStreamReader(entity.getContent()));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
