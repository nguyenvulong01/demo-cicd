package com.example.animals.controller;

import com.example.animals.dto.Animal;
import com.example.animals.service.AnimalsService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/animals")
public class AnimalsController {

    @Autowired
    private AnimalsService animalsService;

    @GetMapping
    public List<Animal> getAnimals() {
        System.out.println("LOG START ...");
        return animalsService.getAnimals();
    }

    @PostMapping
    public ResponseEntity<Void> importFile(@RequestParam(name = "filename") String filename) {
        animalsService.importFile(filename);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
