CREATE TABLE IF NOT EXISTS cargo_spaces (
                              id BIGSERIAL PRIMARY KEY,
                              quantity BIGINT NOT NULL,
                              weight DOUBLE PRECISION NOT NULL,
                              length DOUBLE PRECISION NOT NULL,
                              height DOUBLE PRECISION NOT NULL,
                              width DOUBLE PRECISION NOT NULL,
                              order_id BIGINT NOT NULL,
                              CONSTRAINT fk_order
                                  FOREIGN KEY (order_id)
                                      REFERENCES orders(id)
                                      ON DELETE CASCADE
);
