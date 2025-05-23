
CREATE TABLE IF NOT EXISTS users (
                       id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
                       login VARCHAR(255) unique ,
                       name VARCHAR(255),
                       email VARCHAR(255) unique,
                       password VARCHAR(255),
                       role SMALLINT CHECK (role BETWEEN 0 AND 1),
                       token VARCHAR(255) unique ,
                       company_id BIGINT NOT NULL REFERENCES user_companies(id)
);
