package com.hospitalrafael.crm.controller;

import com.hospitalrafael.crm.model.Interacao;
import com.hospitalrafael.crm.service.InteracaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interacoes")
public class InteracaoController {

    private final InteracaoService interacaoService;

    public InteracaoController(InteracaoService interacaoService) {
        this.interacaoService = interacaoService;
    }

    @PostMapping
    public ResponseEntity<Interacao> registrar(@Valid @RequestBody Interacao interacao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interacaoService.registrar(interacao));
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<Interacao>> listarPorLead(@PathVariable Long leadId) {
        return ResponseEntity.ok(interacaoService.listarPorLead(leadId));
    }

    @GetMapping("/operador/{operadorId}")
    public ResponseEntity<List<Interacao>> listarPorOperador(@PathVariable Long operadorId) {
        return ResponseEntity.ok(interacaoService.listarPorOperador(operadorId));
    }

    @GetMapping("/urgentes")
    public ResponseEntity<List<Interacao>> listarUrgentes() {
        return ResponseEntity.ok(interacaoService.listarUrgentes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Interacao> atualizar(@PathVariable Long id,
                                                @Valid @RequestBody Interacao dados) {
        return ResponseEntity.ok(interacaoService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        interacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
