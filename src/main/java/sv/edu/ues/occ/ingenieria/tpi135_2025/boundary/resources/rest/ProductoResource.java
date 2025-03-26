/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;

/**
 * @author mjlopez
 */
@Path("tipoProducto/{idTipoProducto}/producto")
public class ProductoResource implements Serializable {

    @Inject
    ProductoBean pBean;
    @Inject
    ProductoDetalleBean pdBean;


    /**
     * metodo que devueleve una rango de datos de tipo Producto
     * @param first la pocicion del primer dat
     * @param max la cantidad de datos que se desea obtener
     * @param activo indica si se desea solo registros con la propiedad activo
     @param idTipoProducto string que indica si se quiere encontra todos los registro "any" o los relacionados con idEspecifico
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRangeByIdTipoProducto(@QueryParam("first") @DefaultValue("0") Integer first,@QueryParam("max") @DefaultValue("20") Integer max,@PathParam("idTipoProducto") Integer idTipoProducto,@QueryParam("activo") @DefaultValue("false") boolean activo) {
        try {
            if (first >= 0 && max >= 0 && max <= 50 ) {
                //solicitud de activos
                if (activo) {
                    return findRangeProductoActivos(first, max, idTipoProducto);
                }
                //flujo normal
                List<Producto> encontrados = pBean.findRangeByIdTipoProductos(idTipoProducto,first, max);
                long total = pBean.countByIdTipoProductos(idTipoProducto);
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            } else {
                return Response.status(400).header(Headers.WRONG_PARAMETER,"first:"+ first + ",max: " + max).build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    /**
     * Metodo para encontrar un registro especifico de producto dado su id
     * @param id del registro a buscar
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */

    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("idTipoProducto") Integer idTipoProducto,@PathParam("id") Long id) {
        if (idTipoProducto != null) {
            try {
                Producto encontrado = pBean.findById(id);
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
        return Response.status(404).header(Headers.WRONG_PARAMETER," idTipoProducto"+ id).build();
    }


    /**
     * registra una entidad Producto ademas de establecer la relacion producto detalle
     *
     * @param idTipoProducto id de la entidad tipoProductoRelacionada con producto
     * @param uriInfo informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
     */

    @Path("")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(Producto registro, @PathParam("idTipoProducto") Integer idTipoProducto,@Context UriInfo uriInfo) {
        if (registro != null && idTipoProducto != null) {
            try {
                pBean.createProducto(registro,idTipoProducto);
                if (registro.getIdProducto() != null) {
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
                    uriBuilder.path(String.valueOf(registro.getIdProducto()));
                    return Response.created(uriBuilder.build()).build();
                }
                return Response.status(422).header(Headers.UNPROCESSABLE_ENTITY, "Record couldnt be created").build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, registro).build();
    }

    /**
     * Borra un registro de tipo Producto Especifico
     * @param id
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id, @Context UriInfo uriInfo) {
        if (id != null) {
            try {
                pBean.delete(id);
                return Response.status(200).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(422).header(Headers.PROCESS_ERROR, e.getMessage()).build();
            }
        }
        return Response.status(500).header("Wrong-parameter", id).build();
    }

    /**
     * Actualiza una entidad de base de datos
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @Path("{id}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(Producto registro,@PathParam("id") Long id,@PathParam("idTipoProducto") Integer idTipoProducto,@Context UriInfo uriInfo) {
        if (registro != null && id != null && id > 0 && idTipoProducto >0) {
            try {
                registro.setIdProducto(id);
                pBean.update(registro);
                if (registro.getIdProducto() != null) {
                    return Response.status(200).build();
                }
                return Response.status(422).header(Headers.PROCESS_ERROR, "Record couldnt be updated").build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER, "id: "+id+" ,registro :"+registro).build();
    }

    public Response findRangeProductoActivos(Integer idTipoProducto ,Integer first, Integer max) {
        if (idTipoProducto != null && first != null && max != null) {
            try {
                List<Producto> resultado = pBean.findRangeProductoActivos(idTipoProducto,first,max);
                Long total= pBean.countByIdTipoProductos(idTipoProducto);
                return Response.ok(resultado).header(Headers.TOTAL_RECORD,total).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER, idTipoProducto).build();
    }


}
