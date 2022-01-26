package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.entities.*;
import com.example.workingwithtokens.enums.CostTypes;
import com.example.workingwithtokens.enums.SearchTypeMarks;
import com.example.workingwithtokens.parsers.SearchMarks;
import com.example.workingwithtokens.sortingUtils.Sortinger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

// TODO сделать сортировки(
//  для расходов - по типу, по дате, по автомобилю
//  для новостей - по дате добавления, по автору
//  )

// TODO сделать показ  расхода пользователю исходя из величины последней заправки в литра,
//  оставшегося кол-ва топлива или киллометража


//TODO сделать добавление меток пользователя

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

    @RequestMapping("/testtoken")
    public ResponseEntity<String> personal(){
        return responseSuccess("response","Токен валиден");
    }

    //Работа с транспортными средствами пользователя
    @RequestMapping("/vehicle/add")
    public ResponseEntity<String> newCar(Principal principal,
                                         @Valid @RequestParam("mark") String mark,
                                         @Valid @RequestParam("model") String model,
                                         @Valid @RequestParam(value = "generation",required = false) String generation,
                                         @Valid @RequestParam("consumptionCity") Float consumptionCity,
                                         @Valid @RequestParam("consumptionRoute") Float consumptionRoute,
                                         @Valid @RequestParam("consumptionMixed") Float consumptionMixed,
                                         @Valid @RequestParam("fuelCapacity") Float fuelCapacity,
                                         @Valid @RequestParam(value = "licensePlateNumber",required = false) String licensePlateNumber) {
        User user = userService.findByUsername(principal.getName());
        Vehicle vehicle=new Vehicle(mark,model,generation,consumptionCity,consumptionRoute,consumptionMixed,fuelCapacity,licensePlateNumber);
        userVehicleRepository.save(new UserVehicle(vehicleRepository.save(vehicle), user));
        convertAndSendToUserJSON(principal.getName(),"/vehicle","vehicle");
        return responseCreated("status", "Created", "response", vehicle);
    }

    @RequestMapping("/vehicle/all")
    public ResponseEntity<String> allCars(Principal principal,
                                          @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        User byUsername = userService.findByUsername(principal.getName());
        Set<Vehicle> vehicles = byUsername.getVehicles();
        convertAndSendToUserJSON(principal.getName(),"/vehicle","vehicle");
        return responseSuccess("response", Sortinger.sort(Vehicle.class, vehicles, sortBy));
    }
    @RequestMapping("/vehicle/edit/{id}")
    public ResponseEntity<String> editCar(Principal principal,
                                        @PathVariable("id") Long id,
                                          @Valid @RequestParam("mark") String mark,
                                          @Valid @RequestParam("model") String model,
                                          @Valid @RequestParam(value = "generation",required = false) String generation,
                                          @Valid @RequestParam("consumptionCity") Float consumptionCity,
                                          @Valid @RequestParam("consumptionRoute") Float consumptionRoute,
                                          @Valid @RequestParam("consumptionMixed") Float consumptionMixed,
                                          @Valid @RequestParam("fuelCapacity") Float fuelCapacity,
                                          @Valid @RequestParam(value = "licensePlateNumber",required = false) String licensePlateNumber) {
        User user = userService.findByUsername(principal.getName());
        Optional<Vehicle> vehicle = user.getVehicles().stream().filter(e -> e.getId().equals(id)).findFirst();
        if(vehicle.isPresent()){
            vehicleRepository.save(new Vehicle(id,mark,model,generation,consumptionCity,consumptionRoute,consumptionMixed,fuelCapacity,licensePlateNumber));
            convertAndSendToUserJSON(principal.getName(),"/vehicle","vehicle");
            return responseSuccess("response","Транспортное средство обновлено");
        }else{
            return responseBad("response", "Транспортное средство с таким id не найдено");
        }
    }
    @RequestMapping("/vehicle/{id}")
    public ResponseEntity<String> getCar(@PathVariable("id") Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        for (Vehicle v : user.getVehicles())
            if (Objects.equals(v.getId(), id))
                return responseSuccess("response", v);
        return responseBad("response", "Транспортное средство с таким id не найдено");
    }

    @Transactional
    @DeleteMapping("/vehicle/delete/{id}")
    public ResponseEntity<String> deleteCar(@PathVariable("id") Long id,Principal principal){
        User user = userService.findByUsername(principal.getName());
        Optional<Vehicle> vehicle = user.getVehicles().stream().filter(e -> e.getId().equals(id)).findFirst();
        if(vehicle.isPresent()){
            vehicleRepository.deleteVehicleById(vehicle.get().getId());
            convertAndSendToUserJSON(principal.getName(),"/vehicle","vehicle");
            return responseSuccess("response","Транспортное средство успешно удалено");
        }
        else {
            return responseBad("response", "Транспортное средство с таким id не найдено");
        }
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
                return responseBad("response", "Тип должен быть один из GASSTATION,CARWASH,CARSERVICE");
            }
            mark.setUser(user);
            requestMarkRepository.save(mark);
            return responseSuccess("response", "Метка добавлена");
        } else {
            return responseSuccess("response", "Метка уже была добавлена");
        }
    }

    @RequestMapping("mark/get")
    public ResponseEntity<String> getMarks(@RequestParam("lat") Float lat,
                                           @RequestParam("lon") Float lon,
                                           @RequestParam("type") String type) throws ValidationException {
        try {
            SearchTypeMarks.valueOf(type);
        } catch (IllegalArgumentException e) {
            return responseBad("response", "Тип должен быть один из GASSTATION,CARWASH,CARSERVICE");
        }
        SearchMarks sm = new SearchMarks(type, lat, lon);
        return responseSuccess("response", sm.search());
    }

    //Работа с расходами пользователя
    @RequestMapping("/cost/add")
    public ResponseEntity<String> addCost(Principal principal,
                                          @RequestParam("vId") Long vehicleID,
                                          @RequestParam("type") String type,
                                          @RequestParam("value") Float value,
                                          @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date
                                          ) {

        User user = userService.findByUsername(principal.getName());
        Vehicle vehicle1 = vehicleRepository.getById(vehicleID);
        Set<UserVehicle> userVehicles = user.getUserVehicles();
        for (UserVehicle userVehicle : userVehicles) {
            if (userVehicle.getVehicle().equals(vehicle1)) {
                try {
                    Set<VehicleCosts> vehicleCosts = userVehicle.getVehicleCosts();
                    CostTypes.valueOf(type);
                    VehicleCosts cost=new VehicleCosts(type,value,date,userVehicle);
                    vehicleCostsRepository.save(cost);
                    convertAndSendToUserJSON(principal.getName(),"/cost","cost");
                    return responseCreated("response", vehicleCosts);
                } catch (IllegalArgumentException e) {
                    return responseBad("response", "Тип должен быть один из REFUELING,WASHING,SERVICE,OTHER");
                }
            }
        }
        return responseBad("response", "Транспортное средство не найдено");
    }

    @RequestMapping("/cost/all")
    public ResponseEntity<String> allCosts(Principal principal,
                                           @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = user.getCosts();
        return responseSuccess("response", Sortinger.sort(VehicleCosts.class, vehicleCosts, sortBy));
    }

    @RequestMapping("/cost/edit/{id}")
    public ResponseEntity<String> editCost(Principal principal,
                                           @PathVariable("id") Long id,
                                           @RequestParam("vId") Long vehicleID,
                                           @RequestParam("type") String type,
                                           @RequestParam("value") Float value,
                                           @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date){
        User user = userService.findByUsername(principal.getName());
        Optional<VehicleCosts> cost = user.getCosts().stream().filter(c -> c.getCostId().equals(id)).findFirst();
        Vehicle vehicle = vehicleRepository.getById(vehicleID);
        Set<UserVehicle> userVehicles=user.getUserVehicles();
        if(cost.isPresent()){
            for (UserVehicle userVehicle : userVehicles) {
                if (userVehicle.getVehicle().equals(vehicle)) {
                    try {
                        Set<VehicleCosts> vehicleCosts = userVehicle.getVehicleCosts();
                        CostTypes.valueOf(type);
                        VehicleCosts newCost=new VehicleCosts(id,type,value,date,userVehicle);
                        vehicleCostsRepository.save(newCost);
                        convertAndSendToUserJSON(principal.getName(),"/cost","cost");
                        return responseCreated("response", "Расход обновлен");
                    } catch (IllegalArgumentException e) {
                        return responseBad("response", "Тип должен быть один из REFUELING,WASHING,SERVICE,OTHER");
                    }
                }
            }
            return responseBad("response", "Транспортное средство не найдено");
        }else{
            return responseBad("response","Расход с данным id не найден");
        }
    }


    @RequestMapping("/news/all")
    public ResponseEntity<String> allNews(Principal principal,
                                          @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        List<News> newsList = newsRepository.findAll();
        return responseSuccess("response", Sortinger.sort(News.class,newsList,sortBy));
    }
}
