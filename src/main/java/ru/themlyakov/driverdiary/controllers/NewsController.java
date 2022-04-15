package ru.themlyakov.driverdiary.controllers;

import ru.themlyakov.driverdiary.entities.News;
import ru.themlyakov.driverdiary.sortingUtils.Sortinger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@Api(tags = "Пути новостей")
public class NewsController extends AbstractController {
    @ApiOperation(value = "Просмотр всех новостей")
    @GetMapping("/user/news/all")
    public ResponseEntity<String> allNews(@RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        List<News> newsList = newsRepository.findAll();
        return responseSuccess("response", Sortinger.sort(News.class, newsList, sortBy));
    }
}
