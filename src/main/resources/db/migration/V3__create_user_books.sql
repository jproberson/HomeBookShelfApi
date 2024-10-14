DROP TABLE IF EXISTS "user_books";
CREATE TABLE user_books
(
    id       UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    user_id  UUID      NOT NULL,
    book_id  UUID      NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_book_id FOREIGN KEY (book_id) REFERENCES books (id),
    UNIQUE (user_id, book_id)
);

CREATE INDEX idx_user_books_user_id ON user_books (user_id);
