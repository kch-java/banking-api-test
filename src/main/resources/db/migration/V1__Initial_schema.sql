CREATE TABLE account
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    beneficiary_name VARCHAR(255)   NOT NULL,
    account_number   VARCHAR(255)   NOT NULL,
    pin              VARCHAR(255)   NOT NULL,
    balance          DECIMAL(19, 2) NOT NULL
);

CREATE TABLE transaction
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT         NOT NULL,
    type       VARCHAR(255)   NOT NULL,
    amount     DECIMAL(19, 2) NOT NULL,
    timestamp  TIMESTAMP      NOT NULL,
    FOREIGN KEY (account_id) REFERENCES account (id)
);
