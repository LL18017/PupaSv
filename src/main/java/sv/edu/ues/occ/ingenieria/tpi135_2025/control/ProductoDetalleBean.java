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

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;

/**
 *
 * @author mjlopez bean para control de entidad ProductoDetalle
 */
@LocalBean
@Stateless
public class ProductoDetalleBean extends AbstractDataAccess<ProductoDetalle> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ProductoDetalleBean() {
        super(ProductoDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idProductoDetalle";
    }

    public List<ProductoDetalle> findByIdProductoAndIdProductoDetalle(Integer idTipoProducto, Long idProducto, int first ,int max) {
        try {
            return em.createNamedQuery("ProductoDetalle.findByIdTipoProductoAndIdProducto",ProductoDetalle.class)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .setParameter("idProducto", idProducto)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return List.of();
    }
    public Long countByIdProductoAndIdProductoDetalle(Integer idTipoProducto, Long idProducto) {
        try {
            return em.createNamedQuery("ProductoDetalle.countByIdTipoProductoAndIdProducto",Long.class)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .setParameter("idProducto", idProducto)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return 0L;
    }

}
