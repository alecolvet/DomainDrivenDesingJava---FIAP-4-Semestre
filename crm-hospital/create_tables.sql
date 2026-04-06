-- ============================================================
--  CRM Hospital São Rafael — Script de criação das tabelas
--  Banco: Oracle (FIAP)
--  Execute este script antes de rodar a aplicação.
-- ============================================================

-- ── Tabela: USUARIO (operadores/atendentes do hospital) ─────
CREATE TABLE USUARIO (
    ID        NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NOME      VARCHAR2(100)  NOT NULL,
    EMAIL     VARCHAR2(100)  NOT NULL UNIQUE,
    SENHA     VARCHAR2(100)  NOT NULL,
    DOC       VARCHAR2(11)   NOT NULL UNIQUE,   -- CPF sem pontuação
    TELEFONE  NUMBER(11),
    DATANASC  DATE           NOT NULL
);

-- ── Tabela: LEAD (pacientes potenciais / prospects) ─────────
CREATE TABLE LEAD (
    ID                      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NOME                    VARCHAR2(100)  NOT NULL,
    EMAIL                   VARCHAR2(100)  NOT NULL UNIQUE,
    TELEFONE                NUMBER(11),
    CANAL_ORIGEM            VARCHAR2(50),
    STATUS                  VARCHAR2(30)   DEFAULT 'Novo',
    LEAD_SCORE              VARCHAR2(20),
    PRIORIDADE              NUMBER(1),
    FATOR_URGENCIA          NUMBER(1)      DEFAULT 0,   -- 0=não, 1=sim
    PLANO_SAUDE             VARCHAR2(100),
    PROCEDIMENTO_INTERESSE  VARCHAR2(200),
    CRIADO_EM               DATE           DEFAULT SYSDATE
);

-- ── Tabela: AGENDAMENTO (consultas/procedimentos agendados) ──
CREATE TABLE AGENDAMENTO (
    ID           NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    LEAD_ID      NUMBER         NOT NULL REFERENCES LEAD(ID),
    OPERADOR_ID  NUMBER         NOT NULL REFERENCES USUARIO(ID),
    PROCEDIMENTO VARCHAR2(200),
    DATA_HORA    DATE           NOT NULL,
    STATUS       VARCHAR2(30)   DEFAULT 'Pendente',
    CRIADO_EM    DATE           DEFAULT SYSDATE
);
