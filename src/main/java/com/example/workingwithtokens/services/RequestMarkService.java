package com.example.workingwithtokens.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.example.workingwithtokens.controllers.AbstractController.monitor;

@Service
public class RequestMarkService {

    @Async("schedulePool2")
    @Scheduled(fixedRate = 1_200_000)
    public void updateAcceptedMarks() {
        synchronized (monitor){

        }
    }
}
