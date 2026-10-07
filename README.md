# Barbearia — API de Agendamentos

API REST para gerenciar os agendamentos de uma barbearia: cadastro de serviços e clientes, marcação de horários com verificação de conflito e cancelamento de agendamentos.

> Projeto de estudo desenvolvido para aprender **Spring Boot** na prática, aplicando arquitetura em camadas, validação de dados, tratamento centralizado de erros e regras de negócio reais.

---

##  Sumário

- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Endpoints](#-endpoints)
- [Exemplos de requisição](#-exemplos-de-requisição)
- [Tratamento de erros](#-tratamento-de-erros)
- [Como rodar localmente](#-como-rodar-localmente)
- [Demonstração](#-demonstração)
- [Próximos passos](#-próximos-passos)
- [Autor](#-autor)

---

##  Funcionalidades

- **Serviços:** cadastro e listagem dos serviços oferecidos (ex.: corte, barba), com preço.
- **Clientes:** cadastro e listagem de clientes, com **e-mail único**.
- **Agendamentos:**
  - Marcação de horário ligando um cliente a um serviço.
  - **Bloqueio de conflito:** não é possível agendar dois clientes no mesmo horário.
  - **Cancelamento lógico:** o agendamento não é apagado, apenas muda para `CANCELADO`, preservando o histórico e liberando o horário.
- **Validação de dados** com mensagens claras por campo (nome obrigatório, e-mail válido, preço positivo, data no futuro etc.).
- **Respostas de erro padronizadas** em JSON, com o status HTTP correto para cada situação.

---

## Tecnologias

| Tecnologia | Uso no projeto |
|---|---|
| **Java 25** | Linguagem |
| **Spring Boot 4.1** | Base da aplicação |
| **Spring Web MVC** | Criação da API REST |
| **Spring Data JPA / Hibernate** | Persistência e mapeamento objeto-relacional |
| **Jakarta Bean Validation** | Validação dos dados de entrada |
| **SQL Server** | Banco de dados relacional |
| **Maven** | Gerenciamento de dependências e build |

---

## Arquitetura

O projeto segue a **arquitetura em camadas**, em que cada camada tem uma responsabilidade:

```
Requisição HTTP
      │
      ▼
 Controller   → recebe a requisição, valida a entrada (@Valid) e devolve a resposta HTTP
      │
      ▼
  Service     → regras de negócio (e-mail único, conflito de horário, cancelamento)
      │
      ▼
 Repository   → acesso ao banco de dados (Spring Data JPA)
      │
      ▼
 SQL Server
```

Os erros lançados em qualquer camada são capturados por um **`@RestControllerAdvice`** global, que os converte em respostas JSON padronizadas.

### Estrutura de pacotes

```
src/main/java/com/example/main
├── controller   # Endpoints REST
├── dto          # Objetos de entrada e de resposta de erro
├── exception    # Exceções de negócio e handler global
├── model        # Entidades JPA (Servico, Usuario, Agendamento)
├── repository   # Interfaces Spring Data JPA
└── service      # Regras de negócio
```

### Modelo de dados

```
usuarios (1) ───< (N) agendamentos (N) >─── (1) servico
```

Cada agendamento pertence a um cliente e a um serviço, por meio de chaves estrangeiras (`@ManyToOne`).

---

## Endpoints

### Serviços

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/servicos` | Lista todos os serviços |
| `POST` | `/servicos` | Cadastra um serviço |

### Clientes

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/usuarios` | Lista todos os clientes |
| `POST` | `/usuarios` | Cadastra um cliente |

### Agendamentos

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/agendamentos` | Lista todos os agendamentos |
| `GET` | `/agendamentos/{id}` | Busca um agendamento pelo ID |
| `POST` | `/agendamentos` | Cria um agendamento |
| `PATCH` | `/agendamentos/{id}/cancelar` | Cancela um agendamento |

---

## Exemplos de requisição

### Cadastrar serviço

`POST /servicos`

```json
{
  "nome": "Corte Masculino",
  "preco": 45.00
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "nome": "Corte Masculino",
  "preco": 45.00
}
```

### Cadastrar cliente

`POST /usuarios`

```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "(11) 99999-0000"
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "(11) 99999-0000"
}
```

### Criar agendamento

`POST /agendamentos`

```json
{
  "usuarioId": 1,
  "servicoId": 1,
  "dataHora": "2026-12-10T14:30:00"
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "usuario": {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@email.com",
    "telefone": "(11) 99999-0000"
  },
  "servico": {
    "id": 1,
    "nome": "Corte Masculino",
    "preco": 45.00
  },
  "dataHora": "2026-12-10T14:30:00",
  "status": "AGENDADO"
}
```

### Cancelar agendamento

`PATCH /agendamentos/1/cancelar`

Resposta `200 OK`: o agendamento com `"status": "CANCELADO"`. O horário volta a ficar disponível para novos agendamentos.

---

## Tratamento de erros

Todos os erros seguem o mesmo formato:

```json
{
  "status": 409,
  "erro": "Horário indisponível",
  "timestamp": "2026-10-06T15:44:00.405"
}
```

Erros de validação incluem o campo `campos`, com a mensagem de cada campo inválido:

```json
{
  "status": 400,
  "erro": "Dados inválidos",
  "timestamp": "2026-10-06T15:39:21.210",
  "campos": {
    "nome": "O nome é obrigatório",
    "email": "O e-mail deve ser válido"
  }
}
```

| Status | Quando acontece |
|---|---|
| `400 Bad Request` | Dados inválidos, JSON mal formatado ou parâmetro com tipo errado |
| `404 Not Found` | Cliente, serviço ou agendamento não encontrado |
| `409 Conflict` | E-mail já cadastrado, horário indisponível ou agendamento que não pode ser cancelado |

---

## Como rodar localmente

### Pré-requisitos

- [JDK 25](https://adoptium.net/)
- [SQL Server](https://www.microsoft.com/sql-server/sql-server-downloads) (a edição Developer ou Express funciona)
- Não é necessário instalar o Maven: o projeto usa o **Maven Wrapper** (`mvnw`).

### 1. Clonar o repositório

```bash
git clone https://github.com/PedroComper/Barbearia-agendamento-API.git
cd Barbearia-agendamento-API
```

### 2. Preparar o SQL Server

No **SQL Server Configuration Manager**, verifique se o protocolo **TCP/IP** está habilitado na porta **1433**. Nas propriedades do servidor, a autenticação deve estar no modo **"SQL Server e Windows"**.

Depois, crie o banco e o usuário da aplicação:

```sql
CREATE DATABASE db_agendamento;
GO

CREATE LOGIN seu_usuario WITH PASSWORD = 'sua_senha';
GO

USE db_agendamento;
GO
CREATE USER seu_usuario FOR LOGIN seu_usuario;
ALTER ROLE db_owner ADD MEMBER seu_usuario;
GO
```

As tabelas são criadas automaticamente pelo Hibernate na primeira execução.

### 3. Configurar as credenciais

As credenciais **não ficam no repositório**. Copie o arquivo de exemplo:

```bash
cp src/main/resources/secrets.properties.example src/main/resources/secrets.properties
```

E preencha com os dados do passo anterior:

```properties
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
```

> Como alternativa, as variáveis de ambiente `DB_USERNAME`, `DB_PASSWORD` e `DB_URL` também são aceitas, e têm prioridade sobre o arquivo.

### 4. Executar

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A API estará disponível em **http://localhost:8080**.

---

## Demonstração

<!-- Adicione os prints na pasta docs/images e descomente as linhas abaixo -->
<!-- ![Criando um agendamento](docs/images/criar-agendamento.png) -->
<!-- ![Erro de horário indisponível](docs/images/horario-indisponivel.png) -->

*Em breve.*

---

## Próximos passos

- [ ] Testes unitários com JUnit e Mockito
- [ ] Documentação interativa com Swagger / OpenAPI
- [ ] DTOs de entrada e saída para todas as entidades
- [ ] Migrações de banco de dados com Flyway
- [ ] Interface web para gerenciar os agendamentos

---

## Autor

Pedro Lucas Oliveira Comper

- LinkedIn: www.linkedin.com/in/pedro-lucas-oliveira-comper-92b42426b
- GitHub: [@PedroComper](https://github.com/PedroComper)
