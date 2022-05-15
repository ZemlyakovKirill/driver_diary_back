package ru.themlyakov.driverdiary.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.themlyakov.driverdiary.entities.News;
import ru.themlyakov.driverdiary.utils.PaginationWrapper;
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
    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
    @ApiOperation(value = "Просмотр постранично новостей")
    @GetMapping(value = "/user/news/paged",produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> pagedNews(@RequestParam(value = "sortBy", defaultValue = "author") String sortBy,
                                          @RequestParam(value="page",defaultValue = "0") int page) {
        Pageable pageable=PageRequest.of(page,10,Sort.by(sortBy));
        Page<News> pagedData = newsRepository.findAll(pageable);
        PaginationWrapper wrapper = new PaginationWrapper(pagedData);
        return responseSuccess("response", wrapper);
    }

    @ApiOperation(value = "Просмотр постранично новостей")
    @GetMapping(value = "/user/news/all",produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> allNews() {
        List<News> news = newsRepository.findAll();
        return responseSuccess("response", news);
    }
}
