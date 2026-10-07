CREATE TABLE servico (
    id    BIGINT IDENTITY(1,1) NOT NULL,
    nome  VARCHAR(100)         NOT NULL,
    preco NUMERIC(10, 2)       NOT NULL,
    CONSTRAINT pk_servico PRIMARY KEY (id)
);

CREATE TABLE usuarios (
    id       BIGINT IDENTITY(1,1) NOT NULL,
    nome     VARCHAR(100)         NOT NULL,
    email    VARCHAR(150)         NOT NULL,
    telefone VARCHAR(20)          NULL,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);

CREATE TABLE agendamentos (
    id         BIGINT IDENTITY(1,1) NOT NULL,
    usuario_id BIGINT               NOT NULL,
    servico_id BIGINT               NOT NULL,
    data_hora  DATETIME2            NOT NULL,
    status     VARCHAR(20)          NOT NULL,
    CONSTRAINT pk_agendamentos PRIMARY KEY (id),
    CONSTRAINT fk_agendamentos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_agendamentos_servico FOREIGN KEY (servico_id) REFERENCES servico (id),
    CONSTRAINT ck_agendamentos_status CHECK (status IN ('AGENDADO', 'CONCLUIDO', 'CANCELADO'))
);
