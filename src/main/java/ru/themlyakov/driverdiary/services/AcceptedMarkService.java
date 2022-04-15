package ru.themlyakov.driverdiary.services;

import ru.themlyakov.driverdiary.repositories.AcceptedMarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AcceptedMarkService {
    @Autowired
    AcceptedMarkRepository acceptedMarkRepository;


}
