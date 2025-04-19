//package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;
//
//import jakarta.inject.Inject;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.Context;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.core.UriInfo;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ComboDetalleBean;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//@Path("combo")
//public class ComboDetalleResource implements Serializable {
//    @Inject
//    ComboDetalleBean cdBean;
//
//
//    /**
//     * Metodo que devueleve un rango de datos de tipo ComboDetalle sin importar el idProductos o idCombo
//     * @param first la pocicion del primer dat
//     * @param max la cantidad de datos que se desea obtener
//     * @return una lista de tipo T si no definel los parametros entonces
//     * devuelve los primeros 20 registros
//     */
//    @Path("/producto/ComboDetalle")
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response findRange(
//            @QueryParam("first")
//            @DefaultValue("0") int first,
//            @QueryParam("max")
//            @DefaultValue("20") int max
//    ) {
//
//        try {
//            if ((first >= 0 && max >= 0 && max <=50) ) {
//
//                List<ComboDetalle> encontrados= cdBean.findRange(first,max);
//                long total=cdBean.count();
//                Response.ResponseBuilder builder = Response.ok(encontrados).
//                        header(Headers.TOTAL_RECORD, total).
//                        type(MediaType.APPLICATION_JSON);
//                return builder.build();
//            }else{
//                return Response.status(400).header("wrong parameter, first:", first+",max: "+max  ).header("wrong parameter : max","s").build();
//            }
//        }catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//
//    /**
//     * metodo que devueleve una rango de datos de tipo ComboDetalle con relacion a un idProductos
//     * @param first la pocicion del primer dat
//     * @param max la cantidad de datos que se desea obtener
//     * @return una lista de tipo T si no definel los parametros entonces
//     * @param idProducto
//     * devuelve los primeros 20 registros
//     */
//
//    @Path("/productos/{idProducto}/comboDetalle")
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response findRangeByIdProducto(@QueryParam("first") int first,
//                            @QueryParam("max") int max,
//                            @PathParam("idProducto") Long idProducto) {
//        try {
//            if (first >= 0 && max >= 0 && max <= 50) {
//
//                List<ComboDetalle> encontrados = cdBean.findByIdProducto(idProducto,first,max);
//                long total = cdBean.countByIdProducto(idProducto);
//                Response.ResponseBuilder builder = Response.ok(encontrados).
//                        header(Headers.TOTAL_RECORD, total).
//                        type(MediaType.APPLICATION_JSON);
//                return builder.build();
//            } else {
//                return Response.status(400).header("wrong parameter, first:", first + ",max: " + max +" , idProducto: "+idProducto).header("wrong parameter : max", "s").build();
//            }
//        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//
//    /**
//     * metodo que devueleve una rango de datos de tipo ComboDetalle con relacion a un idCombo
//     * @param first la pocicion del primer dat
//     * @param max la cantidad de datos que se desea obtener
//     * @return una lista de tipo T si no definel los parametros entonces
//     * @param idCombo id relacionado a combo detalle
//     * devuelve los primeros 20 registros
//     */
//
//    @Path("{idCombo}/productos/comboDetalle")
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response findRangeByIdCombo(@QueryParam("first") int first,
//                            @QueryParam("max") int max,
//                            @PathParam("idCombo") Long idCombo) {
//        try {
//            if (first >= 0 && max >= 0 && max <= 50) {
//
//                List<ComboDetalle> encontrados = cdBean.findByIdCombo(idCombo,first,max);
//                long total = cdBean.countByIdCombo(idCombo);
//                Response.ResponseBuilder builder = Response.ok(encontrados).
//                        header(Headers.TOTAL_RECORD, total).
//                        type(MediaType.APPLICATION_JSON);
//                return builder.build();
//            } else {
//                return Response.status(400).header("wrong parameter, first:", first + ",max: " + max +" , idCombo: "+idCombo).header("wrong parameter : max", "s").build();
//            }
//        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//    /**
//     * Metodo que devueleve un rango de datos de tipo ComboDetalle con relacion a un idComboy un idProducto
//     * @param first la pocicion del primer dat
//     * @param max la cantidad de datos que se desea obtener
//     * @return una lista de tipo T si no definel los parametros entonces
//     * @param idCombo id relacionado con comboDetalle
//     * devuelve los primeros 20 registros
//     */
//
//    @Path("{idCombo}/producto/{idProducto}/comboDetalle")
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response findRange(@QueryParam("first") int first,
//                            @QueryParam("max") int max,
//                            @PathParam("idCombo") Long idCombo ,
//                            @PathParam("idProducto") Long idProducto)
//    {
//        try {
//            if (first >= 0 && max >= 0 && max <= 50) {
//
//                List<ComboDetalle> encontrados = cdBean.findByIdComboAndIdProducto(idCombo,idProducto,first,max);
//                long total = cdBean.countByIdComboAndIdProducto(idCombo,idProducto);
//                Response.ResponseBuilder builder = Response.ok(encontrados).
//                        header(Headers.TOTAL_RECORD, total).
//                        type(MediaType.APPLICATION_JSON);
//                return builder.build();
//            } else {
//                return Response.status(400).header("wrong parameter, first:", first + ",max: " + max +" , idCombo: "+idCombo).header("wrong parameter : max", "s").build();
//            }
//        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    /**
//     * Metodo para encontrar un registro especifico de producto dado su id
//     * @param id del registro a buscar
//     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
//     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
//     * encuentra ningun registro con el id especificado 400 si se envia mal una
//     * parametro
//     */
//    @Path("producto/comboDetale/{id}")
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response findById(@PathParam("id") Integer id) {
//        if (id != null) {
//            try {
//                ComboDetalle encontrado = cdBean.findById(id);
//                if (encontrado != null) {
//                    Response.ResponseBuilder builder = Response.ok(encontrado);
//                    return builder.build();
//                }
//                return Response.status(404).header(Headers.NOT_FOUND_ID, id).build();
//            } catch (Exception e) {
//                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
//                return Response.status(500).entity(e.getMessage()).build();
//            }
//        }
//        return Response.status(400).header("wrong-parameter : id", id).build();
//    }
//
//    /**
//     * Borra un ComboDetalle Especifico
//     *
//     * @param idCombo id del Combo relacionado con ComboDetalle
//     * @param idProducto id del Combo relacionado con ComboDetalle
//     * @param uriInfo info de url de donde se ha realizado la peticion
//     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
//     * y 500 si falla el servdor
//     */
//    @DELETE
//    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
//    @Path("{idCombo}/producto/{idProducto}/comboDetalle/")
//    public Response delete(@PathParam("idCombo") Long idCombo, @PathParam("idProducto") Long idProducto, @Context UriInfo uriInfo) {
//
//        if (idCombo != null && idProducto != null) {
//            try {
//                ComboDetallePK id=new ComboDetallePK(idCombo,idProducto);
//                cdBean.delete(id);
//                return Response.status(200).build();
//            } catch (Exception e) {
//                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
//                return Response.status(422).header(Headers.PROCESS_ERROR, "Record couldnt be deleted").build();
//            }
//        }
//        return Response.status(500).header(Headers.WRONG_PARAMETER,"idCombo: "+ idCombo +" ,idProducto: "+idProducto).build();
//    }
//
//    /**
//     * Actualiza el COmboDetalle de base de datos
//     *
//     * @param registro entidda a ser actualizada
//     * @param uriInfo info de url de donde se ha realizado la peticion
//     * @return un status 200 si se actualizo la entidad , un 422 si hubo un
//     * problema y 500 si falla el servidor
//     */
//    @PUT
//    @Produces({MediaType.APPLICATION_JSON})
//    @Consumes({MediaType.APPLICATION_JSON})
//    public Response update(ComboDetalle registro, @Context UriInfo uriInfo) {
//
//        if (registro != null && registro.getComboDetallePK().getIdCombo()!= 0 && registro.getComboDetallePK().getIdProducto()!= 0) {
//
//            try {
////                cdBean.update(registro);
//                if (registro.getComboDetallePK().getIdCombo() != 0 && registro.getComboDetallePK().getIdProducto() != 0) {
//                    return Response.status(200).build();
//                }
//                return Response.status(500).header(Headers.PROCESS_ERROR, "Record couldnt be updated").build();
//            } catch (Exception e) {
//                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
//                return Response.status(500).entity(e.getMessage()).build();
//            }
//        }
//        return Response.status(500).header(Headers.WRONG_PARAMETER, registro).build();
//    }
//
//
//}
