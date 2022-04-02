package com.example.workingwithtokens.controllers;

import com.example.workingwithtokens.entities.User;
import com.example.workingwithtokens.entities.UserNote;
import com.example.workingwithtokens.entities.UserVehicle;
import com.example.workingwithtokens.enums.CostTypes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
                    UserNote note = new UserNote(description, value, dateFormat.parse(endDate), isCost, isCompleted, costType, user, vehicle.get());
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
    public ResponseEntity<String> allUncompletedNotes(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Set<UserNote> notes = user.getNotes().stream().filter(userNote -> !userNote.isCompleted()).collect(Collectors.toSet());
        return responseSuccess("response", notes);
    }

    @ApiOperation(value = "Просмотр всех выполненных временных меток")
    @GetMapping("/user/note/completed/all")
    public ResponseEntity<String> allCompletedNotes(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Set<UserNote> notes = user.getNotes().stream().filter(UserNote::isCompleted).collect(Collectors.toSet());
        return responseSuccess("response", notes);
    }

    @ApiOperation(value = "Просмотр всех просроченных временных меток")
    @GetMapping("/user/note/overdued/all")
    public ResponseEntity<String> allOverduedNotes(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        Set<UserNote> notes = user.getNotes().stream().filter((userNote) -> userNote.getEndDate().compareTo(Calendar.getInstance().getTime()) < 0).collect(Collectors.toSet());
        return responseSuccess("response", notes);
    }
}
