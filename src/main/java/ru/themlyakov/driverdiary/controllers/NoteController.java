package ru.themlyakov.driverdiary.controllers;

import org.springframework.data.domain.Sort;
import ru.themlyakov.driverdiary.entities.User;
import ru.themlyakov.driverdiary.entities.UserNote;
import ru.themlyakov.driverdiary.entities.UserVehicle;
import ru.themlyakov.driverdiary.entities.VehicleCosts;
import ru.themlyakov.driverdiary.enums.CostTypes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.themlyakov.driverdiary.utils.PaginationWrapper;

import javax.transaction.Transactional;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Validated
@Api(tags="Пути заметок")
public class NoteController extends AbstractController{

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");

    @ApiOperation(value = "Добавление временной заметки")
    @PostMapping("/user/note/add")
    public ResponseEntity<String> addNote(Principal principal, @RequestParam(value = "vehicle_id", required = false) Long vehicleID, @RequestParam(value = "description", required = false) String description, @RequestParam(value = "value", required = false) Float value, @RequestParam("end_date") String endDate, @RequestParam("is_cost") boolean isCost, @RequestParam("is_completed") boolean isCompleted, @RequestParam(value = "cost_type", required = false) String costType) throws ParseException {
        User user = userService.findByUsername(principal.getName());
        try {
            if (isCost) {
                if (vehicleID == null) throw new NullPointerException("Поле идентификатора ТС не может быть пустым");
                if (costType == null) throw new NullPointerException("Поле тип не может быть пустым");
                if (value == null) throw new NullPointerException("Поле величины расхода не может быть пустым");
                CostTypes.valueOf(costType);
                Optional<UserVehicle> vehicle = user.getUserVehicles().stream().filter(userVehicle -> userVehicle.getVehicle().getId().equals(vehicleID)).findFirst();
                if (vehicle.isPresent()) {
                    UserNote note = new UserNote(description, value, dateFormat.parse(endDate), true, isCompleted, costType, user, vehicle.get());
                    userNoteRepository.save(note);
                } else {
                    return responseBad("response", "Транспортное средство не найдено");
                }
                convertAndSendToUserJSON(principal.getName(), "/note", "note");
                return responseSuccess();
            } else {
                UserNote note = new UserNote(description, dateFormat.parse(endDate), false, isCompleted, user);
                userNoteRepository.save(note);
                convertAndSendToUserJSON(principal.getName(), "/note", "note");
                return responseSuccess();
            }
        } catch (IllegalArgumentException e) {
            return responseBad("response", "Тип должен быть один из REFUELING,WASHING,SERVICE,OTHER");
        }
    }

    @ApiOperation("Пометка выполнено для заметки")
    @PostMapping("/user/note/complete/{id}")
    public ResponseEntity<String> completeNote(Principal principal,@PathVariable("id") Long noteID){
        User user = userService.findByUsername(principal.getName());
        Optional<UserNote> note = user.getNotes().stream().filter(userNote -> userNote.getId().equals(noteID)).findFirst();
        if (!note.isPresent()) {
            return responseBad("response", "Заметка с таким id не найдена");
        }
        UserNote userNote = note.get();
        if(userNote.isCost()){
            vehicleCostsRepository.save(new VehicleCosts(
                   userNote.getType(),
                   userNote.getValue(),
                   userNote.getEndDate(),
                   userNote.getUserVehicle()
            ));
            userNoteRepository.delete(userNote);
            convertAndSendToUserJSON(principal.getName(), "/note", "note");
            convertAndSendToUserJSON(principal.getName(), "/cost", "cost");
            return responseSuccess("response","Расход добавлен");
        }
        userNote.setCompleted(true);
        userNoteRepository.save(userNote);
        convertAndSendToUserJSON(principal.getName(), "/note", "note");
        return responseSuccess("response","Метка обновлена");
    }

    @ApiOperation("Пометка не выполнено для заметки")
    @PostMapping("/user/note/uncomplete/{id}")
    public ResponseEntity<String> uncompleteNote(Principal principal,@PathVariable("id") Long noteID){
        User user = userService.findByUsername(principal.getName());
        Optional<UserNote> note = user.getNotes().stream().filter(userNote -> userNote.getId().equals(noteID)).findFirst();
        if (!note.isPresent()) {
            return responseBad("response", "Заметка с таким id не найдена");
        }
        UserNote userNote = note.get();
        userNote.setCompleted(false);
        userNoteRepository.save(userNote);
        convertAndSendToUserJSON(principal.getName(), "/note", "note");
        return responseSuccess("response","Метка обновлена");
    }

    @ApiOperation(value = "Редактирование временной метки")
    @PutMapping("/user/note/edit/{id}")
    public ResponseEntity<String> editNote(Principal principal, @PathVariable("id") Long noteID, @RequestParam(value = "vehicle_id", required = false) Long vehicleID, @RequestParam(value = "description", required = false) String description, @RequestParam(value = "value", required = false) Float value, @RequestParam("end_date") String endDate, @RequestParam("is_cost") boolean isCost, @RequestParam("is_completed") boolean isCompleted, @RequestParam(value = "cost_type", required = false) String costType) throws ParseException {
        User user = userService.findByUsername(principal.getName());
        Optional<UserNote> note = user.getNotes().stream().filter(userNote -> userNote.getId().equals(noteID)).findFirst();
        if (!note.isPresent()) {
            return responseBad("response", "Заметка с таким id не найдена");
        }
        try {
            if (isCost) {
                if (vehicleID == null) throw new NullPointerException("Поле идентификатора ТС не может быть пустым");
                if (costType == null) throw new NullPointerException("Поле тип не может быть пустым");
                if (value == null) throw new NullPointerException("Поле велечины расхода не может быть пустым");
                CostTypes.valueOf(costType);
                Optional<UserVehicle> vehicle = user.getUserVehicles().stream().filter(userVehicle -> userVehicle.getVehicle().getId().equals(vehicleID)).findFirst();
                if (vehicle.isPresent()) {
                    UserNote noteEdited = new UserNote(noteID, description, value, dateFormat.parse(endDate), true, isCompleted, costType, user, vehicle.get());
                    userNoteRepository.save(noteEdited);
                } else {
                    return responseBad("response", "Транспортное средство не найдено");
                }
                convertAndSendToUserJSON(principal.getName(), "/note", "note");
                return responseCreated();
            } else {
                UserNote noteEdited = new UserNote(noteID, description, dateFormat.parse(endDate), false, isCompleted, user);
                userNoteRepository.save(noteEdited);
                convertAndSendToUserJSON(principal.getName(), "/note", "note");
                return responseCreated();
            }
        } catch (IllegalArgumentException e) {
            return responseBad("response", "Тип должен быть один из REFUELING,WASHING,SERVICE,OTHER");
        }
    }

    @ApiOperation(value = "Удаление временной метки")
    @Transactional
    @DeleteMapping("/user/note/delete/{id}")
    public ResponseEntity<String> deleteNote(Principal principal, @PathVariable("id") Long id) {
        User user = userService.findByUsername(principal.getName());
        Set<UserNote> vehicleCosts = user.getNotes();
        Optional<UserNote> note = vehicleCosts.stream().filter(userNote -> userNote.getId().equals(id)).findFirst();
        if (note.isPresent()) {
            userNoteRepository.delete(note.get());
            convertAndSendToUserJSON(principal.getName(), "/note", "note");
            return responseSuccess();
        } else {
            return responseBad("response", "Заметка с таким id не найдена");
        }
    }

    @ApiOperation(value = "Просмотр всех незавершенных временных заметок")
    @GetMapping("/user/note/uncompleted/all")
    public ResponseEntity<String> allUncompletedNotes(Principal principal,@RequestParam(value = "sortBy", defaultValue = "description") String sortBy,
                                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "direction", defaultValue = "ASC") Sort.Direction direction) {
        User user = userService.findByUsername(principal.getName());
        List<UserNote> notes = user.getNotes().stream().filter(userNote -> !userNote.isCompleted()).collect(Collectors.toList());
        PaginationWrapper wrapper = new PaginationWrapper(notes, page, sortBy, direction);
        return responseSuccess("response", wrapper);
    }

    @ApiOperation(value = "Просмотр всех выполненных временных меток")
    @GetMapping("/user/note/completed/all")
    public ResponseEntity<String> allCompletedNotes(Principal principal,@RequestParam(value = "sortBy", defaultValue = "description") String sortBy,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "direction", defaultValue = "ASC") Sort.Direction direction) {
        User user = userService.findByUsername(principal.getName());
        List<UserNote> notes = user.getNotes().stream().filter(UserNote::isCompleted).collect(Collectors.toList());
        PaginationWrapper wrapper = new PaginationWrapper(notes, page, sortBy, direction);
        return responseSuccess("response", wrapper);
    }

    @ApiOperation(value = "Просмотр всех просроченных временных меток")
    @GetMapping("/user/note/overdued/all")
    public ResponseEntity<String> allOverduedNotes(Principal principal,@RequestParam(value = "sortBy", defaultValue = "description") String sortBy,
                                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "direction", defaultValue = "ASC") Sort.Direction direction) {
        User user = userService.findByUsername(principal.getName());
        List<UserNote> notes = user.getNotes().stream().filter((userNote) -> userNote.getEndDate().compareTo(Calendar.getInstance().getTime()) < 0).collect(Collectors.toList());
        PaginationWrapper wrapper = new PaginationWrapper(notes, page, sortBy, direction);
        return responseSuccess("response", wrapper);
    }
}
