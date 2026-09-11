-- ============================================================
-- V2 - Associar pedidos e estoque às unidades
-- ============================================================

-- ------------------------------------------------------------
-- 1. Adiciona unidade_id ao estoque
-- ------------------------------------------------------------

ALTER TABLE estoque
ADD COLUMN unidade_id BIGINT;

-- Como os registros existentes pertencem à unidade 1,
-- associamos o estoque atual à unidade já cadastrada.
UPDATE estoque
SET unidade_id = 1
WHERE unidade_id IS NULL;

-- Torna a unidade obrigatória
ALTER TABLE estoque
ALTER COLUMN unidade_id SET NOT NULL;

-- Cria a chave estrangeira para unidades
ALTER TABLE estoque
ADD CONSTRAINT fk_estoque_unidade
FOREIGN KEY (unidade_id)
REFERENCES unidades(id);

-- Impede dois estoques do mesmo produto na mesma unidade
ALTER TABLE estoque
ADD CONSTRAINT uk_estoque_unidade_produto
UNIQUE (unidade_id, produto_id);


-- ------------------------------------------------------------
-- 2. Adiciona unidade_id aos pedidos
-- ------------------------------------------------------------

ALTER TABLE pedidos
ADD COLUMN unidade_id BIGINT;

-- Os pedidos antigos serão associados à unidade existente
UPDATE pedidos
SET unidade_id = 1
WHERE unidade_id IS NULL;

-- Torna a unidade obrigatória
ALTER TABLE pedidos
ALTER COLUMN unidade_id SET NOT NULL;

-- Cria a chave estrangeira para unidades
ALTER TABLE pedidos
ADD CONSTRAINT fk_pedidos_unidade
FOREIGN KEY (unidade_id)
REFERENCES unidades(id);