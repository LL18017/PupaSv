package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("orden")
public class OrdenResource extends Resource<Orden> implements Serializable {

    @Inject
    OrdenBean oBean;

    public OrdenResource() {
        super(Orden.class);
    }

    @Override
    AbstractDataAccess<Orden> getBean() {
        return oBean;
    }

    /**
     * metodo que devueleve una rango de datos de tipo Orden
     *
     * @param first la pocicion del primer dat
     * @param max   la cantidad de datos que se desea obtener
     * @return una lista de tipo T si no definel los parametros entonces
     * devuelve los primeros 20 registros
     */

    @GET
    @Path("")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(@QueryParam("first") @DefaultValue("0") Integer first, @QueryParam("max") @DefaultValue("20") Integer max) {
        try {
            System.out.println("First: " + first + ", Max: " + max);
            Response verificarMaxAndFirst = veririficarMaxAndFirst(first, max);
            if (verificarMaxAndFirst.getStatus() != 200) return verificarMaxAndFirst;

            List<Orden> encontrados = oBean.findRange(first, max);
            long total = oBean.count();
            Response.ResponseBuilder builder = Response.ok(encontrados).
                    header(Headers.TOTAL_RECORD, total).
                    type(MediaType.APPLICATION_JSON);
            return builder.build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Metodo para encontrar un registro especifico de producto dado su id
     *
     * @param id del registro a buscar
     * @return un esatatus 200 si se logro encontrar la entidad junto con dicha entidad
     * un estatus 500 en dado caso falle el servidor un estatus 404 si no se
     * encuentra ningun registro con el id especificado 400 si se envia mal una
     * parametro
     */

    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        if (id > 0) {
            try {
                Response verificarId = verificarId(id, "idOrden");
                if (verificarId.getStatus() != 200) return verificarId;
                return verificarExistencia(id);
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER, "id: " + id).build();
    }

    /**
     * Registra una entidad Orden
     *
     * @param uriInfo informacion de URl donde se encuantra la peticion
     * @return un estatus 201 si la entidad es creada junto con la url donde se
     * puede encontra dicha entidad 422 en dado caso falle la creacion de la
     * entidad y 500 si por fall el servidor
     */

    @Path("")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response create(Orden registro, @Context UriInfo uriInfo) {
        try {
            Response verificarEntidad = verificarEntity(registro);
            if (verificarEntidad.getStatus() != 200) return verificarEntidad;
            oBean.create(registro);

            if (registro.getIdOrden() != null) {
                UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
                uriBuilder.path(String.valueOf(registro.getIdOrden()));
                return Response.created(uriBuilder.build()).build();
            }
            return Response.status(422).header(Headers.UNPROCESSABLE_ENTITY, "Record couldnt be created").build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Borra un registro de tipo Orden Especifico
     *
     * @param id      id del Orden a eliminar
     * @param uriInfo info de url de donde se esta realizado la peticion
     * @return un status 200 si se borro la entidad , un 422 si hubo un problema
     * y 500 si falla el servdor
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{id}")
    public Response delete(@PathParam("id") Long id, @Context UriInfo uriInfo) {
        try {
            Response verificarId = verificarId(id, "idOrden");
            if (verificarId.getStatus() != 200) return verificarId;
            Response verificarExistencia = verificarExistencia(id);
            if (verificarExistencia.getStatus() != 200) return verificarExistencia;
            oBean.delete(id);
            return Response.status(200).build();
        } catch (Exception e) {
            return Response.status(422).header(Headers.PROCESS_ERROR, "Record couldnt be deleted").build();
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
    @Path("{idOrden}")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(Orden registro, @PathParam("idOrden") Long idOrden, @Context UriInfo uriInfo) {
        try {
            Response verificarid = verificarId(idOrden, "idOrden");
            if (verificarid.getStatus() != 200) return verificarid;
            Response encontrado = verificarExistencia(idOrden);
            if (encontrado.getStatus() != 200) return encontrado;
            registro.setIdOrden(idOrden);

            oBean.update(registro);
            if (registro.getIdOrden() != null) {
                return Response.status(200).build();
            }
            return Response.status(422).header(Headers.PROCESS_ERROR, "error al procesar peticion").build();

        } catch (Exception e) {
            return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
        }


    }
}
