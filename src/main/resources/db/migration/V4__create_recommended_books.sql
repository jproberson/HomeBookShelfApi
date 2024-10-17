DROP TABLE IF EXISTS "recommended_books";
CREATE TABLE recommended_books
(
    id                      UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    user_id                 UUID         NOT NULL,
    book_id                 UUID         NOT NULL,
    recommended_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recommendation_strategy VARCHAR(255) not NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_book_id FOREIGN KEY (book_id) REFERENCES books (id),
    UNIQUE (user_id, book_id)
);
