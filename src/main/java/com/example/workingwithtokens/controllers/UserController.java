package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.entities.*;
import com.example.workingwithtokens.enums.CostTypes;
import com.example.workingwithtokens.enums.SearchTypeMarks;
import com.example.workingwithtokens.parsers.SearchMarks;
import com.example.workingwithtokens.sortingUtils.Sortinger;
import com.example.workingwithtokens.sortingUtils.VehicleCostType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");

    // Персональная инфрмация пользователя
    @RequestMapping("/personal")
    public ResponseEntity<String> personal(Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        return responseSuccess("response", user);
    }

    @RequestMapping("/testtoken")
    public ResponseEntity<String> personal() {
        return responseSuccess("response", "Токен валиден");
    }

    //Работа с транспортными средствами пользователя
    @RequestMapping("/vehicle/add")
    public ResponseEntity<String> newCar(Principal principal,
                                         @Valid @RequestParam("mark") String mark,
                                         @Valid @RequestParam("model") String model,
                                         @Valid @RequestParam(value = "generation", required = false) String generation,
                                         @Valid @RequestParam("consumptionCity") Float consumptionCity,
                                         @Valid @RequestParam("consumptionRoute") Float consumptionRoute,
                                         @Valid @RequestParam("consumptionMixed") Float consumptionMixed,
                                         @Valid @RequestParam("fuelCapacity") Float fuelCapacity,
                                         @Valid @RequestParam(value = "licensePlateNumber", required = false) String licensePlateNumber) {
        User user = userService.findByUsername(principal.getName());
        Vehicle vehicle = new Vehicle(mark, model, generation, consumptionCity, consumptionRoute, consumptionMixed, fuelCapacity, licensePlateNumber);
        userVehicleRepository.save(new UserVehicle(vehicleRepository.save(vehicle), user));
        convertAndSendToUserJSON(principal.getName(), "/vehicle", "vehicle");
        return responseCreated("status", "Created", "response", vehicle);
    }

    @RequestMapping("/vehicle/all")
    public ResponseEntity<String> allCars(Principal principal,
                                          @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        User byUsername = userService.findByUsername(principal.getName());
        Set<Vehicle> vehicles = byUsername.getVehicles();
        convertAndSendToUserJSON(principal.getName(), "/vehicle", "vehicle");
        return responseSuccess("response", Sortinger.sort(Vehicle.class, vehicles, sortBy));
    }

    @RequestMapping("/vehicle/edit/{id}")
    public ResponseEntity<String> editCar(Principal principal,
                                          @PathVariable("id") Long id,
                                          @Valid @RequestParam("mark") String mark,
                                          @Valid @RequestParam("model") String model,
                                          @Valid @RequestParam(value = "generation", required = false) String generation,
                                          @Valid @RequestParam("consumptionCity") Float consumptionCity,
                                          @Valid @RequestParam("consumptionRoute") Float consumptionRoute,
                                          @Valid @RequestParam("consumptionMixed") Float consumptionMixed,
                                          @Valid @RequestParam("fuelCapacity") Float fuelCapacity,
                                          @Valid @RequestParam(value = "licensePlateNumber", required = false) String licensePlateNumber) {
        User user = userService.findByUsername(principal.getName());
        Optional<Vehicle> vehicle = user.getVehicles().stream().filter(e -> e.getId().equals(id)).findFirst();
        if (vehicle.isPresent()) {
            vehicleRepository.save(new Vehicle(id, mark, model, generation, consumptionCity, consumptionRoute, consumptionMixed, fuelCapacity, licensePlateNumber));
            convertAndSendToUserJSON(principal.getName(), "/vehicle", "vehicle");
            return responseSuccess("response", "Транспортное средство обновлено");
        } else {
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
    public ResponseEntity<String> deleteCar(@PathVariable("id") Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Optional<Vehicle> vehicle = user.getVehicles().stream().filter(e -> e.getId().equals(id)).findFirst();
        if (vehicle.isPresent()) {
            vehicleRepository.delete(vehicle.get());
            convertAndSendToUserJSON(principal.getName(), "/vehicle", "vehicle");
            return responseSuccess("response", "Транспортное средство успешно удалено");
        } else {
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
                                          @RequestParam("date") String date
    ) throws ParseException {

        User user = userService.findByUsername(principal.getName());
        Vehicle vehicle1 = vehicleRepository.getById(vehicleID);
        Date date1 = dateFormat.parse(date);
        System.out.println(date1);
        Set<UserVehicle> userVehicles = user.getUserVehicles();
        for (UserVehicle userVehicle : userVehicles) {
            if (userVehicle.getVehicle().equals(vehicle1)) {
                try {
                    Set<VehicleCosts> vehicleCosts = userVehicle.getVehicleCosts();
                    CostTypes.valueOf(type);
                    VehicleCosts cost = new VehicleCosts(type, value, date1, userVehicle);
                    vehicleCostsRepository.save(cost);
                    convertAndSendToUserJSON(principal.getName(), "/cost", "cost");
                    return responseCreated();
                } catch (IllegalArgumentException e) {
                    return responseBad("response", "Тип должен быть один из REFUELING,WASHING,SERVICE,OTHER");
                }
            }
        }
        return responseBad("response", "Транспортное средство не найдено");
    }


    @RequestMapping("/cost/type/all")
    public ResponseEntity<String> allTypeCosts(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = user.getCosts();
        VehicleCostType refueling = new VehicleCostType(
                CostTypes.REFUELING,
                vehicleCosts.stream()
                        .filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.REFUELING.toString()))
                        .map(VehicleCosts::getValue)
                        .reduce(0F, Float::sum),
                vehicleCosts.stream()
                        .filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.REFUELING.toString()))
                        .count()
                );
        VehicleCostType washing = new VehicleCostType(
                CostTypes.WASHING,
                vehicleCosts.stream()
                        .filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.WASHING.toString()))
                        .map(VehicleCosts::getValue)
                        .reduce(0F, Float::sum),
                vehicleCosts.stream()
                        .filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.WASHING.toString()))
                        .count()
        );
        VehicleCostType service = new VehicleCostType(
                CostTypes.SERVICE,
                vehicleCosts.stream()
                        .filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.SERVICE.toString()))
                        .map(VehicleCosts::getValue)
                        .reduce(0F, Float::sum),
                vehicleCosts.stream()
                        .filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.SERVICE.toString()))
                        .count()
        );
        VehicleCostType other = new VehicleCostType(
                CostTypes.OTHER,
                vehicleCosts.stream()
                        .filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.OTHER.toString()))
                        .map(VehicleCosts::getValue)
                        .reduce(0F, Float::sum),
                vehicleCosts.stream()
                        .filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.OTHER.toString()))
                        .count()
        );
        return responseSuccess("response", new VehicleCostType[]{refueling,washing,service,other});
    }

    @RequestMapping("/cost/list/all")
    public ResponseEntity<String> allListCosts(Principal principal,
                                               @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = user.getCosts();
        return responseSuccess("response", Sortinger.sort(VehicleCosts.class, vehicleCosts, sortBy));
    }

    @Transactional
    @DeleteMapping("/cost/delete/{id}")
    public ResponseEntity<String> deleteCost(Principal principal,
                                             @PathVariable("id") Long id){
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = user.getCosts();
        Optional<VehicleCosts> cost = vehicleCosts.stream().filter(vc -> Objects.equals(vc.getCostId(), id)).findFirst();
        if(cost.isPresent()){
            vehicleCostsRepository.delete(cost.get());
            return responseSuccess();
        }else{
            return responseBad("response","Расход с таким id не найден");
        }
    }


    @RequestMapping("/cost/edit/{id}")
    public ResponseEntity<String> editCost(Principal principal,
                                           @PathVariable("id") Long id,
                                           @RequestParam("vId") Long vehicleID,
                                           @RequestParam("type") String type,
                                           @RequestParam("value") Float value,
                                           @RequestParam("date") String date) throws ParseException {
        User user = userService.findByUsername(principal.getName());
        Optional<VehicleCosts> cost = user.getCosts().stream().filter(c -> c.getCostId().equals(id)).findFirst();
        Vehicle vehicle = vehicleRepository.getById(vehicleID);
        Date parsedDate = dateFormat.parse(date);
        Set<UserVehicle> userVehicles = user.getUserVehicles();
        if (cost.isPresent()) {
            for (UserVehicle userVehicle : userVehicles) {
                if (userVehicle.getVehicle().equals(vehicle)) {
                    try {
                        Set<VehicleCosts> vehicleCosts = userVehicle.getVehicleCosts();
                        CostTypes.valueOf(type);
                        VehicleCosts newCost = new VehicleCosts(id, type, value, parsedDate, userVehicle);
                        vehicleCostsRepository.save(newCost);
                        convertAndSendToUserJSON(principal.getName(), "/cost", "cost");
                        return responseCreated("response", "Расход обновлен");
                    } catch (IllegalArgumentException e) {
                        return responseBad("response", "Тип должен быть один из REFUELING,WASHING,SERVICE,OTHER");
                    }
                }
            }
            return responseBad("response", "Транспортное средство не найдено");
        } else {
            return responseBad("response", "Расход с данным id не найден");
        }
    }

    @RequestMapping("/note/add")
    public ResponseEntity<String> addNote(Principal principal,
                                          @RequestParam(value = "vehicle_id",required = false) Long vehicleID,
                                          @RequestParam(value = "description",required = false) String description,
                                          @RequestParam(value = "value",required = false) Float value,
                                          @RequestParam("end_date") String endDate,
                                          @RequestParam("is_cost") boolean isCost,
                                          @RequestParam("is_completed") boolean isCompleted,
                                          @RequestParam(value = "cost_type",required = false) String costType
                                          ) throws ParseException {
        User user= userService.findByUsername(principal.getName());
        try{
            if (isCost) {
                if (vehicleID == null)
                    throw new NullPointerException("Поле идентификатора ТС не может быть пустым");
                if (description == null)
                    throw new NullPointerException("Поле описание не может быть пустым");
                if (costType == null)
                    throw new NullPointerException("Поле тип не может быть пустым");
                if (value == null)
                    throw new NullPointerException("Поле велечины расхода не может быть пустым");
                CostTypes.valueOf(costType);
                Optional<UserVehicle> vehicle = user.getUserVehicles().stream().filter(userVehicle -> userVehicle.getVehicle().getId().equals(vehicleID)).findFirst();
                if (vehicle.isPresent()) {
                    UserNote note = new UserNote(description, value, dateFormat.parse(endDate), isCost, isCompleted, costType, user, vehicle.get());
                    userNoteRepository.save(note);
                } else {
                    return responseBad("response", "Транспортное средство не найдено");
                }
                return responseCreated();
            } else {
                UserNote note = new UserNote(description, dateFormat.parse(endDate), false, isCompleted, user);
                userNoteRepository.save(note);
                return responseCreated();
            }
        }catch (IllegalArgumentException e){
            return responseBad("response","Тип должен быть один из REFUELING,WASHING,SERVICE,OTHER");
        }
    }

    @RequestMapping("/note/uncompleted/all")
    public ResponseEntity<String> allUncompletedNotes(Principal principal){
        User user=userService.findByUsername(principal.getName());
        Set<UserNote> notes = user.getNotes().stream().filter(
                userNote -> !userNote.isCompleted()).collect(Collectors.toSet());
        return responseSuccess("response",notes);
    }

    @RequestMapping("/note/completed/all")
    public ResponseEntity<String> allCompletedNotes(Principal principal){
        User user=userService.findByUsername(principal.getName());
        Set<UserNote> notes = user.getNotes().stream().filter(
                userNote -> userNote.isCompleted()).collect(Collectors.toSet());
        return responseSuccess("response",notes);
    }

    @RequestMapping("/note/overdued/all")
    public ResponseEntity<String> allOverduedNotes(Principal principal){
        User user=userService.findByUsername(principal.getName());
        Set<UserNote> notes = user.getNotes().stream().filter(
                userNote -> userNote.getEndDate().compareTo(Calendar.getInstance().getTime())>0).collect(Collectors.toSet());
        System.out.println(Calendar.getInstance().getTime());
        return responseSuccess("response",notes);
    }


    @RequestMapping("/news/all")
    public ResponseEntity<String> allNews(Principal principal,
                                          @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        List<News> newsList = newsRepository.findAll();
        return responseSuccess("response", Sortinger.sort(News.class, newsList, sortBy));
    }
}
