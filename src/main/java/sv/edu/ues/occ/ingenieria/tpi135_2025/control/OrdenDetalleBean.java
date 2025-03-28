/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetalle;

/**
 *
 * @author mjlopez clase bean para control de la entidad OrdenDetalle
 */
@LocalBean
@Stateless
public class OrdenDetalleBean extends AbstractDataAccess<OrdenDetalle> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public OrdenDetalleBean() {
        super(OrdenDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idOrdenDetalle";
    }
    
    public OrdenDetalle findByIdOrdenAndIdPrecioProducto(Long idOrden,Long idProductoPrecio) {
        try {
            return em.createNamedQuery("OrdenDetalle.findByPrecioProductoAndIdOrden",OrdenDetalle.class)
                    .setParameter("idOrden", idOrden)
                    .setParameter("idProductoPrecio", idProductoPrecio)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        
        return null;
    }
    public List<OrdenDetalle> findRangeByIdOrden(Long idOrden,int first , int max) {
        try {
            return em.createNamedQuery("OrdenDetalle.findByIdOrden",OrdenDetalle.class)
                    .setParameter("idOrden", idOrden)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }

        return List.of();
    }
    public Long countByIdOrden(Long idOrden) {
        try {
            return em.createNamedQuery("OrdenDetalle.countByIdOrden",Long.class)
                    .setParameter("idOrden", idOrden)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }

        return 0L;
    }


}
