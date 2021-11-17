package com.example.workingwithtokens.services;

import com.example.workingwithtokens.repositories.AcceptedMarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AcceptedMarkService {
    @Autowired
    AcceptedMarkRepository acceptedMarkRepository;


}
