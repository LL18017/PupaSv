/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.plantillas.ComboCantidadPlantilla;
import sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.plantillas.DatosMixtosDTO;
import sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest.plantillas.ProductoCantidadPLantilla;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("ordenDetalle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdenDetalleResource extends GeneralRest implements Serializable {
    @Inject
    OrdenDetalleBean odBean;

    private EntityManager em;


    /**
     * metodo que devueleve una rango de datos de tipo OrdenDetalle con relacion a un idOrden
     *
     * @param first   la pocicion del primer dat
     * @param max     la cantidad de datos que se desea obtener
     * @param idOrden devuelve los primeros 20 registros
     * @return una lista de tipo T si no definel los parametros entonces
     */

    @Path("orden/{idOrden}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRangeByIdOrden(@QueryParam("first") @DefaultValue("0") int first,
                                       @QueryParam("max") @DefaultValue("20") int max,
                                       @PathParam("idOrden") Long idOrden) {
        try {
            List<OrdenDetalle> encontrados = odBean.findRangeByIdOrden(idOrden, first, max);
            long total = odBean.countByIdOrden(idOrden);
            Response.ResponseBuilder builder = Response.ok(encontrados).
                    header(Headers.TOTAL_RECORD, total).
                    type(MediaType.APPLICATION_JSON);
            return builder.build();
        } catch (Exception e) {
            return responseExcepcions(e, idOrden);
        }
    }

    /**
     * Metodo para encontrar un registro especifico de producto dado su IdOrden y IdPrecio
     *
     * @param idOrden
     * @param idProductoPrecio
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */
    //URL:http://localhost:9080/PupaSv-1.0-SNAPSHOT/v1/ordenDetalle/orden/{idOrden}/productoPrecio/{idProductoPrecio}
    @Path("orden/{idOrden}/productoPrecio/{idProductoPrecio}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findByIdOrdenAndIdProductoPrecio(
            @PathParam("idOrden") Long idOrden,
            @PathParam("idProductoPrecio") Long idProductoPrecio
    ) {
        try {
            OrdenDetalle encontrado = odBean.findByIdOrdenAndIdPrecioProducto(idOrden, idProductoPrecio);
            Response.ResponseBuilder builder = Response.ok(encontrado);
            return builder.build();
        } catch (Exception e) {
            return responseExcepcions(e, idOrden);
        }
    }

    /**
     * Borra un OrdenDetalle Especifico
     *
     * @param idOrden          id del Combo relacionado con ComboDetalle
     * @param idProductoPrecio id del Combo relacionado con ComboDetalle
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    //URL:http://localhost:9080/PupaSv-1.0-SNAPSHOT/v1/ordenDetalle/orden/{idOrden}/productoPrecio/{idProductoPrecio}
    @Path("orden/{idOrden}/productoPrecio/{idProductoPrecio}/")
        public Response delete(@PathParam("idOrden") Long idOrden, @PathParam("idProductoPrecio") Long idProductoPrecio) {
            try {
                odBean.delete(idOrden, idProductoPrecio);
                return Response.status(200).build();
            } catch (Exception e) {
                return responseExcepcions(e, idOrden);
            }
        }


    /**
     * Actualiza el COmboDetalle de base de datos
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo  info de url de donde se ha realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    //URL:http://localhost:9080/PupaSv-1.0-SNAPSHOT/v1/ordenDetalle
    @Path("orden/{idOrden}/productoPrecio/{idProductoPrecio}/")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(OrdenDetalle registro, @Context UriInfo uriInfo, @PathParam("idOrden") Long idOrden, @PathParam("idProductoPrecio") Long idProductoPrecio) {


        try {
            odBean.update(registro, idOrden, idProductoPrecio);
            return Response.status(200).build();
        } catch (Exception e) {
            return responseExcepcions(e, idOrden);
        }
    }


    //
    @POST
    @Path("producto")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generarOrdenDetalleProducto(
            @QueryParam("idOrden") @DefaultValue("0") Long idOrden,
            @QueryParam("idProducto") @DefaultValue("0") Long idProducto,
            @QueryParam("cantidad") @DefaultValue("0") Integer cantidad) {
        try {
            odBean.generarOrdenDetalleProducto(idOrden, idProducto, cantidad);
            return Response.ok().build();

        } catch (Exception e) {
            return responseExcepcions(e, idOrden);
        }
    }


    /**
     * Genera una lista de objetos OrdenDetalle a partir de un Combo seleccionado,
     * asignado a una orden existente y multiplicado por una cantidad dada del combo.
     * <p>
     * Este método no guarda los detalles generados en la base de datos,
     * solo construye la lista a partir de los productos que componen el combo.
     *
     * @param idOrden       ID de la orden a la cual se asociará el combo.
     * @param idCombo       ID del combo del cual se tomarán los productos.
     * @param cantidadCombo Cantidad del combo (por defecto se asume 1 si es nulo o menor a 1).
     * @return Lista de objetos OrdenDetalle generados, o 204 si no hay productos válidos.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    //URL:http://localhost:9080/PupaSv-1.0-SNAPSHOT/v1/ordenDetalle/combo?orden=1&combo=2&cantidad=3
    @Path("combo")
    public Response generarOrdenDetalleDesdeCombo(
            @QueryParam("idOrden") @DefaultValue("0") Long idOrden,
            @QueryParam("idCombo") @DefaultValue("0") Long idCombo,
            @QueryParam("cantidad") @DefaultValue("1") Integer cantidadCombo) {


        try {
            if (idOrden == 0 || idCombo == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Los parámetros 'orden' y 'combo' son obligatorios y deben ser mayores a cero.")
                        .build();
            }
            odBean.generarOrdenDetalleDesdeCombo(idOrden, idCombo, cantidadCombo);
            return Response.ok().build();
        } catch (Exception e) {
            return responseExcepcions(e, null);
        }
    }

    /**
     * Genera una lista combinada de objetos OrdenDetalle a partir de productos y combos seleccionados.
     * <p>
     * Este método permite construir múltiples detalles de una orden en una sola llamada,
     * utilizando productos individuales y combos, con cantidades específicas para cada grupo.
     *
     * @return Lista de OrdenDetalle generados, o 204 si no se pudo generar ningún detalle.
     */


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("mixto")
    public Response generarOrdenDetalleMixto(
            DatosMixtosDTO datos,
            @QueryParam("idOrden") Long idOrden) {

        try {
            if (idOrden == null || idOrden <= 0) {
                return Response.status(400).header(Headers.WRONG_PARAMETER,"idOrden no puede ser nulo o menor que cero").build();
            }

            if (datos == null) {
                return Response.status(400).header(Headers.WRONG_PARAMETER,"almenos una lista debe poseer data").build();
            }
            List<ProductoCantidadPLantilla> productos = datos.getProductoList();
            List<ComboCantidadPlantilla> combos = datos.getComboList();
            odBean.generarOrdenDetalleMixto(idOrden, productos, combos);
            return Response.ok().build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return responseExcepcions(e, null);
        }
    }


}