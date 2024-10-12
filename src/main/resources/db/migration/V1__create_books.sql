DROP TABLE IF EXISTS books;
CREATE TABLE books
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    isbn           VARCHAR(255) NOT NULL UNIQUE,
    title          VARCHAR(255) NOT NULL,
    author         VARCHAR(255) NOT NULL,
    published_year INT
);
