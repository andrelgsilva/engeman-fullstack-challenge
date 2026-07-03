# Testes via curl — Engeman Imóveis

Roteiro de testes manuais da API, usando os usuários do seed (`data.sql`). Comandos escritos para **CMD do Windows** (aspas duplas escapadas). Se estiver no Git Bash/Linux/Mac, troque `"{\"campo\": ...}"` por aspas simples `'{"campo": ...}'`.

Ajuste `http://localhost:8080` se seu backend estiver em outra porta.

---

## 1. Autenticação

### 1.1 Login como ADMIN
```
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"email\": \"admin@engeman.com\", \"password\": \"admin123\"}"
```
Guarda o `token` da resposta — vamos chamar de `TOKEN_ADMIN`.

### 1.2 Login como CORRETOR
```
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"email\": \"corretor@engeman.com\", \"password\": \"corretor123\"}"
```
→ `TOKEN_CORRETOR`

### 1.3 Login como segundo CORRETOR (para teste de isolamento)
```
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"email\": \"corretor2@engeman.com\", \"password\": \"corretor123\"}"
```
→ `TOKEN_CORRETOR2`

### 1.4 Login como CLIENTE
```
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"email\": \"cliente@engeman.com\", \"password\": \"cliente123\"}"
```
→ `TOKEN_CLIENTE`

### 1.5 Login com senha errada (deve retornar 401 "E-mail ou senha inválidos")
```
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"email\": \"admin@engeman.com\", \"password\": \"senhaerrada\"}"
```

### 1.6 Registro público (deve criar sempre como CLIENTE)
```
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d "{\"name\": \"Novo Cliente\", \"email\": \"novocliente@teste.com\", \"password\": \"senha123\"}"
```

---

## 2. Listagem pública (sem autenticação)

### 2.1 Listar todos, paginado
```
curl "http://localhost:8080/api/imoveis/buscar?page=0&size=10"
```

### 2.2 Filtro por tipo
```
curl "http://localhost:8080/api/imoveis/buscar?tipo=CASA"
```

### 2.3 Filtro por faixa de preço
```
curl "http://localhost:8080/api/imoveis/buscar?precoMin=200000&precoMax=400000"
```

### 2.4 Filtro por quartos mínimos
```
curl "http://localhost:8080/api/imoveis/buscar?quartosMin=3"
```

### 2.5 Busca por nome
```
curl "http://localhost:8080/api/imoveis/buscar?titulo=Camaragibe"
```

### 2.6 Filtros combinados + ordenação por preço decrescente
```
curl "http://localhost:8080/api/imoveis/buscar?tipo=CASA&quartosMin=2&sortBy=preco&sortDir=desc"
```

### 2.7 Detalhe de um imóvel específico
```
curl http://localhost:8080/api/imoveis/4
```

### 2.8 Imóvel inexistente (deve retornar 404)
```
curl http://localhost:8080/api/imoveis/9999
```

### 2.9 Confirmar que a vitrine pública NÃO mostra o imóvel inativo (id 1, ativo=false no seed)
```
curl "http://localhost:8080/api/imoveis/buscar?titulo=Casa Editada"
```
Deve retornar lista vazia (`content: []`), mesmo o imóvel existindo no banco.

---

## 3. Gestão de imóveis (autenticado)

Substitua `TOKEN_CORRETOR` / `TOKEN_ADMIN` pelo token real capturado no passo 1.

### 3.1 Gestão sem token (deve retornar 401/403)
```
curl http://localhost:8080/api/imoveis/gestao
```

### 3.2 Gestão como CORRETOR (deve ver só os próprios: ids 1, 2, 3, 4)
```
curl http://localhost:8080/api/imoveis/gestao -H "Authorization: Bearer TOKEN_CORRETOR"
```

### 3.3 Gestão como segundo CORRETOR (deve ver só o próprio: id 5)
```
curl http://localhost:8080/api/imoveis/gestao -H "Authorization: Bearer TOKEN_CORRETOR2"
```

### 3.4 Gestão como ADMIN (deve ver todos, incluindo o inativo)
```
curl http://localhost:8080/api/imoveis/gestao -H "Authorization: Bearer TOKEN_ADMIN"
```

### 3.5 Criar imóvel como CORRETOR
```
curl -X POST http://localhost:8080/api/imoveis -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN_CORRETOR" -d "{\"titulo\": \"Imovel via curl\", \"descricao\": \"Criado em teste manual\", \"tipo\": \"CASA\", \"preco\": 200000, \"quartos\": 2, \"banheiros\": 1, \"areaM2\": 60, \"endereco\": \"Rua Teste\", \"cidade\": \"Recife\", \"estado\": \"PE\"}"
```

### 3.6 Criar imóvel como CLIENTE (deve retornar 403)
```
curl -X POST http://localhost:8080/api/imoveis -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN_CLIENTE" -d "{\"titulo\": \"Nao deveria criar\", \"descricao\": \"teste\", \"tipo\": \"CASA\", \"preco\": 200000, \"quartos\": 2, \"banheiros\": 1, \"areaM2\": 60, \"endereco\": \"Rua Teste\", \"cidade\": \"Recife\", \"estado\": \"PE\"}"
```

### 3.7 Editar imóvel próprio (id 2, dono é o CORRETOR)
```
curl -X PUT http://localhost:8080/api/imoveis/2 -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN_CORRETOR" -d "{\"titulo\": \"Apto Editado via curl\", \"descricao\": \"teste123456\", \"tipo\": \"APARTAMENTO\", \"preco\": 310000, \"quartos\": 2, \"banheiros\": 1, \"areaM2\": 70, \"endereco\": \"Rua B\", \"cidade\": \"Recife\", \"estado\": \"PE\"}"
```

### 3.8 Editar imóvel de outro corretor (id 5, dono é o segundo corretor) — deve retornar 403
```
curl -X PUT http://localhost:8080/api/imoveis/5 -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN_CORRETOR" -d "{\"titulo\": \"Tentativa invalida\", \"descricao\": \"teste123456\", \"tipo\": \"TERRENO\", \"preco\": 100000, \"quartos\": 0, \"banheiros\": 0, \"areaM2\": 100, \"endereco\": \"Rua X\", \"cidade\": \"Recife\", \"estado\": \"PE\"}"
```

### 3.9 Desativar imóvel próprio
```
curl -X PATCH http://localhost:8080/api/imoveis/2/status -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN_CORRETOR" -d "{\"ativo\": false}"
```

### 3.10 Reativar
```
curl -X PATCH http://localhost:8080/api/imoveis/2/status -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN_CORRETOR" -d "{\"ativo\": true}"
```

---

## 4. Favoritos (CLIENTE)

### 4.1 Listar favoritos do cliente do seed (deve trazer os imóveis 2 e 4)
```
curl http://localhost:8080/api/favoritos -H "Authorization: Bearer TOKEN_CLIENTE"
```

### 4.2 Favoritar um novo imóvel
```
curl -X POST http://localhost:8080/api/favoritos/3 -H "Authorization: Bearer TOKEN_CLIENTE"
```

### 4.3 Favoritar o mesmo imóvel de novo (deve retornar 400 "Imóvel já está nos favoritos")
```
curl -X POST http://localhost:8080/api/favoritos/3 -H "Authorization: Bearer TOKEN_CLIENTE"
```

### 4.4 Favoritar um imóvel inativo (id 1) — deve retornar 400 "Não é possível favoritar um imóvel inativo"
```
curl -X POST http://localhost:8080/api/favoritos/1 -H "Authorization: Bearer TOKEN_CLIENTE"
```

### 4.5 Remover um favorito
```
curl -X DELETE http://localhost:8080/api/favoritos/3 -H "Authorization: Bearer TOKEN_CLIENTE"
```

### 4.6 Favoritar como CORRETOR (deve retornar 403 — favoritar é exclusivo de CLIENTE)
```
curl -X POST http://localhost:8080/api/favoritos/2 -H "Authorization: Bearer TOKEN_CORRETOR"
```

---

## 5. Gestão de usuários (ADMIN)

### 5.1 Criar novo corretor
```
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN_ADMIN" -d "{\"name\": \"Corretor via curl\", \"email\": \"corretorcurl@engeman.com\", \"password\": \"senha123\", \"role\": \"CORRETOR\"}"
```

### 5.2 Criar usuário como CORRETOR (deve retornar 403 — só ADMIN pode)
```
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN_CORRETOR" -d "{\"name\": \"Nao deveria criar\", \"email\": \"naodeveria@engeman.com\", \"password\": \"senha123\", \"role\": \"CORRETOR\"}"
```

### 5.3 Listar usuários (só ADMIN)
```
curl http://localhost:8080/api/users -H "Authorization: Bearer TOKEN_ADMIN"
```

---

## Resumo do que cada bloco valida

| Bloco | O que confirma |
|---|---|
| 1 | Login, geração de token, mensagens de erro de credenciais |
| 2 | Listagem pública, filtros, paginação, ordenação, esconde inativos, 404 |
| 3 | CRUD de imóveis, isolamento entre corretores (403), ADMIN vê tudo |
| 4 | Regras de favoritos (duplicidade, imóvel inativo, restrição por role) |
| 5 | Restrição de criação de usuário só para ADMIN |