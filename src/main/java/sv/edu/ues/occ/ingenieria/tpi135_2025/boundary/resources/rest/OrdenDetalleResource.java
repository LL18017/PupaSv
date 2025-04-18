/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.DatosMixtosDTO;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("ordenDetalle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdenDetalleResource extends GeneralRest implements Serializable {
    @Inject
    OrdenDetalleBean odBean;

    private EntityManager em;

    /**
     * Método que devuelve un rango de datos de tipo OrdenDetalle.
     * Si no se especifican los parámetros 'first' y 'max', se devolverán los primeros 20 registros.
     *
     * @param first La posición del primer dato a obtener (índice de inicio para la paginación).
     * @param max   La cantidad máxima de registros a obtener.
     * @return Una lista de objetos de tipo OrdenDetalle, o los primeros 20 registros si no se definen los parámetros.
     */
    //URL:http://localhost:9080/PupaSv-1.0-SNAPSHOT/GET /v1/ordenDetalle?idOrden=1&first=0&max=20
    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(@QueryParam("idOrden") Long idOrden,
                              @QueryParam("first") @DefaultValue("0") int first,
                              @QueryParam("max") @DefaultValue("20") int max) {
        try {
            if (idOrden == null) {
                return Response.status(400).
                        entity("El parametro 'idOrden' es obligatorio")
                        .build();
            }
            if (first < 0 || max < 0) {
                return Response.status(400)
                        .entity("Los parametros 'first' y 'max' deben ser mayores o iguales a 0.")
                        .build();
            }
            if (max > 50) {
                return Response.status(400)
                        .entity("El parametro 'max' no puede ser mayor a 50.")
                        .build();
            }
            List<OrdenDetalle> encontrados = odBean.findRangeByIdOrden(idOrden, first, max);
            Long total = odBean.countByIdOrden(idOrden);

            return Response.ok(encontrados)
                    .header(Headers.TOTAL_RECORD, total)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            Logger.getLogger(OrdenDetalleResource.class.getName()).log(Level.SEVERE, null, e);
            return Response.status(500)
                    .entity("Hubo un prloblema al proceder la solicitud" + e.getMessage())
                    .build();
        }

    }


    /**
     * metodo que devueleve una rango de datos de tipo OrdenDetalle con relacion a un idOrden
     *
     * @param first   la pocicion del primer dat
     * @param max     la cantidad de datos que se desea obtener
     * @param idOrden devuelve los primeros 20 registros
     * @return una lista de tipo T si no definel los parametros entonces
     */

    //URL:http://localhost:9080/PupaSv-1.0-SNAPSHOT/v1/ordenDetalle/orden/{idOrden}
    @Path("orden/{idOrden}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRangeByIdOrden(@QueryParam("first") int first,
                                       @QueryParam("max") int max,
                                       @PathParam("idOrden") Long idOrden) {
        try {
            if (first >= 0 && max >= 0 && max <= 50) {

                List<OrdenDetalle> encontrados = odBean.findRangeByIdOrden(idOrden, first, max);
                long total = odBean.countByIdOrden(idOrden);
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            } else {
                return Response.status(400).header(Headers.WRONG_PARAMETER, "first: " + first + " max :" + max + " , idOrden" + idOrden).header("wrong parameter : max", "s").build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
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
        if (idOrden != null && idProductoPrecio != null) {
            try {

                OrdenDetalle encontrado = odBean.findByIdOrdenAndIdPrecioProducto(idOrden, idProductoPrecio);
                if (encontrado != null) {
                    Response.ResponseBuilder builder = Response.ok(encontrado);
                    return builder.build();
                }
                return Response.status(404)
                        .header(Headers.NOT_FOUND_ID, "idOrden: " + idOrden + " , idProducto: " + idProductoPrecio)
                        .build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER, "id :" + idOrden).build();
    }

    /**
     * Borra un OrdenDetalle Especifico
     *
     * @param idOrden          id del Combo relacionado con ComboDetalle
     * @param idProductoPrecio id del Combo relacionado con ComboDetalle
     * @param uriInfo          info de url de donde se ha realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    //URL:http://localhost:9080/PupaSv-1.0-SNAPSHOT/v1/ordenDetalle/orden/{idOrden}/productoPrecio/{idProductoPrecio}
    @Path("orden/{idOrden}/productoPrecio/{idProductoPrecio}/")
    public Response delete(@PathParam("idOrden") Long idOrden, @PathParam("idProductoPrecio") Long idProductoPrecio, @Context UriInfo uriInfo) {

        if (idOrden != null && idProductoPrecio != null) {
            try {
                OrdenDetallePK id = new OrdenDetallePK(idOrden, idProductoPrecio);
                odBean.delete(id);
                return Response.status(200).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al eliminar: " + e.getMessage(), e);
                return Response.status(422).header(Headers.PROCESS_ERROR, "Record couldn't be deleted").build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, "idOrden: " + idOrden + " ,idProductoPrecio: " + idProductoPrecio).build();
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
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(OrdenDetalle registro, @Context UriInfo uriInfo) {

        if (registro != null && registro.getOrdenDetallePK().getIdOrden() != 0 && registro.getOrdenDetallePK().getIdProductoPrecio() != 0) {

            try {
                odBean.update(registro, pBean);
                if (registro.getOrdenDetallePK().getIdProductoPrecio() != 0 && registro.getOrdenDetallePK().getIdOrden() != 0) {
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


    //
    @GET
    @Path("producto")
    @Produces(MediaType.APPLICATION_JSON)
    //esta URL ES CONFUSA HACELA MEJOR CON QUERY PARAMS
    //URL:http://localhost:9080/PupaSv-1.0-SNAPSHOT/v1/ordenDetalle/producto?orden=1&producto=5&cantidad=3
    public Response generarOrdenDetalleProducto(
            @QueryParam("orden") @DefaultValue("0") Long idOrden,
            @QueryParam("producto") @DefaultValue("0") Long idProducto,
            @QueryParam("cantidad") @DefaultValue("0") Integer cantidad) {
        try {
            if (idOrden == 0 || idProducto == 0 || cantidad == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Todos los parámetros son obligatorios y deben ser mayores a cero.")
                        .build();
            }
            Orden orden = new Orden();
            orden.setIdOrden(idOrden);
            Producto producto = new Producto();
            producto.setIdProducto(idProducto);

            odBean.generarOrdenDetalleProducto(1L, 1L, cantidad);
            return Response.ok().build();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return responseExcepcions(e, null);
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
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //URL:http://localhost:9080/PupaSv-1.0-SNAPSHOT/v1/ordenDetalle/combo?orden=1&combo=2&cantidad=3
    @Path("combo")
    public Response generarOrdenDetalleDesdeCombo(
            @QueryParam("orden") @DefaultValue("0") Long idOrden,
            @QueryParam("combo") @DefaultValue("0") Long idCombo,
            @QueryParam("cantidad") @DefaultValue("1") Integer cantidadCombo) {
        try {
            if (idOrden == 0 || idCombo == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Los parámetros 'orden' y 'combo' son obligatorios y deben ser mayores a cero.")
                        .build();
            }
            Orden orden = new Orden();
            orden.setIdOrden(idOrden);

            Combo combo = new Combo();
            combo.setIdCombo(idCombo);

            odBean.generarOrdenDetalleDesdeCombo(orden.getIdOrden(), combo.getIdCombo(), cantidadCombo);

            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
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

    public List<OrdenDetalle> generarOrdenDetalleMixto(Orden orden, List<Producto> productos, List<Combo> combos,
                                                       List<Integer> cantidadProductos, List<Integer> cantidadCombo) {
        if (orden == null || orden.getIdOrden() == null) {
            throw new IllegalArgumentException("La orden es requerida");
        }
        List<OrdenDetalle> ordenDetalles = new ArrayList<>();
        if (productos != null && !productos.isEmpty()) {
            if (cantidadProductos == null || cantidadProductos.size() != productos.size()) {
                throw new IllegalArgumentException("La lista de cantidades de productos no coincide con la lista de productos");
            }
            for (int i = 0; i < productos.size(); i++) {
                Producto producto = productos.get(i);
                Integer cantidad = cantidadProductos.get(i);
                if (producto == null || producto.getIdProducto() == null) {
                    continue;
                }
                ProductoPrecio precio = null;
                try {
                    precio = em.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)
                            .setParameter("idProducto", producto.getIdProducto())
                            .setMaxResults(1)
                            .getSingleResult();
                } catch (NoResultException e) {
                    continue;
                }
                if (precio == null) {
                    continue;
                }

                OrdenDetalle detalle = new OrdenDetalle();
                detalle.setOrden(orden);
                detalle.setProductoPrecio(precio);
                detalle.setCantidad(cantidad);
                detalle.setPrecio(precio.getPrecioSugerido());
                ordenDetalles.add(detalle);
            }
        }
        if (combos != null && !combos.isEmpty()) {
            if (cantidadCombo == null || cantidadCombo.size() != combos.size()) {
                throw new IllegalArgumentException("La lista de cantidades de combos no coincide con la lista de combos");
            }
            for (int i = 0; i < combos.size(); i++) {
                Combo combo = combos.get(i);
                Integer cantidad = cantidadCombo.get(i);

                if (combo == null || combo.getIdCombo() == null) {
                    continue;
                }
                List<ComboDetalle> comboDetalles = em.createNamedQuery("ComboDetalle.findByIdCombo", ComboDetalle.class)
                        .setParameter("idCombo", combo.getIdCombo())
                        .getResultList();
                if (comboDetalles == null || comboDetalles.isEmpty()) {
                    continue;
                }

                for (ComboDetalle comboDetalle : comboDetalles) {
                    Producto producto = comboDetalle.getProducto();
                    if (producto == null || producto.getIdProducto() == null) {
                        continue;
                    }
                    ProductoPrecio precio = null;
                    try {
                        precio = em.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)
                                .setParameter("idProducto", producto.getIdProducto())
                                .setMaxResults(1)
                                .getSingleResult();
                    } catch (NoResultException e) {
                        continue;
                    }
                    if (precio == null) {
                        continue;
                    }
                    int cantidadFinal = comboDetalle.getCantidad() * cantidad;

                    OrdenDetalle detalle = new OrdenDetalle();
                    detalle.setOrden(orden);
                    detalle.setProductoPrecio(precio);
                    detalle.setCantidad(cantidadFinal);
                    detalle.setPrecio(precio.getPrecioSugerido());
                    ordenDetalles.add(detalle);
                }
            }
        }
        return ordenDetalles;
    }

}