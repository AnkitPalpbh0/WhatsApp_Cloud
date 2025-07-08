CREATE TABLE media_metadata (
    id SERIAL PRIMARY KEY,
    file_name VARCHAR(255),
    mime_type VARCHAR(100),
    url VARCHAR(500)
);
