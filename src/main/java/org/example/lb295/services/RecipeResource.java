package org.example.lb295.services;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.lb295.daos.RecipeDAO;
import org.example.lb295.models.Recipe;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Path("/rezepte")
public class RecipeResource {

    private static final Logger logger = LogManager.getLogger(RecipeResource.class);

    private final RecipeDAO recipeDAO = new RecipeDAO();

    private Response validate(Recipe recipe) {
        if (recipe.getName() == null || recipe.getName().isBlank())
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: Name cannot be empty.").build();

        if (recipe.getName().length() > 100)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: Name cannot exceed 100 characters.").build();

        if (recipe.getZubereitungszeit() <= 0)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: Preparation time must be greater than 0.").build();

        if (recipe.getBewertung() != null) {
            double b = recipe.getBewertung().doubleValue();
            if (b < 1.0 || b > 5.0)
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error: Rating must be between 1.0 and 5.0.").build();
        }

        if (recipe.getErstelltAm() != null && recipe.getErstelltAm().isAfter(LocalDateTime.now()))
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: Created date cannot be in the future.").build();

        return null;
    }

    @GET
    @Path("/ping")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public Response ping() {
        logger.info("GET ping");
        return Response.ok("API is running").build();
    }

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<Recipe> getAll() {
        logger.info("GET all recipes");
        return recipeDAO.getAll();
    }

    @GET
    @Path("/count")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response count() {
        logger.info("GET recipes count");
        return Response.ok(recipeDAO.count()).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") int id) {
        logger.info("GET recipe ID: {}", id);
        Recipe recipe = recipeDAO.getById(id);
        if (recipe == null) {
            logger.warn("Recipe ID {} not found", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Recipe not found.").build();
        }
        return Response.ok(recipe).build();
    }

    @GET
    @Path("/filter")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response filter(@QueryParam("vegetarisch") Boolean vegetarisch,
                           @QueryParam("minBewertung") BigDecimal minBewertung) {
        if (vegetarisch != null) {
            logger.info("GET recipes filtered by vegetarian={}", vegetarisch);
            return Response.ok(recipeDAO.getByVegetarisch(vegetarisch)).build();
        }
        if (minBewertung != null) {
            logger.info("GET recipes filtered by minimum rating={}", minBewertung);
            return Response.ok(recipeDAO.getByMinBewertung(minBewertung)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("Error: Please provide vegetarisch or minBewertung parameter.").build();
    }

    @POST
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Recipe recipe) {
        logger.info("POST create new recipe: {}", recipe.getName());
        Response error = validate(recipe);
        if (error != null) return error;
        return Response.status(Response.Status.CREATED)
                .entity(recipeDAO.create(recipe)).build();
    }

    @POST
    @Path("/batch")
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBatch(List<Recipe> rezepte) {
        logger.info("POST batch create {} recipes", rezepte.size());
        for (Recipe recipe : rezepte) {
            Response error = validate(recipe);
            if (error != null) return error;
        }
        return Response.status(Response.Status.CREATED)
                .entity(recipeDAO.createBatch(rezepte)).build();
    }

    @POST
    @Path("/init")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.TEXT_PLAIN)
    public Response init() {
        logger.info("POST init - Creating seed data");
        recipeDAO.createSeedData();
        return Response.status(Response.Status.CREATED)
                .entity("Seed data created successfully.").build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, Recipe recipe) {
        logger.info("PUT update recipe ID: {}", id);
        Response error = validate(recipe);
        if (error != null) return error;
        Recipe result = recipeDAO.update(id, recipe);
        if (result == null) {
            logger.warn("Recipe ID {} not found", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Recipe not found.").build();
        }
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(@PathParam("id") int id) {
        logger.info("DELETE recipe ID: {}", id);
        if (!recipeDAO.delete(id)) {
            logger.warn("Recipe ID {} not found", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Recipe not found.").build();
        }
        return Response.ok("Recipe " + id + " deleted.").build();
    }

    @DELETE
    @Path("/vor/{datum}")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteBeforeDate(@PathParam("datum") String datumStr) {
        logger.info("DELETE recipes before date: {}", datumStr);
        LocalDateTime datum = LocalDateTime.parse(datumStr);
        int anzahl = recipeDAO.deleteBeforeDate(datum);
        return Response.ok(anzahl + " recipe(s) deleted.").build();
    }
}
