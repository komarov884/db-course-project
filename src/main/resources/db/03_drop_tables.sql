DROP TABLE IF EXISTS mstore.song_author;
DROP TABLE IF EXISTS mstore.album_producer;

DROP TABLE IF EXISTS mstore.songs;
DROP TABLE IF EXISTS mstore.albums;
DROP TABLE IF EXISTS mstore.record_labels;
DROP TABLE IF EXISTS mstore.producers;
DROP TABLE IF EXISTS mstore.authors;
DROP TABLE IF EXISTS mstore.artists;

DROP SEQUENCE IF EXISTS mstore.songs_id_seq;
DROP SEQUENCE IF EXISTS mstore.record_labels_id_seq;
DROP SEQUENCE IF EXISTS mstore.producers_id_seq;
DROP SEQUENCE IF EXISTS mstore.authors_id_seq;
DROP SEQUENCE IF EXISTS mstore.artists_id_seq;
DROP SEQUENCE IF EXISTS mstore.albums_id_seq;