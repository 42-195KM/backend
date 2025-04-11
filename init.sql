CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE IF NOT EXISTS vector_store (
                                            id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    content text,
    metadata json,
    embedding vector(768)
    );
CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);

CREATE DATABASE achievement;
CREATE DATABASE alert;
CREATE DATABASE chatbot;
CREATE DATABASE competition;
CREATE DATABASE crew;
CREATE DATABASE ranking;
CREATE DATABASE runningrecord;
CREATE DATABASE userrecap;
CREATE DATABASE users
