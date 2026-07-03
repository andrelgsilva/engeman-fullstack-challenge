# Engeman Imóveis

Aplicação full-stack para gestão e consulta de imóveis, com autenticação via JWT e controle de permissões por perfil de usuário (ADMIN, CORRETOR, CLIENTE).

Desenvolvido como desafio técnico para vaga de Desenvolvedor Full-Stack.

## Funcionalidades

- Autenticação com e-mail/senha via JWT
- Três níveis de perfil, cada um com regras de acesso próprias:
  - **ADMIN**: cria usuários (incluindo corretores), cria imóveis e gerencia todos os imóveis do sistema
  - **CORRETOR**: cria imóveis e gerencia apenas os que ele mesmo cadastrou
  - **CLIENTE**: consulta, lista, favorita e vê detalhes de imóveis
- CRUD completo de imóveis, com ativação/desativação (soft delete)
- Listagem pública com filtros (tipo, faixa de preço, quartos mínimos, busca por nome), paginação e ordenação
- Tela de gestão de imóveis para ADMIN/CORRETOR
- Tela de favoritos para CLIENTE
- Estados de interface obrigatórios (loading, erro, vazio) em todas as telas de dados

## Stack

**Backend**
- Java 21, Spring Boot 3.5
- Spring Security + JWT (jjwt 0.12)
- Spring Data JPA + PostgreSQL
- Jakarta Validation
- JUnit 5 + Mockito

**Frontend**
- React 19 + TypeScript
- React Router
- React Hook Form + Zod
- Context API para autenticação
- Tailwind CSS
- Axios

**Infraestrutura**
- Docker Compose (PostgreSQL + backend + frontend)

## Pré-requisitos

- [Docker](https://www.docker.com/) e Docker Compose
- (Opcional, para rodar fora do Docker) Java 21, Node 20+, PostgreSQL 16

## Como rodar

### 1. Configurar variáveis de ambiente

Copie o arquivo de exemplo e ajuste os valores:

```bash
cp .env.example .env
```

Gere uma chave JWT própria em vez de usar o valor de exemplo:

```bash
openssl rand -base64 32
```

Cole o resultado na variável `JWT_SECRET` do `.env`. Veja a seção [Variáveis de ambiente](#variáveis-de-ambiente) para o significado de cada uma.

### 2. Subir a aplicação

Na raiz do projeto:

```bash
docker compose up --build
```

Isso sobe, nessa ordem: PostgreSQL → backend (aguardando o banco ficar saudável) → frontend.

- Frontend: [http://localhost:5173](http://localhost:5173)
- Backend: [http://localhost:8080](http://localhost:8080)
- PostgreSQL: `localhost:5432`

Para parar e remover os containers:

```bash
docker compose down
```

Para também apagar os dados do banco (recomeçar do zero):

```bash
docker compose down -v
```

## Credenciais de teste (seed automático)

O banco já sobe populado com um usuário de cada perfil, via `data.sql`:

| Perfil | E-mail | Senha |
|---|---|---|
| ADMIN | `admin@engeman.com` | `admin123` |
| CORRETOR | `corretor@engeman.com` | `corretor123` |
| CORRETOR (segundo, para testar isolamento) | `corretor2@engeman.com` | `corretor123` |
| CLIENTE | `cliente@engeman.com` | `cliente123` |

## Variáveis de ambiente

| Variável | Descrição | Usada por |
|---|---|---|
| `POSTGRES_DB` | Nome do banco de dados | postgres, backend |
| `POSTGRES_USER` | Usuário do banco | postgres, backend |
| `POSTGRES_PASSWORD` | Senha do banco | postgres, backend |
| `JWT_SECRET` | Chave usada para assinar os tokens JWT (Base64, mínimo 256 bits) | backend |
| `JWT_EXPIRATION` | Tempo de expiração do token, em milissegundos | backend |
| `VITE_API_URL` | URL base da API que o navegador vai chamar | frontend |

## Regras de autorização

| Ação | ADMIN | CORRETOR | CLIENTE |
|---|:---:|:---:|:---:|
| Criar usuário / cadastrar corretor | ✅ | ❌ | ❌ |
| Criar imóvel | ✅ | ✅ | ❌ |
| Editar/ativar/desativar imóvel próprio | ✅ | ✅ | ❌ |
| Editar/ativar/desativar imóvel de outro corretor | ✅ | ❌ | ❌ |
| Listar/ver detalhes de imóveis | ✅ | ✅ | ✅ |
| Favoritar imóvel | ❌ | ❌ | ✅ |

## Rodando os testes

Backend:

```bash
cd backend
./mvnw test
```

## Estrutura do projeto

```
engeman/
├── docker-compose.yml
├── .env.example
├── backend/
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/engeman/imoveis/
│       │   ├── controller/     # endpoints REST
│       │   ├── service/        # regras de negócio
│       │   ├── repository/     # acesso a dados (Spring Data JPA)
│       │   ├── entity/         # entidades JPA
│       │   ├── dto/            # request/response
│       │   ├── security/       # JWT e configuração de autenticação
│       │   ├── config/         # Spring Security
│       │   └── exception/      # tratamento global de erros
│       └── test/               # testes unitários
└── frontend/
    ├── Dockerfile
    └── src/
        ├── pages/               # telas (auth, imoveis, gestao, favoritos)
        ├── components/          # componentes reutilizáveis
        ├── contexts/            # AuthContext
        ├── services/            # chamadas à API (axios)
        └── types/               # tipos TypeScript compartilhados
```

## Principais endpoints da API

| Método | Rota | Acesso |

| `POST` | `/auth/register` | Público |
| `POST` | `/auth/login` | Público |
| `GET` | `/api/imoveis/buscar` | Público (filtros, paginação, ordenação) |
| `GET` | `/api/imoveis/{id}` | Público |
| `GET` | `/api/imoveis/gestao` | ADMIN, CORRETOR (filtrado por dono) |
| `POST` | `/api/imoveis` | ADMIN, CORRETOR |
| `PUT` | `/api/imoveis/{id}` | ADMIN, CORRETOR (dono) |
| `PATCH` | `/api/imoveis/{id}/status` | ADMIN, CORRETOR (dono) |
| `GET` / `POST` / `DELETE` | `/api/favoritos` | CLIENTE |
| `POST` | `/api/users` | ADMIN |

mvnw.cmd spring-boot:run

npm run dev