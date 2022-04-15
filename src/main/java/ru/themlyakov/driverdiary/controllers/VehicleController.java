package ru.themlyakov.driverdiary.controllers;

import ru.themlyakov.driverdiary.entities.User;
import ru.themlyakov.driverdiary.entities.UserVehicle;
import ru.themlyakov.driverdiary.entities.Vehicle;
import ru.themlyakov.driverdiary.sortingUtils.Sortinger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RestController
@Validated
@Api(tags="Пути транспортных средств")
public class VehicleController extends AbstractController{
    //Работа с транспортными средствами пользователя
    @ApiOperation(value = "Добавление транспортного средства")
    @PostMapping("/user/vehicle/add")
    public ResponseEntity<String> newCar(Principal principal, @Valid @RequestParam("mark") String mark, @Valid @RequestParam("model") String model, @Valid @RequestParam(value = "generation", required = false) String generation, @Valid @RequestParam("consumptionCity") Float consumptionCity, @Valid @RequestParam("consumptionRoute") Float consumptionRoute, @Valid @RequestParam("consumptionMixed") Float consumptionMixed, @Valid @RequestParam("fuelCapacity") Float fuelCapacity, @Valid @RequestParam(value = "licensePlateNumber", required = false) String licensePlateNumber) {
        User user = userService.findByUsername(principal.getName());
        Vehicle vehicle = new Vehicle(mark, model, generation, consumptionCity, consumptionRoute, consumptionMixed, fuelCapacity, licensePlateNumber);
        userVehicleRepository.save(new UserVehicle(vehicleRepository.save(vehicle), user));
        convertAndSendToUserJSON(principal.getName(), "/vehicle", "vehicle");
        return responseCreated("status", "Created", "response", vehicle);
    }

    @ApiOperation(value = "Просмотр всех транспортных средств")
    @GetMapping("/user/vehicle/all")
    public ResponseEntity<String> allCars(Principal principal, @RequestParam(value = "sortBy", defaultValue = "") String sortBy) {
        User byUsername = userService.findByUsername(principal.getName());
        Set<Vehicle> vehicles = byUsername.getVehicles();
        return responseSuccess("response", Sortinger.sort(Vehicle.class, vehicles, sortBy));
    }

    @ApiOperation(value = "Редактирование транспортного средства")
    @PutMapping("/user/vehicle/edit/{id}")
    public ResponseEntity<String> editCar(Principal principal, @PathVariable("id") Long id, @Valid @RequestParam("mark") String mark, @Valid @RequestParam("model") String model, @Valid @RequestParam(value = "generation", required = false) String generation, @Valid @RequestParam("consumptionCity") Float consumptionCity, @Valid @RequestParam("consumptionRoute") Float consumptionRoute, @Valid @RequestParam("consumptionMixed") Float consumptionMixed, @Valid @RequestParam("fuelCapacity") Float fuelCapacity, @Valid @RequestParam(value = "licensePlateNumber", required = false) String licensePlateNumber) {
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

    @ApiOperation(value = "Просмотр конкретного транспортного средства")
    @GetMapping("/user/vehicle/{id}")
    public ResponseEntity<String> getCar(@PathVariable("id") Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        for (Vehicle v : user.getVehicles())
            if (Objects.equals(v.getId(), id)) return responseSuccess("response", v);
        return responseBad("response", "Транспортное средство с таким id не найдено");
    }

    @ApiOperation(value = "Удаление конкретного транспортного средства")
    @Transactional
    @DeleteMapping("/user/vehicle/delete/{id}")
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
}
