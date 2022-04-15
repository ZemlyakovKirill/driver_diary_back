package ru.themlyakov.driverdiary.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.themlyakov.driverdiary.controllers.AbstractController;

@Service
public class RequestMarkService {

    @Async("schedulePool2")
    @Scheduled(fixedRate = 1_200_000)
    public void updateAcceptedMarks() {
        synchronized (AbstractController.monitor){

        }
    }
}
