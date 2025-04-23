/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.TipoProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetallePK;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

/**
 * @author mjlopez
 */
@Path("producto")
public class ProductoResource extends GeneralRest implements Serializable {


    @Inject
    ProductoBean pBean;
    @Inject
    TipoProductoBean tpBean;
    @Inject
    ProductoDetalleBean pdBean;

    /**
     * metodo que devueleve una rango de datos de tipo Producto
     *
     * @param first          la pocicion del primer dat
     * @param max            la cantidad de datos que se desea obtener
     * @param activo         indica si se desea solo registros con la propiedad activo
     * @param idTipoProducto string que indica si se quiere encontra todos los registro "any" o los relacionados con idEspecifico
     * @return estatus 200 junto una lista de tipo T si no definel los parametros entonces devuelve los primeros 20 registros
     * 400 si los argumentos son erroneos
     * 500 si existe problema con el entity, si i al buscar la cantidad de datos devuelve mas de un resultado o existe problema con la base de datos por restriciones
     * 404 si no existe el tipoProducto enviado
     */

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(@QueryParam("first") @DefaultValue("0") Integer first, @QueryParam("max") @DefaultValue("20") Integer max, @QueryParam("idTipoProducto") @DefaultValue("0") Integer idTipoProducto, @QueryParam("activo") Boolean activo) {
        try {

            //findRange NORMAL
            if (activo == null && idTipoProducto == 0) {
                List<Producto> registros = pBean.findRange(first, max);
                long totalRegistros = pBean.count();
                return Response.ok(registros).header(Headers.TOTAL_RECORD, totalRegistros).build();
            }
//            find range activos
            if (activo != null && idTipoProducto == 0) {
                List<Producto> registros = pBean.findRangeProductoActivos(first, max, activo);
                long totalRegistros = pBean.countProductoActivos(activo);
                return Response.ok(registros).header(Headers.TOTAL_RECORD, totalRegistros).build();
            }
//
            //find range por idTipoProducto y cualquier valor de activo
            if (activo == null ) {
                activo = Boolean.TRUE;
            }
            List<Producto> encontrados = pBean.findRangeByIdTipoProductosAndActivo(idTipoProducto, activo, first, max);
            long total = pBean.countByIdTipoProductosAndActivo(idTipoProducto, activo);
            return Response.ok(encontrados).header(Headers.TOTAL_RECORD, total).type(MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            return responseExcepcions(e, Long.valueOf(idTipoProducto));
        }
    }


    /**
     * Metodo para encontrar un registro especifico de producto dado su id
     *
     * @param idProducto del registro a buscar
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * 500 en dado caso falle el servidor o la base de datos
     * 404 si no se encuentra la entidad
     * 400 si hay problema con los parametros
     */

    @Path("{idProducto}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("idProducto") Long idProducto) {
        try {
            Producto encontrado = pBean.findById(idProducto);
            Response.ResponseBuilder builder = Response.ok(encontrado);
            return builder.build();
        } catch (Exception e) {
            return responseExcepcions(e, idProducto);
        }
    }


    /**
     * Registra una entidad Producto
     *
     * @param uriInfo informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se encuentra
     * 400 si hay error con los argumentos
     * 422 si la entidad ha sido creada previamente
     * 500 si falla el servidor o la base
     * 404 si no existe el tipoProducto
     */

    @Path("")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(Producto registro, @QueryParam("idTipoProducto") @DefaultValue("0") Integer idTipoProducto, @Context UriInfo uriInfo) {
        try {
            //si no se agrega idTipoProducto
            if (idTipoProducto == 0) {
                pBean.create(registro);
                UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
                uriBuilder.path(String.valueOf(registro.getIdProducto()));
                return Response.created(uriBuilder.build()).build();
            }
            pBean.createProductoAndDetail(registro, idTipoProducto);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(registro.getIdProducto()));
            return Response.created(uriBuilder.build()).build();
        } catch (Exception e) {
            return responseExcepcions(e, Long.valueOf(idTipoProducto));
        }
    }

    /**
     * Borra un registro de tipo Producto Especifico
     *
     * @param idProducto a identificador de la entidda a borar
     * @return un status 200 si se borro la entidad ,
     * un 422 si hubo un problema
     * y 500 si falla el seridor o la base
     * 404 si no existe el tipoProducto
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/{idProducto}")
    public Response delete(@PathParam("idProducto") Long idProducto
            , @QueryParam("idTipoProducto") @DefaultValue("0") Integer idTipoProducto) {
        try {
            if (idTipoProducto == 0) {
                pBean.delete(idProducto);
                return Response.status(200).build();
            }
            pBean.deleteProductoAndDetail(idProducto, idTipoProducto);
            return Response.status(200).build();
        } catch (Exception e) {
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }
    }

    /**
     * Actualiza una entidad de base de datos
     * @param registro entidda a ser actualizada
     * @param uriInfo  info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @Path("{idProducto}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(Producto registro, @PathParam("idProducto") Long idProducto, @Context UriInfo uriInfo) {
        try {
            registro.setIdProducto(idProducto);
            pBean.update(registro, idProducto);
            return Response.status(200).build();
        } catch (Exception e) {
            ComboDetalle regitrosdsd=new ComboDetalle(1L, 3L);

            ComboDetallePK PK=new ComboDetallePK(1L,2l);
            ComboDetalle regitrosdsd2=new ComboDetalle(PK);
            return responseExcepcions(e, idProducto);
        }
    }


    /**
     * busca los producto dado un idTipoProducto y su parametro activo
     *
     * @param idTipoProducto idTipoProducto relacionado a producto
     * @param activo         si el producto se encuentra o no activo
     * @param first          inicio
     * @param max            cantidda maxima
     * @return un status 200 si se envvio correctamente en caso contrario retorna un 400
     * problema y 500 si falla el servidor
     */

//    public Response buscarPorTipoProductosAndActivo(Integer idTipoProducto, boolean activo, int first, int max) {
//        try {
//            List<Producto> encontrados = pBean.findRangeByIdTipoProductosAndActivo(idTipoProducto, activo, first, max);
//            long total = pBean.countByIdTipoProductosAndActivo(idTipoProducto, activo);
//            return Response.ok(encontrados).header(Headers.TOTAL_RECORD, total).type(MediaType.APPLICATION_JSON).build();
//
//        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
//            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
//        }
//    }


}



