CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY,
    fragile BOOLEAN,
    weight REAL NOT NULL,
    width REAL NOT NULL,
    height REAL NOT NULL,
    depth REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS stocks (
    product_id UUID REFERENCES products(product_id) PRIMARY KEY,
    quantity BIGINT NOT NULL
);