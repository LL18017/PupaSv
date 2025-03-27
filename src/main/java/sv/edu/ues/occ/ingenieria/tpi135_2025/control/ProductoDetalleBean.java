/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;

/**
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


    public ProductoDetalle findById(Integer idTipoProducto, Long idProducto) {
        if (idTipoProducto != null && idProducto != null) {
            try {
                return em.createNamedQuery("ProductoDetalle.findByIdTipoProductoAndIdProducto", ProductoDetalle.class)
                        .setParameter("idTipoProducto", idTipoProducto)
                        .setParameter("idProducto", idProducto)
                        .getSingleResult();
            }  catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                throw new IllegalStateException("error al aceder al repositorio", e);
            }
        }
        throw new IllegalArgumentException("idTipoProducto , idProducto,first y max no pueden ser nulos");
    }

    public Long countByIdProductoAndIdProducto(Integer idTipoProducto, Long idProducto) {
        if (idTipoProducto != null && idProducto != null) {
            try {
                return em.createNamedQuery("ProductoDetalle.countByIdTipoProductoAndIdProducto", Long.class)
                        .setParameter("idTipoProducto", idTipoProducto)
                        .setParameter("idProducto", idProducto)
                        .getSingleResult();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                throw new IllegalStateException("error al aceder al repositorio", e);

            }
        }

        throw new IllegalArgumentException("idTipoProducto , idProducto no pueden ser nulos");
    }


    public void deleteByPk(ProductoDetallePK pk) {
        if (pk != null) {
            try {
                em.createNamedQuery("ProductoDetalle.deleteByIdProductoAndIdProducto").setParameter("idProducto", pk.getIdProducto()).setParameter("idTipoProducto", pk.getIdTipoProducto()).executeUpdate();
                return;
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                throw new IllegalStateException("error al aceder al repositorio", e);
            }
        }
        throw new IllegalArgumentException("la llave no pueden ser null");
    }


    public List<ProductoDetalle> findAll(Integer first, Integer max) {
        if (first != null && max != null) {
            try {
                return em.createNamedQuery("ProductoDetalle.findAll", ProductoDetalle.class)
                        .setFirstResult(first)
                        .setMaxResults(max)
                        .getResultList();
            } catch (Exception e) {
                throw new IllegalStateException("error al aceder al repositorio", e);
            }
        }
        throw new IllegalArgumentException("first y max no pueden ser nulos");
    }

}
