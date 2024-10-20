DROP TABLE IF EXISTS books;
CREATE TABLE books
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    isbn           VARCHAR(255) NOT NULL UNIQUE,
    title          VARCHAR(255) NOT NULL,
    authors        VARCHAR(255) NOT NULL,
    description    TEXT         NOT NULL,
    categories     VARCHAR(255) NOT NULL,
    published_date DATE             DEFAULT null,
    page_count     INT              DEFAULT NULL,
    thumbnail      VARCHAR(255)     DEFAULT NULL
);

CREATE INDEX idx_isbn ON books (isbn);
