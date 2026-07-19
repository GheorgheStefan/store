CREATE TABLE products
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(150)   NOT NULL,
    description VARCHAR(1000),
    price       NUMERIC(10, 2) NOT NULL,
    stock       INTEGER        NOT NULL,
    category    VARCHAR(50)    NOT NULL,

    CONSTRAINT chk_products_price_non_negative
        CHECK (price >= 0),

    CONSTRAINT chk_products_stock_non_negative
        CHECK (stock >= 0)
);