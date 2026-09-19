# Tasklist API

API REST para gerenciamento de tarefas de uma equipe: projetos, responsáveis, tarefas com ciclo de vida, etiquetas e comentários. Atividade final da disciplina Programação Web Back-end I.

Não há interface gráfica. O entregável é a API, consumível por qualquer cliente HTTP.

## Stack

- Java 25
- Spring Boot 4.1 (Spring Web MVC, Spring Data JPA)
- PostgreSQL 17
- Flyway (versionamento do schema)
- Lombok
- springdoc-openapi (Swagger UI)

## Requisitos de ambiente

- JDK 25
- Docker (para o PostgreSQL)
- Maven não precisa estar instalado: o projeto usa o wrapper `./mvnw`

## Como rodar

1. Subir o banco:

   ```bash
   docker compose up -d
   ```

   Cria um PostgreSQL na porta `5438` com banco `tasklist`, usuário e senha `postgres`.

2. Subir a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

   No primeiro start o Flyway cria as tabelas automaticamente. A API fica em `http://localhost:8080`.

3. Documentação interativa: `http://localhost:8080/swagger-ui.html`

## Banco de dados

PostgreSQL rodando em container (`compose.yaml`). O schema é gerenciado pelo Flyway em `src/main/resources/db/migration`; o Hibernate roda em modo `validate`, ou seja, apenas confere que as entidades batem com as tabelas.

Modelo:

- `project` 1:N `task`
- `owner` 1:N `task` (opcional: a tarefa pode ficar sem responsável)
- `task` N:N `tag`, via entidade explícita `task_tag`
- `task` 1:N `comment`

Enums (`status`, `priority`) são gravados como texto (`@Enumerated(EnumType.STRING)`).

## Organização do código

Pacotes por domínio, e dentro de cada domínio as camadas separadas:

```
com.antoniodias.tasklist
├── project/     controller, service, repository, entity, dto
├── owner/       controller, service, repository, entity, dto
├── tag/         controller, service, repository, entity, dto
├── task/        controller, service, repository, entity, dto, enums
├── comment/     controller, service, repository, entity, dto
└── shared/      exceção de domínio, tratamento global de erros e configuração de logging
```

- Controllers só traduzem HTTP e delegam ao service; toda regra fica no service.
- Injeção por construtor em todas as classes.
- Entrada e saída da API usam DTOs (records); entidades JPA não trafegam nas requisições.

## Endpoints

Nomes de recursos, campos e valores de enum estão em inglês. Correspondência com o enunciado: Projeto → `project`, Responsável → `owner`, Tarefa → `task`, Etiqueta → `tag`, Comentário → `comment`.

### Projetos

| Método | Rota | Sucesso |
|---|---|---|
| POST | `/projects` | 201 + `Location` |
| GET | `/projects` | 200 |
| GET | `/projects/{id}/tasks` | 200 (paginado; 404 se o projeto não existe) |

### Responsáveis

| Método | Rota | Sucesso |
|---|---|---|
| POST | `/owners` | 201 + `Location` |
| GET | `/owners` | 200 |

### Etiquetas

| Método | Rota | Sucesso |
|---|---|---|
| POST | `/tags` | 201 + `Location` |
| GET | `/tags` | 200 |

### Tarefas

| Método | Rota | Sucesso | Falha |
|---|---|---|---|
| POST | `/tasks` | 201 + `Location` | 404 (projeto/responsável inexistente) |
| GET | `/tasks?status=&projectId=&page=&size=&sort=` | 200 (paginado) | |
| GET | `/tasks/{id}` | 200 | 404 |
| PUT | `/tasks/{id}` | 200 | 404 |
| PUT | `/tasks/{id}/complete` | 200 | 404 |
| DELETE | `/tasks/{id}` | 204 | 404 |
| GET | `/tasks/{id}/tags` | 200 | 404 |
| POST | `/tasks/{id}/tags` | 201 | 404 |
| DELETE | `/tasks/{id}/tags/{tagId}` | 204 | 404 |
| GET | `/tasks/{id}/comments` | 200 | 404 |
| POST | `/tasks/{id}/comments` | 201 | 404 |

Valores válidos: `status` = `NEW`, `IN_PROGRESS`, `DONE`, `CANCELED`; `priority` = `LOW`, `MEDIUM`, `HIGH`.

`GET /tasks` e `GET /projects/{id}/tasks` são paginados: `page` (0-based, padrão 0), `size` (padrão 20), `sort` (ex.: `sort=dueDate,asc`; padrão `createdAt,desc`). A resposta tem o formato `{ "content": [...], "page": { "size", "number", "totalElements", "totalPages" } }`.

### Exemplo: criar tarefa

`POST /tasks`

```json
{
  "title": "Review supplier contract",
  "description": "Check price adjustment clauses before renewal",
  "priority": "HIGH",
  "dueDate": "2026-09-30",
  "projectId": "9b3fcd04-2937-4b0b-915a-8bfddb4ce291",
  "ownerId": "30b210e9-5462-44f3-9dac-a68cfa95b7c6"
}
```

Resposta `201 Created`, `Location: http://localhost:8080/tasks/52cf772c-...`:

```json
{
  "id": "52cf772c-301a-4f2b-922f-0cf8f5459f2c",
  "title": "Review supplier contract",
  "description": "Check price adjustment clauses before renewal",
  "status": "NEW",
  "priority": "HIGH",
  "dueDate": "2026-09-30",
  "createdAt": "2026-09-19T10:54:07.166396",
  "completedAt": null,
  "projectId": "9b3fcd04-2937-4b0b-915a-8bfddb4ce291",
  "ownerId": "30b210e9-5462-44f3-9dac-a68cfa95b7c6"
}
```

### Regras de negócio

- A tarefa nasce com `status = NEW` e `createdAt` preenchido pelo sistema; `status` enviado no POST é ignorado. `priority` omitida vira `MEDIUM`.
- No PUT todos os campos são substituídos (`owner` omitido fica sem responsável, `priority` omitida vira `MEDIUM`), exceto `status`, que é preservado quando omitido.
- Toda tarefa pertence a um projeto existente. Responsável é opcional.
- Ao mudar o status para `DONE` (via `PUT /tasks/{id}` ou `PUT /tasks/{id}/complete`), `completedAt` é preenchido. Ao sair de `DONE`, é limpo. Concluir uma tarefa já concluída não altera `completedAt`.
- Vincular uma etiqueta já vinculada não duplica o vínculo.
- Remover uma tarefa remove seus comentários e vínculos de etiqueta.

### Erros

Todo erro devolve o mesmo corpo, sem stack trace:

```json
{
  "error": "Task not found: 52cf772c-301a-4f2b-922f-0cf8f5459f2c",
  "timestamp": "2026-09-19T13:54:07.505386Z"
}
```

| Situação | Status |
|---|---|
| Recurso inexistente | 404 |
| Campo obrigatório ausente (`Missing required field: name`) | 400 |
| JSON malformado, enum ou UUID inválido, campo de ordenação inexistente | 400 |
| Violação de integridade (ex.: e-mail duplicado) | 409 |
| Rota inexistente / método não suportado | 404 / 405 |

## Verificação

O arquivo `api.http` na raiz contém o roteiro completo de verificação (seção 7 do enunciado) mais os bônus e os cenários de erro. Abra no IntelliJ (HTTP Client) e execute as requisições em ordem: os ids gerados são capturados automaticamente para as requisições seguintes. Em outros clientes, substitua as variáveis `{{projectId}}`, `{{ownerId}}`, `{{taskId}}` e `{{tagId}}` pelos ids retornados.

## Fora do escopo

Conforme o enunciado: autenticação/autorização, Bean Validation e testes automatizados ficam para Back-end II.
