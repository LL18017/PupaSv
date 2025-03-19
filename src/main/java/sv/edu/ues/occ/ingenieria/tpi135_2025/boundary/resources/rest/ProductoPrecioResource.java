package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("tipoproducto/producto/{idproducto}/productoPrecio")
public class ProductoPrecioResource implements Serializable {

    @Inject
    ProductoPrecioBean ppBean;


    /**
     * metodo que devueleve una rango de datos de tipo Producto sin importar el idTipoProductos
     *
     * @param first la pocicion del primer dat
     * @param max la cantidad de datos que se desea obtener
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(int first, int max) {
        try {
            if (first >= 0 && max >= 0 && max <= 50) {

                List<ProductoPrecio> encontrados = ppBean.findRange(first, max);
                long total = ppBean.count();
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            } else {
                return Response.status(400).header("wrong parameter, first:", first + ",max: " + max).header("wrong parameter : max", "s").build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * metodo que devueleve una rango de datos de tipo Producto
     * @param first la pocicion del primer dat
     * @param max la cantidad de datos que se desea obtener
     @param idProducto string que indica si se quiere encontra todos los registro "any" o los relacionados con idEspecifico
      * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first")
            @DefaultValue("0") int first,
            @QueryParam("max")
            @DefaultValue("20") int max,
            @PathParam("idproducto") String idProducto
    ) {
        try {
            if (first >= 0 && max >= 0 && max <= 50 ) {

                if (idProducto.equals("any")) {
                    return findRange(first, max);
                }
                List<ProductoPrecio> encontrados = ppBean.findByIdProducto(Integer.parseInt(idProducto),first, max);
                long total = ppBean.countByIdProducto(Integer.parseInt(idProducto));
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            } else {
                return Response.status(400).header("wrong parameter, first:", first + ",max: " + max).header("wrong parameter : max", "s").build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * metodo para encontrar un registro especifico de producto dado su id
     *
     * @param id del registro a buscar
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */

    @Path("/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        if (id != null) {
            try {
                ProductoPrecio encontrado = ppBean.findById(id);
                if (encontrado != null) {
                    Response.ResponseBuilder builder = Response.ok(encontrado);
                    return builder.build();
                }
                return Response.status(404).header(Headers.NOT_FOUND_ID, id).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header("wrong-parameter : id", id).build();
    }



}
