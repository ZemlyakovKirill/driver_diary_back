package ru.themlyakov.driverdiary.controllers;

import ru.themlyakov.driverdiary.enums.SearchTypeMarks;
import ru.themlyakov.driverdiary.parsers.SearchMarks;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.ValidationException;

@RestController
@Validated
@Api(tags="Пути  меток на карте (подтвержденных)")
public class AcceptedMarkController extends AbstractController{
    @ApiOperation(value = "Получение пользовательских меток")
    @GetMapping("/user/mark/get")
    public ResponseEntity<String> getMarks(@RequestParam("lat") Float lat, @RequestParam("lon") Float lon, @RequestParam("type") String type) throws ValidationException {
        try {
            SearchTypeMarks.valueOf(type);
        } catch (IllegalArgumentException e) {
            return responseBad("response", "Тип должен быть один из GASSTATION,CARWASH,CARSERVICE");
        }
        SearchMarks sm = new SearchMarks(type, lat, lon);
        return responseSuccess("response", sm.search());
    }
}
