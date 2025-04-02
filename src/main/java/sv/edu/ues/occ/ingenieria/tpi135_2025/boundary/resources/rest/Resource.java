package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.TipoProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;


public abstract class Resource<T> {
    final Class tipo;

    public Resource(Class tipo) {
        this.tipo = tipo;
    }

    abstract AbstractDataAccess<T> getBean();

    @Inject
    ProductoBean pBean;
    @Inject
    TipoProductoBean tpBean;
    @Inject
    ProductoPrecioBean ppBean;

    /**
     * Verifica que el ID del producto sea válido.
     *
     * @param id ID a ser analizado
     * @return un status 200 si se envió correctamente, 400 si el ID es inválido, y 500 si ocurre un error en el servidor.
     */
    public Response verificarId(Number id, String tipoId) {
        try {
            if (id == null || id.longValue() <= 0) {
                return Response.status(400)
                        .header(Headers.WRONG_PARAMETER, tipoId + " inválido: " + id)
                        .build();
            }
        } catch (Exception e) {
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }
        return Response.status(200).build();
    }


    /**
     * Verifica que los paramentros first y max sean correctos
     *
     * @param first inicio
     * @param max   cantidda maxima
     * @return un status 200 si se envvio correctamente en caso contrario retorna un 400
     * problema y 500 si falla el servidor
     */
    public Response veririficarMaxAndFirst(Integer first, Integer max) {
        try {
            if (first < 0 || max <= 0 || max > 50) {
                return Response.status(400)
                        .header(Headers.WRONG_PARAMETER, "first: " + first + ", max: " + max)
                        .build();
            }
        } catch (Exception e) {
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }
        return Response.status(200).build();
    }

    /**
     * Verifica la existencia de un tipoProducto
     *
     * @param idTipoProducto id a ser veridicado
     * @return un status 200 si se envvio correctamente en caso contrario retorna un 404
     * problema y 500 si falla el servidor
     */
    public Response verificarTipoProductoExiste(Integer idTipoProducto) {
        try {
            TipoProducto encontrado = tpBean.findById(idTipoProducto);
            if (encontrado == null) {
                return Response.status(404).header(Headers.NOT_FOUND_ID, idTipoProducto).build();
            }
        } catch (Exception e) {
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }
        return Response.status(200).build();
    }

    /**
     * Verifica la existencia de un tipoProducto
     *
     * @param idProducto id a ser veridicado
     * @return un status 200 si se envvio correctamente en caso contrario retorna un 404
     * problema y 500 si falla el servidor
     */
    public Response verificarProductoExiste(Long idProducto) {
        try {
            Producto encontrado = pBean.findById(idProducto);
            if (encontrado == null) {
                return Response.status(404).header(Headers.NOT_FOUND_ID, idProducto).build();
            }
        } catch (Exception e) {
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }
        return Response.status(200).build();
    }

    /**
     * Verifica la existencia de un productoPrecio
     *
     * @param idProductoPrecio id a ser veridicado
     * @return un status 200 si se envvio correctamente en caso contrario retorna un 404
     * problema y 500 si falla el servidor
     */
    public Response verificarProductoPrecioExiste(Long idProductoPrecio) {
        try {
            ProductoPrecio encontrado = ppBean.findById(idProductoPrecio);
            if (encontrado == null) {
                return Response.status(404).header(Headers.NOT_FOUND_ID, idProductoPrecio).build();
            }
        } catch (Exception e) {
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }
        return Response.status(200).build();
    }

    public Response verificarExistencia(Number id) {
        try {
            T encontrado = getBean().findById(id);
            if (encontrado == null) {
                return Response.status(404).header(Headers.NOT_FOUND_ID, id).build();
            }
            return Response.ok(encontrado).build();
        } catch (Exception e) {
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }
    }
    public Response verificarEntity(T entity) {
        if (entity == null) {
            return Response.status(400).header(Headers.WRONG_PARAMETER, entity).build();
        }
        return Response.ok(entity).build();
    }

}
