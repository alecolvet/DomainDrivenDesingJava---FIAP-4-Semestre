package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByLeadId(Long leadId);
    List<Agendamento> findByOperadorId(Long operadorId);
    List<Agendamento> findByStatus(String status);
    List<Agendamento> findByLembreteEnviadoFalse();
    boolean existsByOperadorIdAndDataHora(Long operadorId, LocalDate dataHora);
}
