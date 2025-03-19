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
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

/**
 *
 * @author mjlopez bean para control de entidad productoPrecio
 */
@LocalBean
@Stateless
public class ProductoPrecioBean extends AbstractDataAccess<ProductoPrecio> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ProductoPrecioBean() {
        super(ProductoPrecio.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idProductoPrecio";
    }

    public List<ProductoPrecio> findByIdProducto(Integer idProducto, int first , int max) {
        try {
            return em.createNamedQuery("ProductoPrecio.findByIdTipoProductoAndIdProducto", ProductoPrecio.class)
                    .setParameter("idProducto", idProducto)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return List.of();
    }
    public Long countByIdProducto(Integer idProducto) {
        try {
            return em.createNamedQuery("ProductoPrecio.countByIdTipoProductoAndIdProducto",Long.class)
                    .setParameter("idProducto", idProducto)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return 0L;
    }

}
