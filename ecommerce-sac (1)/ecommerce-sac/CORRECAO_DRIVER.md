# 🔧 Como Resolver: "No suitable driver found"

O erro acontece porque o driver JDBC do Oracle (`ojdbc.jar`) não está no classpath do projeto.
Escolha **uma** das duas opções abaixo:

---

## ✅ OPÇÃO 1 — Maven (Recomendada, mais fácil)

O projeto já tem um `pom.xml` configurado. Basta:

1. No IntelliJ, clique com o botão direito no arquivo `pom.xml`
2. Selecione **"Add as Maven Project"** (ou "Open as Maven Project")
3. Aguarde o IntelliJ baixar as dependências automaticamente
4. Execute a classe `Main.java` normalmente

> Se aparecer uma janela pedindo para "Trust the Maven project", clique em **Trust**.

---

## ✅ OPÇÃO 2 — Adicionar o JAR manualmente (sem Maven)

Se preferir não usar Maven:

### Passo 1 — Baixar o ojdbc
- Acesse: https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html
- Baixe o **ojdbc11.jar** (ou ojdbc8.jar para Java 8)

### Passo 2 — Adicionar ao projeto no IntelliJ
1. `File` → `Project Structure` (atalho: `Ctrl+Alt+Shift+S`)
2. Clique em `Modules` no menu lateral
3. Aba `Dependencies`
4. Clique no `+` → `JARs or Directories...`
5. Selecione o arquivo `ojdbc11.jar` que você baixou
6. Clique `OK` → `Apply` → `OK`

### Passo 3 — Execute novamente
- Rode a classe `Main.java` — o erro deve sumir

---

## 🔑 Credenciais do Banco (Servidor FIAP)

Já configuradas em `ConexaoBD.java`:
```
URL:      jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl
Usuário:  rm560059
Senha:    fiap24   ← verifique se está correta
```

> Se a senha estiver errada, altere a linha `SENHA` em `ConexaoBD.java`.
