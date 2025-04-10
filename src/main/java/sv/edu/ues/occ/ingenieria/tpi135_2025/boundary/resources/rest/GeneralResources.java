//package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;
//
//import jakarta.inject.Inject;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
//import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//@Path("all")
//
//
//
//public class GeneralResources implements Serializable {
//    @Inject
//    ProductoBean pBean;
//    @Inject
//    ProductoDetalleBean pdBean;
//    @Inject
//    ProductoPrecioBean ppBean;
//
//    @Path("productos")
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response findRangeProducto(@QueryParam("first")
//                              @DefaultValue("0")
//                              int first,
//                              @QueryParam("max")
//                              @DefaultValue("20")
//                              int max) {
//        try {
//            if (first >= 0 && max >= 0 && max <= 50) {
//                List<Producto> encontrados = pBean.findRange(first, max);
//                long total = pBean.count();
//                Response.ResponseBuilder builder = Response.ok(encontrados).
//                        header(Headers.TOTAL_RECORD, total).
//                        type(MediaType.APPLICATION_JSON);
//                return builder.build();
//            } else {
//                return Response.status(400).header("wrong parameter, first:", first + ",max: " + max).header("wrong parameter : max", "s").build();
//            }
//        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//    /**
//     * metodo que devueleve una rango de datos de tipo ProductoDetalle sin importar ids
//     *
//     * @return una lista de tipo T si no definel los parametros entonces
//     * devuelve los primeros 20 registros
//     */
//
//    @Path("producto/detalle")
//    @GET
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response findRangeProductoDetalle(
//            @QueryParam("first")
//            @DefaultValue("0") int first,
//            @QueryParam("max")
//            @DefaultValue("20") int max
//
//    ) {
//        try {
//
//            List<ProductoDetalle> encontrados = pdBean.findRange(first,max);
//            long total = pdBean.count();
//            Response.ResponseBuilder builder = Response.ok(encontrados).
//                    header(Headers.TOTAL_RECORD, total).
//                    type(MediaType.APPLICATION_JSON);
//            return builder.build();
//        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//
//    /**
//     * Metodo que devueleve un rango de datos de tipo Producto sin importar el idTipoProductos
//     * @param first la pocicion del primer dat
//     * @param max la cantidad de datos que se desea obtener
//     * @return una lista de tipo T si no definel los parametros entonces
//     * devuelve los primeros 20 registros
//     */
//
//    @Path("producto/precio")
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response findRange(int first, int max) {
//        try {
//            if (first >= 0 && max >= 0 && max <= 50) {
//
//                List<ProductoPrecio> encontrados = ppBean.findRange(first, max);
//                long total = ppBean.count();
//                Response.ResponseBuilder builder = Response.ok(encontrados).
//                        header(Headers.TOTAL_RECORD, total).
//                        type(MediaType.APPLICATION_JSON);
//                return builder.build();
//            } else {
//                return Response.status(400).header("wrong parameter, first:", first + ",max: " + max).header("wrong parameter : max", "s").build();
//            }
//        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//
//}
