package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("productoPrecio")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoPrecioResource implements Serializable {

    @Inject
    ProductoPrecioBean productoPrecioBean;
    @Inject
    ProductoBean productoBean;

    /**
     * Obtiene un rango de ProductoPrecio.
     *
     * @param first La posición del primer registro a recuperar (para paginación).
     * Por defecto es 0.
     * @param max La cantidad máxima de registros a recuperar (para paginación).
     * Por defecto es 20.
     * @return Una respuesta HTTP con una lista de objetos ProductoPrecio en formato JSON.
     * Incluye un encabezado "X-Total-Count" con el número total de registros disponibles.
     * Puede devolver un estado 500 Internal Server Error si ocurre un error
     * al acceder a los datos.
     */
    @GET
    public Response getAll(@QueryParam("first") @DefaultValue("0") int first,
                           @QueryParam("max") @DefaultValue("20") int max) {
        List<ProductoPrecio> precios =productoPrecioBean.findRange(first, max);
        long total = productoPrecioBean.count();
        return Response.ok(precios).header(Headers.TOTAL_RECORD, total).build();
    }

    /**
     * Elimina un ProductoPrecio específico por su ID.
     *
     * @param id El identificador único del ProductoPrecio que se desea eliminar.
     * @return Una respuesta HTTP con un estado 204 No Content si la eliminación es exitosa,
     * un estado 404 Not Found si el ProductoPrecio con el ID especificado no existe,
     * o un estado 500 Internal Server Error si ocurre un error durante la eliminación.
     */
    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id) {
        ProductoPrecio precio = productoPrecioBean.findById(id);
        if (precio == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(precio).build();
    }

    /**
     * Registra un nuevo ProductoPrecio.
     *
     * @param precio  El objeto ProductoPrecio que se desea crear. Este objeto debe
     * contener los datos del nuevo precio, incluyendo la referencia
     * al Producto al que pertenece.
     * @param uriInfo Información sobre la URI de la solicitud. Se utiliza para construir
     * la URI de la ubicación del recurso recién creado en la respuesta.
     * @return Una respuesta HTTP con un estado 201 Created si la creación es exitosa.
     * El encabezado `Location` contendrá la URI del nuevo recurso creado.
     * Puede devolver un estado 400 Bad Request si los datos proporcionados
     * no son válidos (por ejemplo, falta la referencia al Producto).
     * También puede devolver un estado 500 Internal Server Error si ocurre
     * un error durante la creación.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(ProductoPrecio precio, @Context UriInfo uriInfo) {
        // Validar que el precio y el producto sean válidos
        if (precio == null || precio.getIdProducto() == null || precio.getIdProducto().getIdProducto() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar un ProductoPrecio válido con un idProducto.")
                    .build();
        }

        Producto producto = productoBean.findById(precio.getIdProducto().getIdProducto());
        if (producto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El idProducto proporcionado no existe.")
                    .build();
        }
        try {
            productoPrecioBean.create(precio);
            URI createdUri = uriInfo.getAbsolutePathBuilder()
                    .path(precio.getIdProductoPrecio().toString())
                    .build();
            return Response.created(createdUri).entity(precio).build();
        } catch (Exception e) {
            Logger.getLogger(ProductoPrecioResource.class.getName()).log(Level.SEVERE, "Error al crear el precio del producto", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al crear el precio del producto.")
                    .build();
        }
    }


    /**
     * Actualiza un ProductoPrecio existente.
     *
     * @param id     El identificador único del ProductoPrecio que se va a actualizar.
     * Este ID debe coincidir con el ID proporcionado en el cuerpo de la solicitud.
     * @param precio El objeto ProductoPrecio con los datos actualizados. El ID del
     * ProductoPrecio en este objeto también debe coincidir con el `id`
     * proporcionado en la ruta.
     * @return Una respuesta HTTP con el objeto ProductoPrecio actualizado en formato JSON
     * si la actualización es exitosa (estado 200 OK). Puede devolver un estado 400
     * Bad Request si el ID en la ruta no coincide con el ID en el cuerpo o si
     * los datos proporcionados no son válidos. También puede devolver un estado 404
     * Not Found si el ProductoPrecio con el ID especificado no existe, o un estado
     * 500 Internal Server Error si ocurre un error durante la actualización.
     */
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") Long id, ProductoPrecio precio) {
        if (precio == null || precio.getIdProductoPrecio() == null || !id.equals(precio.getIdProductoPrecio())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El Id en la ruta debe de coincidor con el ID en la base de datos")
                    .build();
        }
        if (productoPrecioBean.findById(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            productoPrecioBean.update(precio,id);
            return Response.ok(precio).build();
        }catch (Exception e) {
            Logger.getLogger(ProductoPrecioResource.class.getName()).log(Level.SEVERE, null, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al actualizar el precio del producto.")
                    .build();
        }
    }

    /**
     * Obtiene un ProductoPrecio específico por su ID.
     *
     * @param id El identificador único del ProductoPrecio que se desea obtener.
     * @return Una respuesta HTTP con el objeto ProductoPrecio en formato JSON si se encuentra,
     * o un estado 404 Not Found si no existe. También puede devolver un estado 500
     * si ocurre un error interno del servidor.
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        if (productoPrecioBean.findById(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            productoPrecioBean.delete(productoPrecioBean.findById(id));
            return Response.noContent().build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al eliminar el precio del producto.")
                    .build();
        }
    }

    /**
     * Obtiene un rango de precios asociados a un producto específico.
     *
     * @param idProducto El identificador del producto para el cual se desean obtener los precios.
     * @param first      La posición del primer resultado a recuperar (para paginación).
     * @param max   La cantidad máxima de resultados a recuperar (para paginación).
     * @return Una respuesta HTTP con una lista de objetos ProductoPrecio en formato JSON.
     * Incluye un encabezado "X-Total-Count" con el número total de precios para el producto.
     * Puede devolver un estado 400 si el `idProducto` proporcionado no existe
     * o si los parámetros de paginación no son válidos. También puede devolver un
     * estado 500 si ocurre un error interno del servidor.
     */
    @GET
    @Path("producto/{idProducto}")
    public Response getByIdProducto(@PathParam("idProducto") Long idProducto,
                                    @QueryParam("first")@DefaultValue("0") int first,
                                    @QueryParam("max") @DefaultValue("20") int max) {
        if (productoBean.findById(idProducto) == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("El idProducto proporcionado no existe.").build();
        }
        try {
            List<ProductoPrecio> precios = productoPrecioBean.findByIdProducto(idProducto, first,max);
            long total = productoPrecioBean.countByIdProducto(idProducto);
            return Response.ok(precios).header(Headers.TOTAL_RECORD, total).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al obtener precios por idProducto.").build();
        }
    }

    /**
     * Cuenta la cantidad de precios asociados a un producto específico.
     *
     * @param idProducto El identificador del producto para el cual se desea contar los precios.
     * @return La cantidad de registros de ProductoPrecio asociados al producto con el ID especificado.
     * @throws IllegalArgumentException Si el `idProducto` proporcionado no es válido (según la lógica del bean).
     * @throws WebApplicationException Si ocurre un error al acceder al recurso (por ejemplo, producto no encontrado) o un error interno del servidor.
     */
    @GET
    @Path("producto/{idProducto}/count")
    public Response countByProductId(@PathParam("idProducto") Long idProducto) {
        if (productoBean.findById(idProducto) == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("El idProducto proporcionado no existe.").build();
        }
        try {
            long count = productoPrecioBean.countByIdProducto(idProducto);
            return Response.ok(count).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error al contar precios por idProducto.").build();
        }
    }

}
