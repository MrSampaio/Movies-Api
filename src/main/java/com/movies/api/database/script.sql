CREATE DATABASE cinefc;
USE cinefc;

CREATE TABLE movie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    synopsis TEXT NOT NULL,
    cover_url VARCHAR(500) NOT NULL
);

CREATE TABLE showtime (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movie(id)
);

CREATE TABLE showtime_seat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    showtime_id BIGINT NOT NULL,
    row_letter CHAR(1) NOT NULL,
    seat_number INT NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (showtime_id) REFERENCES showtime(id)
);

INSERT INTO movie (title, synopsis, cover_url) VALUES 
(
    'Todo Mundo em Pânico', 
    'Vinte e seis anos depois de escaparem de um assassino mascarado suspeitosamente familiar, o Quarteto Fantástico está de volta à mira do criminoso, e nenhuma propriedade intelectual de filmes de terror está a salvo.', 
    'https://www.themoviedb.org/t/p/w600_and_h900_face/y9yJd2qIIwhZcllHeKHsz5eRvNr.jpg'
);

INSERT INTO showtime (movie_id, start_time) VALUES 
(1, '2026-11-13 15:00:00'),
(1, '2026-11-13 20:30:00');

-- Inserindo a malha 6x6 (36 assentos) para o Horário 1 (15:00)
-- Alguns assentos estão marcados como false (ocupados) para teste
INSERT INTO showtime_seat (showtime_id, row_letter, seat_number, is_available) VALUES
(1, 'A', 1, true),  (1, 'A', 2, false), (1, 'A', 3, false), (1, 'A', 4, true),  (1, 'A', 5, true),  (1, 'A', 6, true),
(1, 'B', 1, true),  (1, 'B', 2, true),  (1, 'B', 3, false), (1, 'B', 4, false), (1, 'B', 5, true),  (1, 'B', 6, true),
(1, 'C', 1, true),  (1, 'C', 2, true),  (1, 'C', 3, true),  (1, 'C', 4, true),  (1, 'C', 5, true),  (1, 'C', 6, true),
(1, 'D', 1, false), (1, 'D', 2, false), (1, 'D', 3, true),  (1, 'D', 4, true),  (1, 'D', 5, true),  (1, 'D', 6, true),
(1, 'E', 1, true),  (1, 'E', 2, true),  (1, 'E', 3, true),  (1, 'E', 4, true),  (1, 'E', 5, false), (1, 'E', 6, false),
(1, 'F', 1, true),  (1, 'F', 2, true),  (1, 'F', 3, true),  (1, 'F', 4, true),  (1, 'F', 5, true),  (1, 'F', 6, true);

INSERT INTO showtime_seat (showtime_id, row_letter, seat_number, is_available) VALUES
(2, 'A', 1, true),  (2, 'A', 2, true),  (2, 'A', 3, true),  (2, 'A', 4, true),  (2, 'A', 5, true),  (2, 'A', 6, true),
(2, 'B', 1, true),  (2, 'B', 2, true),  (2, 'B', 3, true),  (2, 'B', 4, true),  (2, 'B', 5, true),  (2, 'B', 6, true),
(2, 'C', 1, true),  (2, 'C', 2, true),  (2, 'C', 3, true),  (2, 'C', 4, true),  (2, 'C', 5, true),  (2, 'C', 6, true),
(2, 'D', 1, true),  (2, 'D', 2, true),  (2, 'D', 3, true),  (2, 'D', 4, true),  (2, 'D', 5, true),  (2, 'D', 6, true),
(2, 'E', 1, true),  (2, 'E', 2, true),  (2, 'E', 3, false), (2, 'E', 4, false), (2, 'E', 5, true),  (2, 'E', 6, true),
(2, 'F', 1, true),  (2, 'F', 2, true),  (2, 'F', 3, true),  (2, 'F', 4, true),  (2, 'F', 5, true),  (2, 'F', 6, true);
