/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.boundary.resources.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.AbstractDataAccess;
import sv.edu.ues.occ.ingenieria.tpi135_2025.control.OrdenDetalleBean;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

/**
 *
 * @author mjlopez
 */
@Path("orden/ordendetalle")
public class OrdenDetalleResource implements Serializable {

    @Inject
    OrdenDetalleBean odBean;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first")
            @DefaultValue("0") int first,
            @QueryParam("max")
            @DefaultValue("20") int max,
            @QueryParam("idOrden") Long idOrden
    ) {

        try {
            if ((first >= 0 && max >= 0 && max <=50) || idOrden==null) {

                if (idOrden==null){
                    List<OrdenDetalle> allOrdenDetalle=odBean.findAll();
                    long totalOrdenDetalleByIdOrden=odBean.count();
                    Response.ResponseBuilder builder = Response.ok(allOrdenDetalle).
                            header(Headers.TOTAL_RECORD, totalOrdenDetalleByIdOrden).
                            type(MediaType.APPLICATION_JSON);
                    return builder.build();

                }
            List<OrdenDetalle> encontrados= odBean.findRangeByIdOrden(idOrden,first,max);
            long total=odBean.count();
                Response.ResponseBuilder builder = Response.ok(encontrados).
                        header(Headers.TOTAL_RECORD, total).
                        type(MediaType.APPLICATION_JSON);
                return builder.build();
            }else{
                return Response.status(400).header("wrong parameter, first:", first+",max: "+max +" ,idOrden: "+idOrden  ).header("wrong parameter : max","s").build();
            }
        }catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

}
