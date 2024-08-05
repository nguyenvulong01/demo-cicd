package com.example.animals.service;

import com.example.animals.dto.Animal;

import java.util.List;

public interface AnimalsService {
    List<Animal> getAnimals();
    void importFile(String fileName);
}
