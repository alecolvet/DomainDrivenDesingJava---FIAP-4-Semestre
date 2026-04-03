package com.hospitalrafael.crm.controller;

import com.hospitalrafael.crm.model.Agendamento;
import com.hospitalrafael.crm.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public ResponseEntity<Agendamento> criar(@Valid @RequestBody Agendamento agendamento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.criar(agendamento));
    }

    @GetMapping
    public ResponseEntity<List<Agendamento>> listar() {
        return ResponseEntity.ok(agendamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.buscarPorId(id));
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<Agendamento>> listarPorLead(@PathVariable Long leadId) {
        return ResponseEntity.ok(agendamentoService.listarPorLead(leadId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Agendamento>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(agendamentoService.listarPorStatus(status));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Agendamento> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.confirmar(id));
    }

    @PatchMapping("/{id}/reagendar")
    public ResponseEntity<Agendamento> reagendar(@PathVariable Long id,
                                                   @RequestBody Map<String, String> body) {
        LocalDate novaData = LocalDate.parse(body.get("dataHora"));
        return ResponseEntity.ok(agendamentoService.reagendar(id, novaData));
    }

    @PatchMapping("/lembretes/enviar")
    public ResponseEntity<Map<String, Integer>> enviarLembretes() {
        int count = agendamentoService.enviarLembretes();
        return ResponseEntity.ok(Map.of("lembretes_enviados", count));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        agendamentoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
