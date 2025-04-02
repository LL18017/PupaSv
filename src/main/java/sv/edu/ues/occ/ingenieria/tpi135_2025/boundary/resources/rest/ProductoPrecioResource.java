package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.ProductoPrecioBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("productoPrecio")
public class ProductoPrecioResource extends Resource<ProductoPrecio> implements Serializable {

    @Inject
    ProductoPrecioBean ppBean;
    @Inject
    ProductoBean pBean;

    public ProductoPrecioResource() {
        super(ProductoPrecio.class);
    }

    @Override
    AbstractDataAccess<ProductoPrecio> getBean() {
        return ppBean;
    }


    /**
     * metodo que devueleve una rango de datos de tipo Producto
     *
     * @param first la pocicion del primer dat
     * @param max   la cantidad de datos que se desea obtener
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(@QueryParam("first") @DefaultValue("0") int first, @QueryParam("max") @DefaultValue("20") int max, @QueryParam("idProducto") @DefaultValue("0") Long idProducto) {
        try {

            Response verificarMaxAndFist = veririficarMaxAndFirst(first, max);
            if (verificarMaxAndFist.getStatus() != 200) return verificarMaxAndFist;
            if (idProducto != null && idProducto == 0) {
                List<ProductoPrecio> encontrados = ppBean.findRange(first, max);
                long total = ppBean.countByIdProducto((idProducto));
                return Response.ok(encontrados).header(Headers.TOTAL_RECORD, total).type(MediaType.APPLICATION_JSON).build();
            }

            Response verificarIdProduto = verificarId(idProducto, "idProdcuto");
            Response existeP = verificarProductoExiste(idProducto);

            if (verificarIdProduto.getStatus() != 200) return verificarIdProduto;
            if (existeP.getStatus() != 200) return existeP;

            List<ProductoPrecio> encontrados = ppBean.findByIdProducto(idProducto, first, max);
            long total = ppBean.countByIdProducto((idProducto));
            Response.ResponseBuilder builder = Response.ok(encontrados).
                    header(Headers.TOTAL_RECORD, total).
                    type(MediaType.APPLICATION_JSON);
            return builder.build();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Metodo para encontrar un registro especifico de producto dado su id
     *
     * @param idProductoPrecio del registro a buscar
     * @return un esatatus 200 se se encontro la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */

    @Path("{idProductoPrecio}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("idProductoPrecio") Long idProductoPrecio) {
        try {
            Response verificarIdPrecio = verificarId(idProductoPrecio, "idPrecio");
            if (verificarIdPrecio.getStatus() != 200) return verificarIdPrecio;

            ProductoPrecio encontrado = ppBean.findById(idProductoPrecio);
            if (encontrado != null) {
                Response.ResponseBuilder builder = Response.ok(encontrado).type(MediaType.APPLICATION_JSON);
                return builder.build();
            }
            return Response.status(404).header(Headers.NOT_FOUND_ID, idProductoPrecio).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Registra una entidad ProductoPrecio
     *
     * @param uriInfo informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
     */

    @Path("{idProducto}")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(ProductoPrecio registro, @Context UriInfo uriInfo, @PathParam("idProducto") Long idProducto) {

        try {
            Response verificarIdProducto = verificarId(idProducto, "idProducto");
            Response existeP = verificarProductoExiste(idProducto);
            if (verificarIdProducto.getStatus() != 200) return verificarIdProducto;
            if (existeP.getStatus() != 200) return existeP;

            registro.setIdProducto(new Producto(idProducto));
            ppBean.create(registro);
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            uriBuilder.path(String.valueOf(registro.getIdProductoPrecio()));
            return Response.created(uriBuilder.build()).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Borra un registro de tipo ProductoPrecio Especifico
     *
     * @param idProductoPrecio id del TipoProducto a eliminar
     * @param uriInfo          info de url de donde se esta realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{idProdcutoPrecio}")
    public Response delete(@PathParam("idProdcutoPrecio") Long idProductoPrecio, @Context UriInfo uriInfo) {
        try {
            Response verificarIdProdutoPrecio = verificarId(idProductoPrecio, "idProductoPrecio");
            if (verificarIdProdutoPrecio.getStatus() != 200) return verificarIdProdutoPrecio;

            Response existePp = verificarProductoPrecioExiste(idProductoPrecio);
            if (existePp.getStatus() != 200) return existePp;

            ppBean.delete(idProductoPrecio);
            return Response.status(200).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).header(Headers.PROCESS_ERROR, "Record couldnt be deleted").build();
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

    @Path("{idProductoPrecio}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(ProductoPrecio registro, @Context UriInfo uriInfo, @PathParam("idProductoPrecio") Long idProductoPrecio) {
        try {
            Response verificarIdProdutoPrecio = verificarId(idProductoPrecio, "idProductoPrecio");
            if (verificarIdProdutoPrecio.getStatus() != 200) return verificarIdProdutoPrecio;

            Response existePp = verificarProductoPrecioExiste(idProductoPrecio);
            if (existePp.getStatus() != 200) return existePp;
            registro.setIdProductoPrecio(idProductoPrecio);
            ppBean.update(registro);
            return Response.status(200).build();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }


}
