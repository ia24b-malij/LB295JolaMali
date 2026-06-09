package org.example.lb295.services;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.lb295.daos.CategoryDAO;
import org.example.lb295.models.Category;

import java.util.List;

@Path("/kategorien")
public class CategoryResource {

    private static final Logger logger = LogManager.getLogger(CategoryResource.class);

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public List<Category> getAll() {
        logger.info("GET all categories");
        return categoryDAO.getAll();
    }

    @POST
    @RolesAllowed({"ADMIN", "USER"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Category category) {
        logger.info("POST create new category: {}", category.getName());
        if (category.getName() == null || category.getName().isBlank()) {
            logger.warn("Validation error: Name is empty");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: Name cannot be empty.").build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(categoryDAO.create(category)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "USER"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, Category category) {
        logger.info("PUT update category ID: {}", id);
        if (category.getName() == null || category.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: Name cannot be empty.").build();
        }
        Category result = categoryDAO.update(id, category);
        if (result == null) {
            logger.warn("Category ID {} not found", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Category not found.").build();
        }
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Produces(MediaType.TEXT_PLAIN)
    public Response delete(@PathParam("id") int id) {
        logger.info("DELETE category ID: {}", id);
        if (!categoryDAO.delete(id)) {
            logger.warn("Category ID {} could not be deleted", id);
            return Response.status(Response.Status.CONFLICT)
                    .entity("Category not found or has recipes.").build();
        }
        return Response.ok("Category " + id + " deleted.").build();
    }
}
