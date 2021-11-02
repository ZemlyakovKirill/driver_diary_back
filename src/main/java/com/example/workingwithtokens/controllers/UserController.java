package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.entities.*;
import com.example.workingwithtokens.enums.CostTypes;
import com.example.workingwithtokens.enums.SearchTypeMarks;
import com.example.workingwithtokens.parsers.SearchMarks;
import com.example.workingwithtokens.sortingUtils.Sortinger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

// TODO сделать сортировки(
//  для расходов - по типу, по дате, по автомобилю
//  для новостей - по дате добавления, по автору
//  )

// TODO сделать показ  расхода пользователю исходя из величины последней заправки в литра,
//  оставшегося кол-ва топлива или киллометража

//TODO сделать поиск меток заправок/серввис-центров/детейлинг-центров

//TODO сделать добавление меток пользователя

//TODO(Протестить безопасность JWT-токена)

//TODO почистить код и ненужные методы/классы
@RestController
@RequestMapping(value = "/user", produces = "application/json")
public class UserController extends AbstractController {


    // Персональная инфрмация пользователя
    @RequestMapping("/personal")
    public ResponseEntity<String> personal(Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        return responseSuccess("response", user);
    }

    //Работа с транспортными средствами пользователя
    @RequestMapping("/vehicle/add")
    public ResponseEntity<String> newCar(Principal principal,
                                         @Valid @RequestBody Vehicle vehicle) {
        User user = userService.findByUsername(principal.getName());
        userVehicleRepository.save(new UserVehicle(vehicleRepository.save(vehicle), user));
        return responseCreated("status", "Created", "response", vehicle);
    }

    @RequestMapping("/vehicle/all")
    public ResponseEntity<String> allCars(Principal principal,
                                          @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        User byUsername = userService.findByUsername(principal.getName());
        Set<Vehicle> vehicles = byUsername.getVehicles();
        return responseSuccess("response", Sortinger.sort(Vehicle.class, vehicles, sortBy));
    }

    @RequestMapping("/vehicle/{id}")
    public ResponseEntity<String> getCar(@PathVariable("id") Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        for (Vehicle v : user.getVehicles())
            if (v.getId() == id)
                return responseSuccess("response", v);
        return responseBad("message", "NOT FOUND");
    }


    //Работа с метками на карте
    @RequestMapping("/mark/set")
    public ResponseEntity<String> setMark(Principal principal,
                                          @RequestBody RequestMark mark) {
        User user = userService.findByUsername(principal.getName());

        int count = requestMarkRepository.getMarksByUser(mark.getLat(), mark.getLon(), user.getId());
        if (count == 0) {
            try {
                SearchTypeMarks.valueOf(mark.getType());
            } catch (IllegalArgumentException e) {
                return responseBad("response", "Type has to be like GASSTATION,CARWASH,CARSERVICE");
            }
            mark.setUser(user);
            requestMarkRepository.save(mark);
            return responseSuccess("response", "Mark Added");
        } else {
            return responseSuccess("response", "Mark already exists");
        }
    }

    @RequestMapping("mark/get")
    public ResponseEntity<String> getMarks(@RequestParam("lat") Float lat,
                                           @RequestParam("lon") Float lon,
                                           @RequestParam("type") String type) throws ValidationException {
        try {
            SearchTypeMarks.valueOf(type);
        } catch (IllegalArgumentException e) {
            return responseBad("response", "Type has to be like GASSTATION,CARWASH,CARSERVICE");
        }
        SearchMarks sm = new SearchMarks(type, lat, lon);
        return responseSuccess("response", sm.search());
    }


    @RequestMapping("/cost/add")
    public ResponseEntity<String> addCost(Principal principal,
                                          @RequestParam("vId") Long vehicle,
                                          @RequestBody VehicleCosts vehicleCosts) {

        User user = userService.findByUsername(principal.getName());
        Vehicle vehicle1 = vehicleRepository.getById(vehicle);

        Set<UserVehicle> userVehicles = user.getUserVehicles();
        for (UserVehicle userVehicle : userVehicles) {
            if (userVehicle.getVehicle().equals(vehicle1)) {
                try {
                    Set<VehicleCosts> vehicleCosts1 = userVehicle.getVehicleCosts();
                    CostTypes.valueOf(vehicleCosts.getType());
                    vehicleCosts1.add(vehicleCosts);
                    vehicleCosts.setUserVehicle(userVehicle);
                    vehicleCostsRepository.save(vehicleCosts);
                    return responseCreated("response", vehicleCosts);
                } catch (IllegalArgumentException e) {
                    return responseBad("response", "Type has to be like REFUELING,WASHING,SERVICE,OTHER");
                }
            }
        }
        return responseBad("response", "Something goes wrong");
    }

    @RequestMapping("/cost/all")
    public ResponseEntity<String> allCosts(Principal principal,
                                           @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = new HashSet<>();
        for (UserVehicle userVehicle : user.getUserVehicles()) {
            if (userVehicle.getVehicleCosts() != null) {
                vehicleCosts.addAll(userVehicle.getVehicleCosts());
            }
        }
        return responseSuccess("response", Sortinger.sort(VehicleCosts.class, vehicleCosts, sortBy));
    }
}
