package com.hospitalrafael.crm.controller;

import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public ResponseEntity<Lead> cadastrar(@Valid @RequestBody Lead lead) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.cadastrar(lead));
    }

    @GetMapping
    public ResponseEntity<List<Lead>> listar() {
        return ResponseEntity.ok(leadService.listarTodos());
    }

    @GetMapping("/prioridade")
    public ResponseEntity<List<Lead>> listarPorPrioridade() {
        return ResponseEntity.ok(leadService.listarPorPrioridade());
    }

    @GetMapping("/urgentes")
    public ResponseEntity<List<Lead>> listarUrgentes() {
        return ResponseEntity.ok(leadService.listarUrgentes());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Lead>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(leadService.listarPorStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lead> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Lead> atualizarStatus(@PathVariable Long id,
                                                 @RequestBody Map<String, String> body) {
        String novoStatus = body.get("status");
        return ResponseEntity.ok(leadService.atualizarStatus(id, novoStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        leadService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
