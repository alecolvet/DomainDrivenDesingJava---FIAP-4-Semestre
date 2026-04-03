package com.hospitalrafael.crm.controller;

import com.hospitalrafael.crm.model.Notificacao;
import com.hospitalrafael.crm.service.NotificacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @PostMapping
    public ResponseEntity<Notificacao> criar(@Valid @RequestBody Notificacao notificacao) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacaoService.criar(notificacao));
    }

    @GetMapping
    public ResponseEntity<List<Notificacao>> listar() {
        return ResponseEntity.ok(notificacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notificacaoService.buscarPorId(id));
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<Notificacao>> listarPorLead(@PathVariable Long leadId) {
        return ResponseEntity.ok(notificacaoService.listarPorLead(leadId));
    }

    @GetMapping("/operador/{operadorId}")
    public ResponseEntity<List<Notificacao>> listarPorOperador(@PathVariable Long operadorId) {
        return ResponseEntity.ok(notificacaoService.listarPorOperador(operadorId));
    }

    @PatchMapping("/{id}/mensagem")
    public ResponseEntity<Notificacao> atualizarMensagem(@PathVariable Long id,
                                                           @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(notificacaoService.atualizarMensagem(id, body.get("mensagem")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        notificacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
