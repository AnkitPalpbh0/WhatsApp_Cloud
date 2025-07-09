CREATE TABLE chat (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    chat_number VARCHAR(50),
    external_id UUID,
    created_at TIMESTAMP,
    CONSTRAINT fk_chat_user FOREIGN KEY (user_id) REFERENCES users(id)
);