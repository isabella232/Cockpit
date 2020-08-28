package com.cockpit.api.controller;

import com.cockpit.api.exception.ResourceNotFoundException;
import com.cockpit.api.model.dto.ImpedimentDTO;
import com.cockpit.api.service.AuthService;
import com.cockpit.api.service.ImpedimentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ImpedimentController {

    public final ImpedimentService impedimentService;
    private final AuthService authService;
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public ImpedimentController(ImpedimentService impedimentService, AuthService authService) {
        this.impedimentService = impedimentService;
        this.authService = authService;
    }

    // DELETE an Impediment
    @DeleteMapping(
            value = "/api/v1/impediment/delete/{id}"
    )
    public ResponseEntity deleteImpediment(@PathVariable Long id,
                                           @RequestHeader("Authorization") String authHeader) {
        if (authService.isUserAuthorized(authHeader)) {
            try {
                impedimentService.deleteImpediment(id);
                return ResponseEntity.ok("One Impediment has been deleted");
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        } else {
            return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
        }
    }

    // UPDATE an Impediment
    @PutMapping(
            value = "/api/v1/impediment/update/{id}"
    )
    public ResponseEntity updateImpediment(@RequestBody ImpedimentDTO impedimentDTO,
                                           @PathVariable Long id,
                                           @RequestHeader("Authorization") String authHeader) {
        if (authService.isUserAuthorized(authHeader)) {
            try {
                ImpedimentDTO impediment = impedimentService.updateImpediment(impedimentDTO, id);
                return ResponseEntity.ok().body(impediment);
            } catch (ResourceNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        } else {
            return ResponseEntity.ok(HttpStatus.UNAUTHORIZED);
        }
    }
}
