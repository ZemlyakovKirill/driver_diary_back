package ru.themlyakov.driverdiary.controllers;

import ru.themlyakov.driverdiary.entities.RequestMark;
import ru.themlyakov.driverdiary.entities.User;
import ru.themlyakov.driverdiary.entities.UserRequestMark;
import ru.themlyakov.driverdiary.enums.SearchTypeMarks;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Optional;


@RestController
@Validated
@Api(tags="Пути пользовательских меток на карте (неподтвержденных)")
public class RequestMarkController extends AbstractController{
    //Работа с метками на карте
    @ApiOperation(value = "Добавление пользовательской метки на карте")
    @Transactional
    @PostMapping("/user/mark/set")
    public ResponseEntity<String> setMark(Principal principal, @RequestParam("lat") Float lat, @RequestParam("lon") Float lon, @RequestParam("type") String type, @RequestParam("name") String name) {
        User user = userService.findByUsername(principal.getName());
        try {
            SearchTypeMarks.valueOf(type);
            RequestMark mark = requestMarkRepository.getRequestMarkByLatAndLonAndType(lat, lon, type);
            if (mark != null) {
                return responseBad("response", "Подтвердите метку");
            } else {
                synchronized (monitor) {
                    RequestMark newMark = new RequestMark(type, lat, lon, name);
                    requestMarkRepository.save(newMark);
                    userRequestMarkRepository.save(new UserRequestMark(user, newMark, true));
                    return responseSuccess("response", "Метка сохранена, ожидайте подтверждения");
                }
            }
        } catch (IllegalArgumentException e) {
            return responseBad("response", "Тип должен быть один из GASSTATION,CARWASH,CARSERVICE");
        }
    }

    @ApiOperation(value = "Подтверждение метки")
    @PostMapping("/user/mark/confirm/{id}")
    public ResponseEntity<String> confirmMark(Principal principal,
                                              @PathVariable("id") Long id,
                                              @RequestParam("isTruth") boolean isTruth) {
        User user = userService.findByUsername(principal.getName());
        try {
            RequestMark mark = requestMarkRepository.getById(id);
            Optional<RequestMark> userMark = user.getUserRequestMarks().stream()
                    .map(UserRequestMark::getRequestMark)
                    .filter(requestMark -> requestMark.getId().equals(id)).findAny();
            if (userMark.isPresent()) {
                return responseSuccess("response", "Метка уже была подтверждена");
            } else {
                userRequestMarkRepository.save(new UserRequestMark(user, mark, isTruth));
                return responseSuccess("response", "Решение добавлено");
            }
        } catch (EntityNotFoundException ignored) {
            return responseBad("response", "Метка с таким ID не найдена");
        }
    }
}
