package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("productoPrecio")
@Produces(MediaType.APPLICATION_JSON)
public class ProductoPrecioResource extends GeneralRest implements Serializable {

    @Inject
    private ProductoPrecioBean productoPrecioBean;
    @Context
    private UriInfo uriInfo;

    @GET
    public Response findAll(@QueryParam("first") @DefaultValue("0") int firts,
                            @QueryParam("max") @DefaultValue("20") int max){
        try {
            List<ProductoPrecio> precios = productoPrecioBean.findRange(firts, max);
            return Response.ok(precios).build();
        }catch (Exception e) {
            return responseExcepcions(e,null);
        }
    }


    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") Long id){
        try {
            ProductoPrecio precio = productoPrecioBean.findById(id);
            if (precio == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(precio).build();
        }catch (Exception e) {
            return responseExcepcions(e,null);
        }
    }
    /**
     @GET
     @Path("{id}")
     public Response getById(@PathParam("id") Long id) {
     try {
     ProductoPrecio entity = productoPrecioBean.findById(id);
     return Response.ok(entity).build();
     } catch (EntityNotFoundException e) {
     throw new NotFoundException("ProductoPrecio no encontrado con ID: " + id);
     }
     }
     **/
    @Path("{idProducto}")
    @POST
    public Response create(@PathParam("idProducto") Long idProducto, ProductoPrecio precio) {
        try {

            String[] url=uriInfo.getAbsolutePath().toString().split("/");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < url.length - 1; i++) {
                if (!url[i].isEmpty()) { // para evitar dobles slash iniciales
                    sb.append("/").append(url[i]);
                }
            }
            productoPrecioBean.create(precio,idProducto);
            String urlNueva=sb.toString()+"/"+precio.getIdProductoPrecio();
            return Response.created(URI.create(urlNueva)).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, ProductoPrecio precio){
        if (precio == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El cuerpo de la solicitud no puede estar vacio")
                    .build();
        }
        if (!id.equals(precio.getIdProductoPrecio())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID en la ruta no coincide con el ID de la solicitud")
                    .build();
        }
        try {
            productoPrecioBean.update(precio,id);
            return Response.ok(precio).build();
        }catch (Exception e) {
            return responseExcepcions(e,id);
        }
    }


    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            productoPrecioBean.delete(id);
            return Response.ok().build();
        }  catch (Exception e) {
            return responseExcepcions(e,id);
        }
    }


    @GET
    @Path("producto/{idProducto}")
    public Response findByIdProducto(@PathParam("idProducto") Long idProducto,
                                     @QueryParam("first")@DefaultValue("0") int first,
                                     @QueryParam("max")@DefaultValue("20") int max){
        if (idProducto == null || idProducto < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID del producto no puede ser nulo o menor que 0.")
                    .build();
        }
        try {
            ProductoPrecio precios=productoPrecioBean.findByIdProducto(idProducto);
            return Response.ok(precios).build();
        }catch (Exception e) {
            return responseExcepcions(e,idProducto);
        }
    }

    @GET
    @Path("producto/{idProducto}/count")
    public Response count(@PathParam("idProducto") Long idProducto){
        if (idProducto == null || idProducto < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID del producto no puede ser nulo o menor que 0.")
                    .build();
        }
        try {
            Long count=productoPrecioBean.countByIdProducto(idProducto);
            return Response.ok(count).build();
        }catch (Exception e) {
            return responseExcepcions(e,idProducto);
        }
    }
}