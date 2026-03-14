# 🧩 Design Patterns com Spring Boot

API REST de gerenciamento de clientes desenvolvida em Java com Spring Boot, com o objetivo de demonstrar na prática os principais **Padrões de Projeto (Design Patterns)** do catálogo GoF aplicados em um contexto real de desenvolvimento backend.

O projeto integra com a API pública [ViaCEP](https://viacep.com.br) para busca automática de endereço a partir do CEP informado, persistindo os dados em um banco H2 em memória.

---

## 🏗️ Padrões de Projeto Implementados

### Strategy
Definido pela interface `ClienteService`, que declara o contrato das operações de negócio (buscar, inserir, atualizar, deletar). A classe `ClienteServiceImpl` é a implementação concreta dessa estratégia.

O controller depende apenas da interface, nunca da implementação direta — o que significa que novas estratégias podem ser criadas e trocadas sem alterar quem as consome.

```java
// O controller conhece apenas o contrato
@Autowired
private ClienteService clienteService; // não sabe que é ClienteServiceImpl
```

---

### Singleton
Todo componente anotado com `@Service`, `@Repository` ou `@RestController` é gerenciado pelo container do Spring como uma única instância durante toda a vida da aplicação. Não é necessário implementar o padrão manualmente — o Spring cuida disso via injeção de dependência com `@Autowired`.

```java
@Service // Spring cria uma única instância e a reutiliza em toda a aplicação
public class ClienteServiceImpl implements ClienteService { ... }
```

---

### Facade
O `ClienteRestController` funciona como uma fachada: ele expõe uma interface REST simples para o mundo externo, escondendo toda a complexidade interna — consulta ao banco H2, integração com a API ViaCEP, cache de endereços e publicação de eventos. Quem consome a API não precisa saber nada disso.

```
POST /clientes  →  valida entrada  →  consulta ViaCEP  →  persiste no H2  →  publica evento  →  retorna resposta
```

---

### Observer
Ao cadastrar um novo cliente, o serviço publica um `ClienteInseridoEvent` via `ApplicationEventPublisher` do Spring. O `ClienteInseridoListener` escuta esse evento e reage de forma completamente desacoplada — o serviço não sabe que o listener existe.

Isso permite adicionar novos comportamentos pós-cadastro (envio de e-mail, integração com CRM, auditoria) sem tocar na lógica de negócio existente.

```java
// Serviço publica o evento sem conhecer quem vai ouvir
eventPublisher.publishEvent(new ClienteInseridoEvent(this, cliente));

// Listener reage de forma independente
@EventListener
public void onClienteInserido(ClienteInseridoEvent event) { ... }
```

---

### Builder
Utilizado na construção dos objetos de resposta da API (`ClienteResponse`) e de erros (`ErroResponse`). O padrão Builder torna a criação de objetos complexos legível, fluente e segura — sem construtores com muitos parâmetros.

```java
return ClienteResponse.builder()
        .id(cliente.getId())
        .nome(cliente.getNome())
        .logradouro(cliente.getEndereco().getLogradouro())
        .localidade(cliente.getEndereco().getLocalidade())
        .uf(cliente.getEndereco().getUf())
        .build();
```

---

## 📐 Arquitetura da Aplicação

```
┌─────────────────────────────────────────────┐
│              Cliente HTTP / Swagger          │
└────────────────────┬────────────────────────┘
                     │ JSON
┌────────────────────▼────────────────────────┐
│         ClienteRestController (Facade)       │
│  • Recebe ClienteRequest (validado com @Valid)│
│  • Devolve ClienteResponse (Builder)         │
└────────────────────┬────────────────────────┘
                     │
┌────────────────────▼────────────────────────┐
│      ClienteService (Strategy interface)     │
│         ClienteServiceImpl (Singleton)       │
│  • Orquestra repositórios e ViaCEP (Facade)  │
│  • Publica eventos de domínio (Observer)     │
└────────┬───────────────────────┬────────────┘
         │                       │
┌────────▼────────┐   ┌──────────▼──────────┐
│  H2 Database    │   │   API ViaCEP         │
│  (JPA + Spring  │   │   (OpenFeign)        │
│   Data)         │   │                      │
└─────────────────┘   └─────────────────────┘
```

---

## ✅ Boas Práticas Aplicadas

- **DTOs de entrada e saída** — `ClienteRequest` e `ClienteResponse` separam o modelo de persistência da API pública, evitando expor detalhes internos do banco
- **Bean Validation** — o CEP e o nome são validados antes de chegar à camada de serviço, com mensagens de erro descritivas
- **Tratamento global de erros** — `GlobalExceptionHandler` com `@RestControllerAdvice` garante respostas de erro consistentes em toda a API
- **Exceções de domínio** — `ClienteNotFoundException` e `CepInvalidoException` tornam os erros explícitos e rastreáveis
- **HTTP status semânticos** — `201 Created` para cadastro, `204 No Content` para deleção, `404` para recurso não encontrado

---

## 🚀 Como Executar

**Pré-requisitos:** Java 11+ e Maven instalados.

```bash
./mvnw spring-boot:run
```

### Endpoints

| Método | URL | Descrição |
|---|---|---|
| `GET` | `/clientes` | Lista todos os clientes |
| `GET` | `/clientes/{id}` | Busca cliente por ID |
| `POST` | `/clientes` | Cadastra novo cliente |
| `PUT` | `/clientes/{id}` | Atualiza cliente existente |
| `DELETE` | `/clientes/{id}` | Remove cliente |

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### H2 Console
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:clientesdb
```

---

## 📥 Exemplos de Uso

**Cadastrar cliente — POST /clientes**
```json
{
  "nome": "Maria da Silva",
  "cep": "01310100"
}
```

**Resposta — 201 Created**
```json
{
  "id": 1,
  "nome": "Maria da Silva",
  "cep": "01310-100",
  "logradouro": "Avenida Paulista",
  "bairro": "Bela Vista",
  "localidade": "São Paulo",
  "uf": "SP"
}
```

**CEP inválido — 400 Bad Request**
```json
{
  "status": 400,
  "mensagem": "CEP inválido ou não encontrado: 00000000",
  "timestamp": "2024-03-14T10:30:00"
}
```

**Cliente não encontrado — 404 Not Found**
```json
{
  "status": 404,
  "mensagem": "Cliente com ID 99 não encontrado.",
  "timestamp": "2024-03-14T10:30:00"
}
```

---

## 📦 Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 11 | Linguagem |
| Spring Boot 2.5.4 | Framework principal |
| Spring Data JPA | Persistência |
| H2 Database | Banco em memória |
| Spring Cloud OpenFeign | Client HTTP para ViaCEP |
| Bean Validation | Validação de entrada |
| springdoc-openapi | Documentação Swagger |

---

## 🗂️ Estrutura de Pacotes

```
src/main/java/one/digitalinnovation/gof/
├── controller/         # Camada REST — Facade
├── dto/                # Objetos de transferência — Builder
├── exception/          # Exceções de domínio e handler global
├── model/              # Entidades JPA e Repositories
├── observer/           # Eventos de domínio e listeners
└── service/
    ├── ClienteService.java         # Interface — Strategy
    ├── ViaCepService.java          # Client Feign
    └── impl/
        └── ClienteServiceImpl.java # Implementação — Singleton + Facade
```