package org.example.lb295.configs;

import org.example.lb295.services.CategoryResource;
import org.example.lb295.services.RecipeResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(RecipeResource.class);
        register(CategoryResource.class);
        register(AuthenticationFilter.class);
    }
}
