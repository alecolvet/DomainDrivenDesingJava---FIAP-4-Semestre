package com.hospitalrafael.crm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceptions — captura e formata todos os erros da API
 * em um padrão consistente de resposta JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ─── Estrutura padrão de erro ─────────────────────────────────────────────

    private Map<String, Object> buildErrorResponse(HttpStatus status, String erro, String mensagem) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("erro", erro);
        body.put("mensagem", mensagem);
        return body;
    }

    // ─── Exceções de recurso não encontrado ───────────────────────────────────

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage()));
    }

    // ─── Exceções de Lead ────────────────────────────────────────────────────

    @ExceptionHandler(LeadDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleLeadDuplicado(LeadDuplicadoException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(HttpStatus.CONFLICT, "Lead duplicado", ex.getMessage()));
    }

    @ExceptionHandler(LeadStatusInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleLeadStatusInvalido(LeadStatusInvalidoException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Status de Lead inválido", ex.getMessage()));
    }

    // ─── Exceções de Agendamento ──────────────────────────────────────────────

    @ExceptionHandler(AgendamentoConflitanteException.class)
    public ResponseEntity<Map<String, Object>> handleAgendamentoConflitante(AgendamentoConflitanteException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(HttpStatus.CONFLICT, "Conflito de agendamento", ex.getMessage()));
    }

    @ExceptionHandler(AgendamentoDataPassadaException.class)
    public ResponseEntity<Map<String, Object>> handleAgendamentoDataPassada(AgendamentoDataPassadaException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Data inválida", ex.getMessage()));
    }

    // ─── Exceções de Usuário ──────────────────────────────────────────────────

    @ExceptionHandler(CpfInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCpfInvalido(CpfInvalidoException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(HttpStatus.BAD_REQUEST, "CPF inválido", ex.getMessage()));
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDuplicado(EmailDuplicadoException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(HttpStatus.CONFLICT, "Email duplicado", ex.getMessage()));
    }

    // ─── Validação de campos (@Valid) ─────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errosCampos = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            errosCampos.put(campo, mensagem);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("erro", "Erro de validação");
        body.put("campos", errosCampos);
        return ResponseEntity.badRequest().body(body);
    }

    // ─── Exceção genérica ────────────────────────────────────────────────────

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<Map<String, Object>> handleDadosInvalidos(DadosInvalidosException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Dados inválidos", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", ex.getMessage()));
    }
}
