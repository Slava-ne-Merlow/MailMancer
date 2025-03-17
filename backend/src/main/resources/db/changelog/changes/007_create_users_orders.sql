CREATE TABLE IF NOT EXISTS users_orders (
                              user_entity_id BIGINT NOT NULL REFERENCES users(id),
                              orders_id BIGINT NOT NULL REFERENCES orders(id)
);

