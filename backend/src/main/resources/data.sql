-- =====================================================
-- Seed de dados para ambiente de desenvolvimento/Docker
-- Credenciais de teste (senha em texto puro, só para referência):
--   admin@engeman.com     / admin123     (ADMIN)
--   corretor@engeman.com  / corretor123  (CORRETOR)
--   corretor2@engeman.com / corretor123  (CORRETOR - usado para testar isolamento entre corretores)
--   cliente@engeman.com   / cliente123   (CLIENTE)
-- =====================================================

DELETE FROM favoritos;
DELETE FROM imoveis;
DELETE FROM users;

-- =====================
-- USERS
-- =====================
INSERT INTO users (id, name, email, password, role, active, created_at) VALUES
(1, 'Admin', 'admin@engeman.com', '$2b$10$5LNQhG9AFprW3fy8axPU7eZwPvL331IAT9vnpW/PUmX8EmeknWo9O', 'ADMIN', true, now()),
(2, 'Corretor Teste', 'corretor@engeman.com', '$2b$10$YN42s81.1S8QNmOFM5BiAuVYYwhhIvd1IvyJ.lCWy/d5wkCeofzEO', 'CORRETOR', true, now()),
(3, 'Segundo Corretor', 'corretor2@engeman.com', '$2b$10$YN42s81.1S8QNmOFM5BiAuVYYwhhIvd1IvyJ.lCWy/d5wkCeofzEO', 'CORRETOR', true, now()),
(4, 'Cliente Teste', 'cliente@engeman.com', '$2b$10$MRKfp4ujDR4kBHCtCDoLse5jKCzCqNoBSlAyrdvFgl968IxjOLL4i', 'CLIENTE', true, now());

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- =====================
-- IMOVEIS
-- =====================
-- corretor_id 2 = Corretor Teste | corretor_id 3 = Segundo Corretor
INSERT INTO imoveis (
    id, titulo, descricao, tipo, preco, quartos, banheiros,
    area_m2, endereco, cidade, estado, ativo, corretor_id,
    created_at, updated_at
) VALUES
(1, 'Casa Editada pelo Admin', 'Teste de alteracao de descricao', 'CASA', 500000.00, 3, 2,
 120.00, 'Rua A', 'Recife', 'PE', false, 2,
 now(), now()),

(2, 'Apto do Corretor', 'Apartamento reformado, pronto para morar', 'APARTAMENTO', 300000.00, 2, 1,
 70.00, 'Rua B', 'Recife', 'PE', true, 2,
 now(), now()),

(3, 'Casa do Corretor', 'Casa ampla com quintal', 'CASA', 250000.00, 2, 1,
 80.00, 'Rua C', 'Recife', 'PE', true, 2,
 now(), now()),

(4, 'Casa com 3 quartos em Camaragibe', 'Casa de 120 m² em Camaragibe possui um total de 7 cômodos, compostos por 3 quartos amplos, 2 banheiros, uma acolhedora sala de estar e uma cozinha integrada.', 'CASA', 450000.00, 3, 2,
 120.00, 'Rua fictícia - Bosque Fictício, n67', 'Camaragibe', 'PE', true, 2,
 now(), now()),

(5, 'Terreno do Segundo Corretor', 'Terreno plano, pronto para construir', 'TERRENO', 180000.00, 0, 0,
 300.00, 'Rua D', 'Recife', 'PE', true, 3,
 now(), now());

SELECT setval('imoveis_id_seq', (SELECT MAX(id) FROM imoveis));

-- =====================
-- FAVORITOS
-- =====================
-- cliente_id 4 = Cliente Teste
INSERT INTO favoritos (id, cliente_id, imovel_id, created_at) VALUES
(1, 4, 2, now()),
(2, 4, 4, now());

SELECT setval('favoritos_id_seq', (SELECT MAX(id) FROM favoritos));