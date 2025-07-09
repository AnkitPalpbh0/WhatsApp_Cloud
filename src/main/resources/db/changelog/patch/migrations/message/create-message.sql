CREATE TABLE message (
    id SERIAL PRIMARY KEY,
    chat_id INT NOT NULL,
    message_id VARCHAR NOT NULL,
    status VARCHAR(50),
    message_type VARCHAR(50),
    content TEXT,
    media_id INT,
    timestamp TIMESTAMP,
    CONSTRAINT fk_message_chat FOREIGN KEY (chat_id) REFERENCES chat(id),
    CONSTRAINT fk_message_media FOREIGN KEY (media_id) REFERENCES media_metadata(id)
);
