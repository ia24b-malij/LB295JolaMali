-- Datenbank erstellen
CREATE DATABASE IF NOT EXISTS `lb295_db`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE `lb295_db`;

-- Tabelle kategorie
DROP TABLE IF EXISTS `rezept`;
DROP TABLE IF EXISTS `kategorie`;

CREATE TABLE `kategorie` (
  `KategorieId` int(11)     NOT NULL AUTO_INCREMENT,
  `Name`        varchar(45) DEFAULT NULL,
  PRIMARY KEY (`KategorieId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Tabelle rezept
CREATE TABLE `rezept` (
  `RezeptId`         int(11)       NOT NULL AUTO_INCREMENT,
  `Beschreibung`     varchar(500)  DEFAULT NULL,
  `Bewertung`        decimal(3,1)  DEFAULT NULL,
  `ErstelltAm`       datetime(6)   DEFAULT NULL,
  `Kosten`           decimal(10,2) DEFAULT NULL,
  `Name`             varchar(100)  DEFAULT NULL,
  `Vegetarisch`      bit(1)        DEFAULT NULL,
  `Zubereitungszeit` int(11)       DEFAULT NULL,
  `KategorieId`      int(11)       DEFAULT NULL,
  PRIMARY KEY (`RezeptId`),
  CONSTRAINT `FK_rezept_kategorie`
    FOREIGN KEY (`KategorieId`) REFERENCES `kategorie` (`KategorieId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Seed-Daten: kategorie
INSERT INTO `kategorie` (`Name`) VALUES ('Italienisch');
INSERT INTO `kategorie` (`Name`) VALUES ('Asiatisch');

-- Seed-Daten: rezept
INSERT INTO `rezept` (`Name`, `Beschreibung`, `Zubereitungszeit`, `Vegetarisch`, `Bewertung`, `Kosten`, `ErstelltAm`, `KategorieId`)
VALUES ('Spaghetti Carbonara', 'Klassisches italienisches Pasta-Gericht', 30, 0, 4.5, 12.50, '2026-05-01 12:00:00', 1);

INSERT INTO `rezept` (`Name`, `Beschreibung`, `Zubereitungszeit`, `Vegetarisch`, `Bewertung`, `Kosten`, `ErstelltAm`, `KategorieId`)
VALUES ('Pad Thai', 'Gebratene Reisnudeln mit Erdnuessen', 25, 1, 4.0, 10.00, '2026-05-05 18:00:00', 2);
