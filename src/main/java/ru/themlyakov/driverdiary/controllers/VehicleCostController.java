package ru.themlyakov.driverdiary.controllers;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.themlyakov.driverdiary.entities.User;
import ru.themlyakov.driverdiary.entities.UserVehicle;
import ru.themlyakov.driverdiary.entities.Vehicle;
import ru.themlyakov.driverdiary.entities.VehicleCosts;
import ru.themlyakov.driverdiary.enums.CostTypes;
import ru.themlyakov.driverdiary.enums.SearchIntervalForTypeCost;
import ru.themlyakov.driverdiary.utils.PaginationWrapper;
import ru.themlyakov.driverdiary.utils.Sortinger;
import ru.themlyakov.driverdiary.utils.VehicleCostType;

import javax.transaction.Transactional;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Validated
@Api(tags = "Пути расходов на ТС")
public class VehicleCostController extends AbstractController {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");


    private static class VehicleTypeCostWrapper {
        @Expose
        private int month;
        @Expose
        private Map<CostTypes, Double> result;

        private VehicleTypeCostWrapper(int month, Map<CostTypes, Double> result) {
            this.month = month;
            this.result = result;
        }

        public static VehicleTypeCostWrapper of(Collection<VehicleCosts> vehicleCosts, int month) {
            Calendar cal = Calendar.getInstance();
            int currYear = cal.get(Calendar.YEAR);
            Map<CostTypes, Double> collect = vehicleCosts.stream().filter(vc -> {
                cal.setTime(vc.getDate());
                return cal.get(Calendar.MONTH) == month-1 && cal.get(Calendar.YEAR) == currYear;
            }).collect(Collectors.groupingBy(o -> CostTypes.fromOrdinal(Integer.parseInt(o.getType())), Collectors.summingDouble(VehicleCosts::getValue)));
            return new VehicleTypeCostWrapper(month,collect);
        }
    }

    //Работа с расходами пользователя
    @ApiOperation(value = "Добавление расхода транспортного средства")
    @PostMapping("/user/cost/add")
    public ResponseEntity<String> addCost(Principal principal, @RequestParam("vId") Long vehicleID, @RequestParam("type") String type, @RequestParam("value") Float value, @RequestParam("date") String date) throws ParseException {

        User user = userService.findByUsername(principal.getName());
        Vehicle vehicle1 = vehicleRepository.getById(vehicleID);
        Date date1 = dateFormat.parse(date);
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

    @ApiOperation(value = "Просмотр помесячно расходов по типу")
    @GetMapping("/user/cost/type/all")
    public ResponseEntity<String> monthTypeCosts(Principal principal,
                                               @RequestParam(name = "month", defaultValue = "1")
                                               int month) {
        if(month<1 || month>12){
            throw new IllegalArgumentException("Месяц долженбыть в диапазоне от 1 до 12");
        }
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = user.getCosts();
        VehicleTypeCostWrapper wrapper = VehicleTypeCostWrapper.of(vehicleCosts, month);
        return responseSuccess("response", wrapper);
    }

    @ApiOperation(value = "Просмотр месяцев с расходами")
    @GetMapping("/user/cost/type/month/get")
    public ResponseEntity<String> monthTypeCosts(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Calendar cal = Calendar.getInstance();
        int currYear=cal.get(Calendar.YEAR);
        Set<Integer> months = user.getCosts().stream()
                .filter(vc->{
                    cal.setTime(vc.getDate());
                    return cal.get(Calendar.YEAR)==currYear;
                })
                .map(vc->{
            cal.setTime(vc.getDate());
            return cal.get(Calendar.MONTH)+1;
        }).collect(Collectors.toSet());
        return responseSuccess("response", months);
    }

    @ApiOperation(value = "Просмотр постранично расходов списком")
    @GetMapping("/user/cost/list/paged")
    public ResponseEntity<String> pagedListCosts(Principal principal,
                                                 @RequestParam(value = "sortBy", defaultValue = "value") String sortBy,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "direction", defaultValue = "ASC") Sort.Direction direction) {
        User user = userService.findByUsername(principal.getName());
        List<VehicleCosts> vehicleCosts = new ArrayList<>(user.getCosts());
        PaginationWrapper wrapper = new PaginationWrapper(vehicleCosts, page, sortBy, direction);
        return responseSuccess("response", wrapper);
    }

    @ApiOperation(value = "Просмотр всех расходов списком")
    @GetMapping("/user/cost/list/all")
    public ResponseEntity<String> allListCosts(Principal principal,
                                               @RequestParam(value = "month",defaultValue = "1") int month,
                                               @RequestParam(value = "sortBy", defaultValue = "value") String sortBy,
                                               @RequestParam(value = "direction", defaultValue = "ASC") Sort.Direction direction) {
        User user = userService.findByUsername(principal.getName());
        Calendar cal = Calendar.getInstance();
        int currYear=cal.get(Calendar.YEAR);
        List<VehicleCosts> vehicleCosts = user.getCosts().stream()
                .filter(vc->{
                    cal.setTime(vc.getDate());
                    return cal.get(Calendar.YEAR)==currYear && cal.get(Calendar.MONTH)==month-1;
                }).collect(Collectors.toList());
        Sortinger.sort(vehicleCosts, sortBy, direction);
        return responseSuccess("response", vehicleCosts);
    }

    @ApiOperation(value = "Удаление расхода")
    @Transactional
    @DeleteMapping("/user/cost/delete/{id}")
    public ResponseEntity<String> deleteCost(Principal principal, @PathVariable("id") Long id) {
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = user.getCosts();
        Optional<VehicleCosts> cost = vehicleCosts.stream().filter(vc -> Objects.equals(vc.getCostId(), id)).findFirst();
        if (cost.isPresent()) {
            vehicleCostsRepository.delete(cost.get().getCostId());
            convertAndSendToUserJSON(principal.getName(), "/cost", "cost");
            return responseSuccess();
        } else {
            return responseBad("response", "Расход с таким id не найден");
        }
    }

    @ApiOperation(value = "Редактирование расхода")
    @PutMapping("/user/cost/edit/{id}")
    public ResponseEntity<String> editCost(Principal principal, @PathVariable("id") Long id, @RequestParam("vId") Long vehicleID, @RequestParam("type") String type, @RequestParam("value") Float value, @RequestParam("date") String date) throws ParseException {
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

}
