package org.example.lb295.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Rezept")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RezeptId")
    private int rezeptId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "KategorieId")
    private Category category;

    @Column(name = "Name", length = 100)
    private String name;

    @Column(name = "Beschreibung", length = 500)
    private String beschreibung;

    @Column(name = "Zubereitungszeit")
    private int zubereitungszeit;

    @Column(name = "Kosten", precision = 10, scale = 2)
    private BigDecimal kosten;

    @Column(name = "ErstelltAm")
    private LocalDateTime erstelltAm;

    @Column(name = "Vegetarisch")
    private boolean vegetarisch;

    @Column(name = "Bewertung", precision = 3, scale = 1)
    private BigDecimal bewertung;

    public int getRezeptId() { return rezeptId; }
    public void setRezeptId(int rezeptId) { this.rezeptId = rezeptId; }

    public Category getKategorie() { return category; }
    public void setKategorie(Category category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public int getZubereitungszeit() { return zubereitungszeit; }
    public void setZubereitungszeit(int zubereitungszeit) { this.zubereitungszeit = zubereitungszeit; }

    public BigDecimal getKosten() { return kosten; }
    public void setKosten(BigDecimal kosten) { this.kosten = kosten; }

    public LocalDateTime getErstelltAm() { return erstelltAm; }
    public void setErstelltAm(LocalDateTime erstelltAm) { this.erstelltAm = erstelltAm; }

    public boolean isVegetarisch() { return vegetarisch; }
    public void setVegetarisch(boolean vegetarisch) { this.vegetarisch = vegetarisch; }

    public BigDecimal getBewertung() { return bewertung; }
    public void setBewertung(BigDecimal bewertung) { this.bewertung = bewertung; }
}
