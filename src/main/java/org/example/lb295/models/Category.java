package org.example.lb295.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Kategorie")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KategorieId")
    private int kategorieId;

    @Column(name = "Name", length = 45)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Recipe> rezepte;

    public int getKategorieId() { return kategorieId; }
    public void setKategorieId(int kategorieId) { this.kategorieId = kategorieId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Recipe> getRezepte() { return rezepte; }
    public void setRezepte(List<Recipe> rezepte) { this.rezepte = rezepte; }
}
