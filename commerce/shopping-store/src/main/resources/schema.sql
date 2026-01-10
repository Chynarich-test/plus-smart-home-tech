CREATE TABLE IF NOT EXISTS products(
    product_id UUID PRIMARY KEY,
    product_name VARCHAR(254) NOT NULL,
    description TEXT NOT NULL,
    image_src VARCHAR(1000),
    quantity_state VARCHAR(100) NOT NULL,
    product_state VARCHAR(100) NOT NULL,
    product_category VARCHAR(100),
    price REAL NOT NULL
);