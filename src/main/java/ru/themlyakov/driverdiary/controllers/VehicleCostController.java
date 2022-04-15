package ru.themlyakov.driverdiary.controllers;

import ru.themlyakov.driverdiary.entities.User;
import ru.themlyakov.driverdiary.entities.UserVehicle;
import ru.themlyakov.driverdiary.entities.Vehicle;
import ru.themlyakov.driverdiary.entities.VehicleCosts;
import ru.themlyakov.driverdiary.enums.CostTypes;
import ru.themlyakov.driverdiary.sortingUtils.Sortinger;
import ru.themlyakov.driverdiary.sortingUtils.VehicleCostType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RestController
@Validated
@Api(tags = "Пути расходов на ТС")
public class VehicleCostController extends AbstractController {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");

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

    @ApiOperation(value = "Просмотр всех расходов по типу")
    @GetMapping("/user/cost/type/all")
    public ResponseEntity<String> allTypeCosts(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = user.getCosts();
        VehicleCostType refueling = new VehicleCostType(CostTypes.REFUELING, vehicleCosts.stream().filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.REFUELING.toString())).map(VehicleCosts::getValue).reduce(0F, Float::sum), vehicleCosts.stream().filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.REFUELING.toString())).count());
        VehicleCostType washing = new VehicleCostType(CostTypes.WASHING, vehicleCosts.stream().filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.WASHING.toString())).map(VehicleCosts::getValue).reduce(0F, Float::sum), vehicleCosts.stream().filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.WASHING.toString())).count());
        VehicleCostType service = new VehicleCostType(CostTypes.SERVICE, vehicleCosts.stream().filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.SERVICE.toString())).map(VehicleCosts::getValue).reduce(0F, Float::sum), vehicleCosts.stream().filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.SERVICE.toString())).count());
        VehicleCostType other = new VehicleCostType(CostTypes.OTHER, vehicleCosts.stream().filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.OTHER.toString())).map(VehicleCosts::getValue).reduce(0F, Float::sum), vehicleCosts.stream().filter(vehicleCosts1 -> vehicleCosts1.getType().equals(CostTypes.OTHER.toString())).count());
        return responseSuccess("response", new VehicleCostType[]{refueling, washing, service, other});
    }

    @ApiOperation(value = "Просмотр расходов списком")
    @GetMapping("/user/cost/list/all")
    public ResponseEntity<String> allListCosts(Principal principal, @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = user.getCosts();
        return responseSuccess("response", Sortinger.sort(VehicleCosts.class, vehicleCosts, sortBy));
    }

    @ApiOperation(value = "Удаление расхода")
    @Transactional
    @DeleteMapping("/user/cost/delete/{id}")
    public ResponseEntity<String> deleteCost(Principal principal, @PathVariable("id") Long id) {
        User user = userService.findByUsername(principal.getName());
        Set<VehicleCosts> vehicleCosts = user.getCosts();
        Optional<VehicleCosts> cost = vehicleCosts.stream().filter(vc -> Objects.equals(vc.getCostId(), id)).findFirst();
        if (cost.isPresent()) {
            vehicleCostsRepository.delete(cost.get());
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
