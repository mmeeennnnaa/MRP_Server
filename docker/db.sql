CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
    );
CREATE TABLE IF NOT EXISTS media (
    id SERIAL PRIMARY KEY,
    media_type VARCHAR(50), -- 'Movie', 'Series', 'Game'
    title VARCHAR(255) NOT NULL,
    description TEXT,
    release_year INT,
    age_restriction INT,
    genres TEXT,
    creator_id INT REFERENCES users(id) -- Verknüpfung: Welcher User hat es erstellt?
);