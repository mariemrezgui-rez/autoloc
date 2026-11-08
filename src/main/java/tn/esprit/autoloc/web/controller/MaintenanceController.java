package tn.esprit.autoloc.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.dto.MaintenanceRequestDTO;
import tn.esprit.autoloc.dto.MaintenanceResponseDTO;
import tn.esprit.autoloc.service.IMaintenanceService;

import java.util.List;

@RestController
@RequestMapping("/api/maintenances")
@RequiredArgsConstructor
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceResponseDTO create(@Valid @RequestBody MaintenanceRequestDTO dto) {
        return maintenanceService.create(dto);
    }

    @GetMapping("/{id}")
    public MaintenanceResponseDTO findById(@PathVariable Long id) {
        return maintenanceService.findById(id);
    }

    @GetMapping
    public List<MaintenanceResponseDTO> findAll() {
        return maintenanceService.findAll();
    }

    @PutMapping("/{id}")
    public MaintenanceResponseDTO update(@PathVariable Long id, @Valid @RequestBody MaintenanceRequestDTO dto) {
        return maintenanceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        maintenanceService.deleteById(id);
    }
}
