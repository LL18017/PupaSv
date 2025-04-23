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


    /**
     * Busca un {ProductoPrecio} por su ID y lo retorna en la respuesta HTTP.
     *
     * Este método busca un ProductoPrecio utilizando el ID proporcionado en la URL. Si se encuentra el producto,
     * se retorna con un estado HTTP {@code 200 OK}. Si no se encuentra, se retorna un estado HTTP {@code 404 Not Found}.</p>
     *
     * @param id el ID del {ProductoPrecio} que se desea buscar.
     * @return una respuesta HTTP con el producto encontrado, o un error si el producto no existe.
     * @throws Exception si ocurre un error durante el proceso de búsqueda.
     */
    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") Long id) {
        try {
            ProductoPrecio precio = productoPrecioBean.findById(id);
            if (precio == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(precio).build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
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

    /**
     * Actualiza un ProductoPrecio existente con los datos proporcionados en la solicitud.
     *
     * <Este método recibe una solicitud HTTP PUT con un objeto ProductoPrecio en el cuerpo de la solicitud,
     * y actualiza el ProductoPrecio correspondiente en la base de datos. Si el ID proporcionado en la ruta no
     * coincide con el ID del objeto en el cuerpo de la solicitud, se retorna un error
     * Si el cuerpo de la solicitud está vacío, se retorna un error
     *
     * @param id el ID del ProductoPrecio que se desea actualizar (proporcionado en la ruta).
     * @param precio el objeto ProductoPrecio que contiene los nuevos valores a actualizar.
     * @return una respuesta HTTP con el objeto actualizado si la operación fue exitosa,
     *         o un error {@code 400 Bad Request} si los IDs no coinciden o {@code 404 Not Found} si el cuerpo está vacío.
     * @throws Exception si ocurre un error durante el proceso de actualización.
     */
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


    /**
     * Elimina un ProductoPrecio existente según el ID proporcionado en la ruta.
     *
     * Este método recibe una solicitud HTTP DELETE para eliminar el objeto correspondiente
     * al ID proporcionado en la ruta. Si la eliminación es exitosa, se retorna una respuesta HTTP con el estado {@code 200 OK}.
     * En caso de error, se maneja la excepción y se retorna una respuesta adecuada.</p>
     *
     * @param id el ID del ProductoPrecio que se desea eliminar (proporcionado en la ruta).
     * @return una respuesta HTTP si la eliminación fue exitosa, o una respuesta de error si ocurre una excepción.
     * @throws Exception si ocurre un error durante el proceso de eliminación.
     */
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


    /**
     * Recupera un ProductoPrecio correspondiente al Producto especificado por su ID.
     *
     * Este método maneja las solicitudes HTTP GET para obtener los precios asociados a un producto dado el ID
     * del producto. Los parámetros opcionales  first y max permiten paginar los resultados si es necesario.
     *
     * @param idProducto el ID del Producto cuyo precio se desea recuperar (proporcionado en la ruta).
     * @param first el índice del primer elemento de la página de resultados (por defecto es 0).
     * @param max el número máximo de elementos a devolver en la respuesta (por defecto es 20).
     * @return una respuesta HTTP que contiene el objeto ProductoPrecio correspondiente al Producto solicitado,
     *         o una respuesta de error si ocurre un problema durante la consulta.
     * @throws Exception si ocurre un error durante la recuperación del producto o sus precios.
     */
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

    /**
     * Cuenta la cantidad de registros de ProductoPrecio asociados a un Producto especificado por su ID.
     *
     * Este método maneja las solicitudes HTTP GET para obtener el número total de precios asociados a un producto
     * dado el ID del producto. La respuesta es el conteo de los registros relacionados con el producto solicitado.
     *
     * @param idProducto el ID del Producto cuyo número de precios se desea contar (proporcionado en la ruta).
     * @return una respuesta HTTP que contiene el número total de precios asociados al Producto especificado.
     * @throws Exception si ocurre un error durante el conteo de los registros.
     */
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