package ru.themlyakov.driverdiary.controllers;

import ru.themlyakov.driverdiary.entities.AcceptedMark;
import ru.themlyakov.driverdiary.entities.RequestMark;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@Api(tags="Пути  меток на карте (подтвержденных)")
public class AcceptedMarkController extends AbstractController{
    @ApiOperation(value = "Получение пользовательских меток")
    @GetMapping("/user/mark/get")
    public ResponseEntity<String> getMarks(@RequestParam("lat") Float lat, @RequestParam("lon") Float lon, @RequestParam("type") String type) throws ValidationException {
        try {
            SearchTypeMarks typeEnum = SearchTypeMarks.valueOf(type);
            SearchMarks sm = new SearchMarks(typeEnum, lat, lon);
            List<AcceptedMark> responseMarks = sm.search();
            responseMarks.addAll(acceptedMarkRepository.getAcceptedMarkByTypeAndLatAndLon(type,lat,lon));
            List<RequestMark> requestMarks = requestMarkRepository.getRequestMarksInRadius(lat, lon, type);
            Map<String,Object> response=Map.of(
                    "accepted",responseMarks,
                    "requested",requestMarks
            );
            return responseSuccess("response", response);
        } catch (IllegalArgumentException e) {
            return responseBad("response", "Тип должен быть один из GAS,WASH,SERVICE,METHANE,CHARGE");
        }
    }
}
