package org.example.lb295;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.example.lb295.kategorien.services.Kategorien;
import org.example.lb295.rezepte.services.Rezepte;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class JerseyConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(Rezepte.class);
        classes.add(Kategorien.class);
        classes.add(AuthenticationFilter.class);
        return classes;
    }
}
