CREATE SEQUENCE mstore.albums_id_seq
    INCREMENT BY 1
    START WITH 9;
CREATE SEQUENCE mstore.artists_id_seq
    INCREMENT BY 1
    START WITH 6;
CREATE SEQUENCE mstore.authors_id_seq
    INCREMENT BY 1
    START WITH 11;
CREATE SEQUENCE mstore.producers_id_seq
    INCREMENT BY 1
    START WITH 6;
CREATE SEQUENCE mstore.record_labels_id_seq
    INCREMENT BY 1
    START WITH 6;
CREATE SEQUENCE mstore.songs_id_seq
    INCREMENT BY 1
    START WITH 94;

CREATE TABLE mstore.artists(
    "id"         BIGINT DEFAULT nextval('mstore.artists_id_seq') PRIMARY KEY,
    "first_name" VARCHAR(255) NOT NULL,
    "last_name"  VARCHAR(255) NOT NULL,
    "birth_date" DATE
);
CREATE TABLE mstore.authors(
    "id"          BIGINT DEFAULT nextval('mstore.authors_id_seq') PRIMARY KEY,
    "first_name"  VARCHAR(255) NOT NULL,
    "last_name"   VARCHAR(255) NOT NULL,
    "birth_date"  DATE,
    "author_type" VARCHAR(255) NOT NULL
);
CREATE TABLE mstore.producers(
    "id"         BIGINT DEFAULT nextval('mstore.producers_id_seq') PRIMARY KEY,
    "first_name" VARCHAR(255) NOT NULL,
    "last_name"  VARCHAR(255) NOT NULL,
    "birth_date" DATE
);
CREATE TABLE mstore.record_labels(
    "id"              BIGINT DEFAULT nextval('mstore.record_labels_id_seq') PRIMARY KEY,
    "name"            VARCHAR(255) NOT NULL,
    "country"         VARCHAR(255),
    "foundation_year" INTEGER
);
CREATE TABLE mstore.albums(
    "id"              BIGINT DEFAULT nextval('mstore.albums_id_seq') PRIMARY KEY,
    "title"           VARCHAR(255) NOT NULL,
    "issue_year"      INTEGER,
    "price"           INTEGER NOT NULL,
    "genre"           VARCHAR(75),
    "artist_id"       BIGINT REFERENCES mstore.artists (id) NOT NULL,
    "record_label_id" BIGINT REFERENCES mstore.record_labels (id)
);
CREATE TABLE mstore.songs(
    "id"           BIGINT DEFAULT nextval('mstore.songs_id_seq') PRIMARY KEY,
    "album_id"     BIGINT REFERENCES mstore.albums (id) NOT NULL,
    "order_number" INTEGER NOT NULL,
    "title"        VARCHAR(255) NOT NULL
);

CREATE TABLE mstore.album_producer(
    "album_id"    BIGINT REFERENCES mstore.albums (id) NOT NULL,
    "producer_id" BIGINT REFERENCES mstore.producers (id) NOT NULL,
    CONSTRAINT album_producer_pkey PRIMARY KEY (album_id, producer_id)
);
CREATE TABLE mstore.song_author(
    "song_id" BIGINT REFERENCES mstore.songs (id) NOT NULL,
    "author_id" BIGINT REFERENCES mstore.authors (id) NOT NULL,
    CONSTRAINT song_author_pkey PRIMARY KEY (song_id, author_id)
);