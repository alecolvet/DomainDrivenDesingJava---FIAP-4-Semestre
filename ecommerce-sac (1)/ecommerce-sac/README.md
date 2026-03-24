# 🛒 E-Commerce SAC — Serviço de Atendimento ao Cliente

> **Módulo:** SAC (Serviço de Atendimento ao Cliente)  
> **Domínio:** www.ecommerce.com.br  
> **Disciplina:** Domain Driven Design using Java — FIAP  

---

## 📋 Sobre o Projeto

Este projeto implementa o módulo de **SAC (Serviço de Atendimento ao Cliente)** de um e-commerce. O sistema permite que clientes abram tickets de suporte, acompanhem o status dos atendimentos e consultem seu histórico completo.

---

## 🗂️ Estrutura do Projeto

```
ecommerce-sac/
├── banco/
│   └── create_tables.sql              ← Script para criar as tabelas no Oracle
└── src/main/java/br/com/ecommerce/sac/
    ├── model/
    │   ├── Cliente.java               ← Entidade Cliente
    │   └── Ticket.java                ← Entidade Ticket (chamado de SAC)
    ├── exception/
    │   └── SacException.java          ← Exception customizada do módulo
    ├── connection/
    │   └── ConexaoBD.java             ← Padrão Singleton — conexão com o BD
    ├── dao/
    │   ├── ClienteDAO.java            ← Interface DAO de Cliente
    │   ├── TicketDAO.java             ← Interface DAO de Ticket
    │   └── impl/
    │       ├── ClienteDAOImpl.java    ← Implementação JDBC do ClienteDAO
    │       └── TicketDAOImpl.java     ← Implementação JDBC do TicketDAO
    ├── factory/
    │   └── DAOFactory.java            ← Padrão Factory — cria instâncias de DAO
    ├── service/
    │   └── SacService.java            ← Regras de negócio e 3 funcionalidades
    └── main/
        └── Main.java                  ← Ponto de entrada — demonstração completa
```

---

## ✅ Funcionalidades Implementadas

| # | Funcionalidade | Descrição |
|---|---------------|-----------|
| 1 | **Abertura de Ticket** | Cria um novo chamado de suporte vinculado a um cliente, com validação de prioridade e campos obrigatórios |
| 2 | **Atualização de Status** | Altera o status do ticket (`ABERTO → EM_ANDAMENTO → RESOLVIDO → FECHADO`) com regras de transição |
| 3 | **Histórico do Cliente** | Lista todos os tickets de um cliente com resumo formatado |

---

## 🏗️ Padrões de Projeto Aplicados

| Padrão | Classe | Descrição |
|--------|--------|-----------|
| **Singleton** | `ConexaoBD` | Garante uma única instância de conexão com o banco Oracle |
| **DAO** | `TicketDAO`, `ClienteDAO` e suas implementações | Separa a lógica de persistência da lógica de negócio |
| **Factory** | `DAOFactory` | Centraliza a criação dos objetos DAO, desacoplando o código |

---

## 🗄️ Banco de Dados

### Tabelas

**`sac_clientes`**
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | NUMBER (PK) | Identificador auto-gerado |
| nome | VARCHAR2(100) | Nome do cliente |
| email | VARCHAR2(150) | E-mail único |
| cpf | VARCHAR2(14) | CPF único |
| telefone | VARCHAR2(20) | Telefone de contato |

**`sac_tickets`**
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | NUMBER (PK) | Identificador auto-gerado |
| titulo | VARCHAR2(200) | Título do chamado |
| descricao | VARCHAR2(1000) | Descrição detalhada |
| status | VARCHAR2(20) | `ABERTO`, `EM_ANDAMENTO`, `RESOLVIDO`, `FECHADO` |
| prioridade | VARCHAR2(10) | `BAIXA`, `MEDIA`, `ALTA`, `CRITICA` |
| cliente_id | NUMBER (FK) | Referência ao cliente |
| data_criacao | TIMESTAMP | Data de abertura |
| data_atualizacao | TIMESTAMP | Última atualização |

---

## 🚀 Como Executar

### Pré-requisitos

- Java 17+ (JDK instalado)
- Oracle Database XE (ou acesso a um banco Oracle)
- Driver JDBC Oracle (`ojdbc8.jar`) — [Download aqui](https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html)
- IDE: IntelliJ IDEA, Eclipse ou VS Code com extensão Java

### Passo a Passo

**1. Clone o repositório**
```bash
git clone https://github.com/seu-usuario/ecommerce-sac.git
cd ecommerce-sac
```

**2. Configure o banco de dados**

Execute o script SQL no Oracle:
```bash
sqlplus usuario/senha@localhost:1521/xe @banco/create_tables.sql
```
Ou abra o arquivo `banco/create_tables.sql` no SQL Developer e execute.

**3. Configure as credenciais de banco**

Abra o arquivo `ConexaoBD.java` e altere as constantes:
```java
private static final String URL     = "jdbc:oracle:thin:@localhost:1521:xe";
private static final String USUARIO = "seu_usuario";
private static final String SENHA   = "sua_senha";
```

**4. Adicione o driver JDBC ao projeto**

No IntelliJ: `File → Project Structure → Modules → Dependencies → + → ojdbc8.jar`

**5. Execute a aplicação**

```
Rode a classe: br.com.ecommerce.sac.main.Main
```

---

## 🎯 Saída esperada no console

```
══════════════════════════════════════════════════════════════
    MÓDULO SAC — Serviço de Atendimento ao Cliente
    www.ecommerce.com.br
══════════════════════════════════════════════════════════════

[ConexaoBD] Conexão estabelecida com sucesso!
[DAOFactory] Criando TicketDAOImpl...
[DAOFactory] Criando ClienteDAOImpl...

>>> Cadastrando clientes...
✅ Cliente cadastrado: Cliente[id=1, nome='João Silva', ...]

>>> Funcionalidade 1: Abertura de Tickets
✅ Ticket #1 aberto com sucesso para o cliente: João Silva

>>> Funcionalidade 2: Atualização de Status
✅ Status do Ticket #1 atualizado para: EM_ANDAMENTO

>>> Funcionalidade 3: Histórico de Tickets
📋 Histórico de Tickets — Cliente: João Silva
────────────────────────────────────────────────────────────
  #1    | EM_ANDAMENTO | ALTA       | Produto não entregue
  #2    | EM_ANDAMENTO | CRITICA    | Produto com defeito

>>> Demonstração da SacException:
  ⚠ Exception capturada: [SAC_TICKET_TITULO] O título do ticket não pode ser vazio.

>>> Demonstração do Singleton:
  Instância 1: 12345678
  Instância 2: 12345678
  São a mesma instância? true

[ConexaoBD] Conexão encerrada.
══════════════════════════════════════════════════════════════
    Execução finalizada.
══════════════════════════════════════════════════════════════
```

---

## 👥 Integrantes do Grupo

- Aluno 1 — RM: XXXXX
- Aluno 2 — RM: XXXXX
- Aluno 3 — RM: XXXXX
