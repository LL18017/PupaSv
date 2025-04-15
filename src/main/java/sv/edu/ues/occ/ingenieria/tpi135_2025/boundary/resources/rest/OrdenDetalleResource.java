/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.DatosMixtosDTO;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("ordenDetalle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdenDetalleResource extends GeneralRest implements Serializable {
    @Inject
    OrdenDetalleBean odBean;

    /**
     * Metodo que devueleve un rango de datos de tipo OrdenDetalle sin importar el idOrden
     * @param first la pocicion del primer dat
     * @param max la cantidad de datos que se desea obtener
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */
    @Path("productoPrecio/ordenDetalle")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first")
            @DefaultValue("0") int first,
            @QueryParam("max")
            @DefaultValue("20") int max
    ) {

        try {
            if ((first >= 0 && max >= 0 && max <=50) ) {

                List<OrdenDetalle> encontrados= odBean.findRange(first,max);
                long total=odBean.count();
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            }else{
                return Response.status(400).header(Headers.WRONG_PARAMETER," first: "+ first+",max: "+max  ).header("wrong parameter : max","s").build();
            }
        }catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }




    /**
     * metodo que devueleve una rango de datos de tipo OrdenDetalle con relacion a un idOrden
     * @param first la pocicion del primer dat
     * @param max la cantidad de datos que se desea obtener
     * @return una lista de tipo T si no definel los parametros entonces
     * @param idOrden
     * devuelve los primeros 20 registros
     */

    @Path("{idOrden}/productoPrecio/ordenDetalle")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRangeByIdOrden(@QueryParam("first") int first,
                                          @QueryParam("max") int max,
                                          @PathParam("idOrden") Long idOrden) {
        try {
            if (first >= 0 && max >= 0 && max <= 50) {

                List<OrdenDetalle> encontrados = odBean.findRangeByIdOrden(idOrden, first,max);
                long total = odBean.countByIdOrden(idOrden);
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            } else {
                return Response.status(400).header(Headers.WRONG_PARAMETER,"first: "+first+" max :" +max +" , idOrden"+ idOrden).header("wrong parameter : max", "s").build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Metodo para encontrar un registro especifico de producto dado su IdOrden y IdPrecio
     * @param idOrden
     * @param idProductoPrecio
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */
    @Path("{idOrden}/productoPrecio/{idProductoPrecio}/ordenDetalle")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findByIdOrdenAndIdProductoPrecio(
            @PathParam("idOrden") Long idOrden,
            @PathParam("idProductoPrecio") Long idProductoPrecio
    ) {
        if (idOrden != null && idProductoPrecio != null) {
            try {

                OrdenDetalle encontrado = odBean.findByIdOrdenAndIdPrecioProducto(idOrden,idProductoPrecio);
                if (encontrado != null) {
                    Response.ResponseBuilder builder = Response.ok(encontrado);
                    return builder.build();
                }
                return Response.status(404).header(Headers.NOT_FOUND_ID, "idOrden: "+idOrden + " , idProducto: "+idOrden).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER,"id :"+ idOrden).build();
    }

    /**
     * Borra un OrdenDetalle Especifico
     *
     * @param idOrden id del Combo relacionado con ComboDetalle
     * @param idProductoPrecio id del Combo relacionado con ComboDetalle
     * @param uriInfo info de url de donde se ha realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{idOrden}/productoPrecio/{idProductoPrecio}/ordenDetall")
    public Response delete(@PathParam("idOrden") Long idOrden, @PathParam("idProductoPrecio") Long idProductoPrecio, @Context UriInfo uriInfo) {

        if (idOrden != null && idProductoPrecio != null) {
            try {
                OrdenDetallePK id=new OrdenDetallePK(idOrden,idProductoPrecio);
                odBean.delete(id);
                return Response.status(200).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(422).header(Headers.PROCESS_ERROR, "Record couldnt be deleted").build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER,"idOrden: "+ idOrden +" ,idProductoPrecio: "+idProductoPrecio).build();
    }

    /**
     * Actualiza el COmboDetalle de base de datos
     *
     * @param registro entidda a ser actualizada
     * @param uriInfo info de url de donde se ha realizado la peticion
     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
     * problema y 500 si falla el servidor
     */
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(OrdenDetalle registro, @Context UriInfo uriInfo) {

        if (registro != null && registro.getOrdenDetallePK().getIdOrden()!= 0 && registro.getOrdenDetallePK().getIdProductoPrecio()!= 0) {

            try {
                odBean.update(registro,pBean);
               if (registro.getOrdenDetallePK().getIdProductoPrecio()!= 0 && registro.getOrdenDetallePK().getIdOrden() != 0) {
                    return Response.status(200).build();
                }
                return Response.status(500).header(Headers.PROCESS_ERROR, "Record couldnt be updated").build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
       }
       return Response.status(500).header(Headers.WRONG_PARAMETER, registro).build();
   }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{idOrden}/producto/{idProducto}/cantidad/{cantidad}/ordenDetalle")
    public Response generarOrdenDetalleProducto(@PathParam("idOrden") Long idOrden,
                                                @PathParam("idProducto") Long idProdcuto,
                                                @PathParam("cantidad")Integer cantidad){
        try {
            Orden orden= new Orden();
            orden.setIdOrden(idOrden);

            Producto producto=new Producto();
            producto.setIdProducto(idProdcuto);

            OrdenDetalle generado=odBean.generarOrdenDetalleProducto(orden,producto,cantidad);
            return Response.ok(generado).build();
        }catch (Exception e){
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return responseExcepcions(e,null);
        }
    }

    /**
     * Genera una lista de objetos OrdenDetalle a partir de un Combo seleccionado,
     * asignado a una orden existente y multiplicado por una cantidad dada del combo.
     *
     * Este método no guarda los detalles generados en la base de datos,
     * solo construye la lista a partir de los productos que componen el combo.
     *
     * @param idOrden ID de la orden a la cual se asociará el combo.
     * @param idCombo ID del combo del cual se tomarán los productos.
     * @param cantidadCombo Cantidad del combo (por defecto se asume 1 si es nulo o menor a 1).
     * @return Lista de objetos OrdenDetalle generados, o 204 si no hay productos válidos.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{idOrden}/combo/{idCombo}/cantidad/{cantidadCombo}/ordenDetalleCombo")
    public Response generarOrdenDetalleDesdeCombo(@PathParam("idOrden") Long idOrden,
                                                  @PathParam("idCombo") Long idCombo,
                                                  @PathParam("cantidadCombo") Integer cantidadCombo){
        try {
            Orden orden= new Orden();
            orden.setIdOrden(idOrden);
            Combo combo=new Combo();
            combo.setIdCombo(idCombo);

            List<OrdenDetalle> lista=odBean.generarOrdenDetalleDesdeCombo(orden,combo,cantidadCombo);
            if (lista==null || lista.isEmpty()){
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            return Response.ok(lista).build();
        }catch (Exception e){
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return responseExcepcions(e,null);
        }
    }

    /**
     * Genera una lista combinada de objetos OrdenDetalle a partir de productos y combos seleccionados.
     *
     * Este método permite construir múltiples detalles de una orden en una sola llamada,
     * utilizando productos individuales y combos, con cantidades específicas para cada grupo.
     *
     * @param datos Objeto JSON que contiene el id de la orden, listas de ids de productos y combos,
     *              y cantidades respectivas.
     * @return Lista de OrdenDetalle generados, o 204 si no se pudo generar ningún detalle.
     */

    public Response generarOrdeDetalleMixto(DatosMixtosDTO datos) {
        try {
            if (datos == null || datos.getIdOrden() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("La orden es requerida").build();
            }
            Orden orden = new Orden();
            orden.setIdOrden(datos.getIdOrden());

            List<Producto> productos = new ArrayList<>();
            if (datos.getIdProductos() != null) {
                for (Long isProd : datos.getIdProductos()) {
                    Producto producto = new Producto();
                    producto.setIdProducto(isProd);
                    productos.add(producto);
                }
            }
            List<Combo> combos = new ArrayList<>();
            if (datos.getIdCombos() != null) {
                for (Long isCombo : datos.getIdCombos()) {
                    Combo combo = new Combo();
                    combo.setIdCombo(isCombo);
                    combos.add(combo);
                }
            }
            List<OrdenDetalle> lista = odBean.generarOrdenDetalleMixto(orden, productos, combos,
                    datos.getCantidadProductos(), datos.getCantidadCombo());
            if (lista == null || lista.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            return Response.ok(lista).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return responseExcepcions(e, null);
        }
    }

}



