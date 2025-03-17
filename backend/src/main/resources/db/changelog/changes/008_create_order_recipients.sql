CREATE TABLE IF NOT EXISTS order_recipients (
                                  order_id BIGINT NOT NULL REFERENCES orders(id),
                                  recipient_id BIGINT NOT NULL REFERENCES carrier_representative(id),
                                  PRIMARY KEY (order_id, recipient_id)
);
