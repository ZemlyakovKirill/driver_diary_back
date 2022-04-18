package ru.themlyakov.driverdiary.controllers;

import ru.themlyakov.driverdiary.entities.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;

@Api(tags = "Пути пользователя")
@RestController
@RequestMapping(produces = "application/json")
public class UserController extends AbstractController {

    // Персональная инфрмация пользователя
    @ApiOperation(value = "Посмотреть информацию пользователя")
    @GetMapping("/user/personal")
    public ResponseEntity<String> personal(Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        return responseSuccess("response", user);
    }

    @ApiOperation(value = "Редактирование персональной информации пользователя")
    @Transactional
    @PutMapping("/user/personal/edit")
    public ResponseEntity<String> editUser(Principal principal, @Valid @RequestParam(value = "username") String username, @Valid @RequestParam(value = "email") String email, @Valid @RequestParam(value = "fname") String firstName, @Valid @RequestParam(value = "lname") String lastName, @Valid @RequestParam(value = "phone", required = false) String phone) {
        User user = userService.findByUsername(principal.getName());
        if (user.getGoogle() || user.getVk()) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setTelnum(phone);
            userService.save(user);
            convertAndSendToUserJSON(principal.getName(), "/personal", "personal");
            return responseSuccess("response", "Данные пользователя обновлены");
        } else {
            if (principal.getName().equals(username) || (userService.findByUsername(username) == null && userService.findByEmail(email) == null)) {
                user.setUsername(username);
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setTelnum(phone);
                userService.save(user);
                convertAndSendToUserJSON(principal.getName(), "/personal", "personal");
                return responseSuccess("Данные пользователя обновлены");
            } else {
                return responseBad("response", "Пользователь c такими данными уже существует");
            }
        }
    }

    @ApiOperation(value = "Проверка токена доступа")
    @GetMapping("/user/testtoken")
    public ResponseEntity<String> personal() {
        return responseSuccess("response", "Токен валиден");
    }
}
