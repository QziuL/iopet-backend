-- 1. Habilitar a extensão PostGIS para tratamento de coordenadas e polígonos
CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. Tabela de Tutor
CREATE TABLE IF NOT EXISTS tutor (
    id BIGSERIAL PRIMARY KEY,
    id_publico UUID NOT NULL UNIQUE,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    senha VARCHAR(255) NOT NULL,
    url_foto VARCHAR(500),
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    editado_em TIMESTAMP WITH TIME ZONE,
    excluido_em TIMESTAMP WITH TIME ZONE,
    ativo BOOLEAN DEFAULT TRUE
);

-- 3. Tabela de Pet
CREATE TABLE IF NOT EXISTS pet (
    id BIGSERIAL PRIMARY KEY,
    id_publico UUID NOT NULL UNIQUE,
    tutor_id BIGINT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    raca VARCHAR(100),
    especie VARCHAR(50) NOT NULL,
    descricao TEXT,
    url_foto VARCHAR(500),
    data_nascimento DATE,
    zona_seguranca GEOMETRY(Polygon, 4326),
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    editado_em TIMESTAMP WITH TIME ZONE,
    excluido_em TIMESTAMP WITH TIME ZONE,
    ativo BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_pet_tutor FOREIGN KEY (tutor_id) REFERENCES tutor(id) ON DELETE CASCADE
);

-- 4. Tabela de Dispositivo IoT (rastreador físico ESP32)
CREATE TABLE IF NOT EXISTS dispositivo_iot (
    endereco_mac VARCHAR(17) PRIMARY KEY,
    pet_id BIGINT UNIQUE,
    bateria_nivel INT DEFAULT 100,
    ativo BOOLEAN DEFAULT TRUE,
    ultima_comunicacao TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_dispositivo_pet FOREIGN KEY (pet_id) REFERENCES pet(id) ON DELETE SET NULL
);

-- 5. Tabela de Histórico de Localização
CREATE TABLE IF NOT EXISTS historico_localizacao (
    id BIGSERIAL PRIMARY KEY,
    localizador_endereco_mac VARCHAR(17) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    ponto GEOMETRY(Point, 4326),
    data_registro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historico_dispositivo FOREIGN KEY (localizador_endereco_mac) REFERENCES dispositivo_iot(endereco_mac) ON DELETE CASCADE
);

-- 6. Tabela de Alertas de Geofencing
CREATE TABLE IF NOT EXISTS alerta_geofencing (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL,
    historico_id BIGINT,
    mensagem VARCHAR(255) NOT NULL,
    data_disparo TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    visualizado BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_alerta_pet FOREIGN KEY (pet_id) REFERENCES pet(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerta_historico FOREIGN KEY (historico_id) REFERENCES historico_localizacao(id) ON DELETE SET NULL
);

-- Índices espaciais para acelerar consultas de cruzamento geográfico
CREATE INDEX IF NOT EXISTS idx_pet_zona_seguranca ON pet USING GIST (zona_seguranca);
CREATE INDEX IF NOT EXISTS idx_historico_ponto ON historico_localizacao USING GIST (ponto);