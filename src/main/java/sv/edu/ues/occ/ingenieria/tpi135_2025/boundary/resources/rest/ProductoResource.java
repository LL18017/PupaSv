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
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.TipoProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

/**
 * @author mjlopez
 */
@Path("producto")
public class ProductoResource extends Resource implements Serializable {

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
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(@QueryParam("first") @DefaultValue("0") Integer first, @QueryParam("max") @DefaultValue("20") Integer max, @QueryParam("idTipoProducto") @DefaultValue("0") Integer idTipoProducto, @QueryParam("activo") Boolean activo) {
        try {
            Response verificarFirstAndMax = veririficarMaxAndFirst(first, max);

            if (verificarFirstAndMax.getStatus() != 200) return verificarFirstAndMax;
            //find range normal
            if (activo == null && idTipoProducto == 0) {
                List<Producto> registros = pBean.findRange(first, max);
                long totalRegistros = pBean.count();
                return Response.ok(registros).header(Headers.TOTAL_RECORD, totalRegistros).build();
            }
//            find range de acuerdo a activos
            if (activo != null && idTipoProducto == 0) {
                List<Producto> registros = pBean.findRangeProductoActivos(first, max, activo);
                long totalRegistros = pBean.countProductoActivos(activo);
                return Response.ok(registros).header(Headers.TOTAL_RECORD, totalRegistros).build();
            }


            Response verificarIdTipoProducto = verificarId(idTipoProducto, "IdTipoProduct");
            if (verificarIdTipoProducto.getStatus() != 200) return verificarIdTipoProducto;

            //find range por idTipoProducto y cualquier valor de activo
            if (verificarIdTipoProducto.getStatus() == 200) {
                return buscarPorTipoProductosAndActivo(idTipoProducto, Boolean.TRUE.equals(activo), first, max);
            }
            //findRange by activo
            return Response.status(500).header(Headers.PROCESS_ERROR, "NO SE PUEDE REALIZAR LA PETICION").build();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en findRangeByIdTipoProducto", e);
            return Response.status(500)
                    .header(Headers.PROCESS_ERROR, e.getMessage())
                    .entity("Error interno del servidor")
                    .build();
        }
    }


    /**
     * Metodo para encontrar un registro especifico de producto dado su id
     *
     * @param idProducto del registro a buscar
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */

    @Path("{idProducto}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("idProducto") Long idProducto) {

        try {
            Response verificarIdProducto = verificarId(idProducto, "IdProducto");

            if (verificarIdProducto.getStatus() != 200) return verificarIdProducto;
            Producto encontrado = pBean.findById(idProducto);
            if (encontrado != null) {
                Response.ResponseBuilder builder = Response.ok(encontrado);
                return builder.build();
            }
            return Response.status(404).header(Headers.NOT_FOUND_ID, idProducto).build();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }


    /**
     * registra una entidad Producto ademas de establecer la relacion producto detalle
     *
     * @param idTipoProducto id de la entidad tipoProductoRelacionada con producto
     * @param uriInfo        informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
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
            Response verificarTipoProducto = verificarId(idTipoProducto, "idTipoProducto");

            if (verificarTipoProducto.getStatus() != 200) return verificarTipoProducto;
            TipoProducto encontrado = tpBean.findById(idTipoProducto);
            if (encontrado == null)
                return Response.status(404).header(Headers.NOT_FOUND_ID, "no existe el tipo producto: " + idTipoProducto).build();

            pBean.createProducto(registro, idTipoProducto);

            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(registro.getIdProducto()));
            return Response.created(uriBuilder.build()).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Borra un registro de tipo Producto Especifico
     *
     * @param idProducto
     * @param uriInfo    info de url de donde se esta realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/{idProducto}")
    public Response delete(@PathParam("idProducto") Long idProducto, @Context UriInfo uriInfo) {
        try {
            Response verificarProducto = verificarId(idProducto, "idProducto");
            if (verificarProducto.getStatus() != 200) return verificarProducto;
            Producto encontrado = pBean.findById(idProducto);
            if (encontrado == null) {
                return Response.status(404).header(Headers.NOT_FOUND_ID, "no existe este registro: " + idProducto).build();
            }
            pBean.delete(idProducto);
            return Response.status(200).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }
    }

    /**
     * Actualiza una entidad de base de datos
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo  info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @Path("{idProducto}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(Producto registro, @PathParam("idProducto") Long idProducto, @QueryParam("idTipoProducto") @DefaultValue("0") Integer idTipoProducto, @Context UriInfo uriInfo) {
        try {
            Response verificarProducto = verificarId(idProducto, "idProducto");
            Response verificarTipoProducto = verificarId(idProducto, "idTipoProducto");

            if (verificarProducto.getStatus() != 200) return verificarProducto;
            if (verificarTipoProducto.getStatus() != 200 && idTipoProducto != 0) return verificarTipoProducto;
            Producto encontrado = pBean.findById(idProducto);
            if (encontrado == null) {
                return Response.status(404).header(Headers.NOT_FOUND_ID, idProducto).build();
            }
            registro.setIdProducto(idProducto);
            pBean.update(registro);
            if (registro.getIdProducto() != null) {
                return Response.status(200).build();
            }
            return Response.status(422).header(Headers.PROCESS_ERROR, "error al procesar peticion").build();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
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

    public Response buscarPorTipoProductosAndActivo(Integer idTipoProducto, boolean activo, int first, int max) {
        try {
            Response verificarExiste = verificarTipoProductoExiste(idTipoProducto);
            if (verificarExiste.getStatus() != 200) return verificarExiste;
            List<Producto> encontrados = pBean.findRangeByIdTipoProductosAndActivo(idTipoProducto, activo, first, max);
            long total = pBean.countByIdTipoProductosAndActivo(idTipoProducto, activo);
            return Response.ok(encontrados).header(Headers.TOTAL_RECORD, total).type(MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }
    }


}



