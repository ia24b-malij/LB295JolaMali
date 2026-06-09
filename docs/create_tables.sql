-- Datenbank erstellen
CREATE DATABASE IF NOT EXISTS lb295_db;
USE lb295_db;

-- Tabelle Kategorie (Tabelle B)
CREATE TABLE IF NOT EXISTS Kategorie (
    KategorieId INT AUTO_INCREMENT PRIMARY KEY,
    Name        VARCHAR(45) NOT NULL
);

-- Tabelle Rezept (Tabelle A)
CREATE TABLE IF NOT EXISTS Rezept (
    RezeptId          INT AUTO_INCREMENT PRIMARY KEY,
    KategorieId       INT NOT NULL,
    Name              VARCHAR(100) NOT NULL,
    Beschreibung      VARCHAR(500),
    Zubereitungszeit  INT NOT NULL,
    Kosten            DECIMAL(10, 2),
    ErstelltAm        DATETIME,
    Vegetarisch       BOOLEAN,
    Bewertung         DECIMAL(3, 1),
    CONSTRAINT fk_kategorie FOREIGN KEY (KategorieId) REFERENCES Kategorie(KategorieId)
);

-- Seed-Daten Kategorie
INSERT INTO Kategorie (Name) VALUES ('Italienisch');
INSERT INTO Kategorie (Name) VALUES ('Asiatisch');

-- Seed-Daten Rezept
INSERT INTO Rezept (KategorieId, Name, Beschreibung, Zubereitungszeit, Kosten, ErstelltAm, Vegetarisch, Bewertung)
VALUES (1, 'Spaghetti Carbonara', 'Klassisches italienisches Pasta-Gericht', 30, 12.50, '2026-05-01 12:00:00', false, 4.5);

INSERT INTO Rezept (KategorieId, Name, Beschreibung, Zubereitungszeit, Kosten, ErstelltAm, Vegetarisch, Bewertung)
VALUES (2, 'Pad Thai', 'Gebratene Reisnudeln mit Erdnuessen', 25, 10.00, '2026-05-05 18:00:00', true, 4.0);
