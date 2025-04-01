/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.annotation.Resource;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.transaction.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;

/**
 * @author mjlopez bean para control de entidad Producto
 */
@Stateless
@LocalBean
public class ProductoBean extends AbstractDataAccess<Producto> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ProductoBean() {
        super(Producto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idProducto";
    }


    public List<Producto> findRangeByIdTipoProductosAndActivo(Integer idTipoProducto, boolean activo, Integer first, Integer max) {
        if (first != null && max != null && idTipoProducto != null) {
            try {
                return em.createNamedQuery("Producto.findActivosAndIdTipoProducto", Producto.class)
                        .setParameter("idTipoProducto", idTipoProducto)
                        .setParameter("activo", activo)
                        .setFirstResult(first)
                        .setMaxResults(max)
                        .getResultList();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                throw new IllegalStateException("error al aceder al repositorio", e);
            }
        }
        throw new IllegalArgumentException("first , idTipoProducto y max no pueden ser null");
    }

    public Long countByIdTipoProductosAndActivo(Integer idTipoProducto, boolean activo) {

        if (idTipoProducto != null) {
            try {
                return em.createNamedQuery("Producto.countActivosAndIdTipoProducto", Long.class)
                        .setParameter("idTipoProducto", idTipoProducto)
                        .setParameter("activo", activo)
                        .getSingleResult();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                throw new IllegalStateException("error al aceder al repositorio", e);
            }
        }

        throw new IllegalArgumentException("first , idTipoProducto y max no pueden ser null");
    }


    public void createProducto(Producto registro, Integer idTipoProducto) {
        if (registro == null || idTipoProducto == null) {
            throw new IllegalArgumentException("El registro y el idTipoProducto no pueden ser nulos");
        }
        try {
            em.persist(registro);
            em.flush();
            em.refresh(registro);
            ProductoDetalle detalle = new ProductoDetalle(idTipoProducto, registro.getIdProducto());
            detalle.setActivo(true);
            em.persist(detalle);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error durante la creación", e);
            throw new IllegalStateException("error al aceder al repositorio", e);
        }
    }

    public List<Producto> findRangeProductoActivos(Integer first, Integer max ,Boolean activo) {
        if (first == null || max == null || first < 0 || max < 0) {
            throw new IllegalArgumentException("first , max no pueden ser nulos o menores que cero");
        }
        try {
            return em.createNamedQuery("Producto.findByAnyActivo", Producto.class)
                    .setParameter("activo", activo)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (IllegalStateException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException("error al aceder al repositorio", e);
        }
    }

    public Long countProductoActivos(Boolean activo) {

        try {
            return em.createNamedQuery("Producto.countByAnyActivo", Long.class)
                    .setParameter("activo", activo)
                    .getSingleResult();
        } catch (IllegalStateException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException("error al aceder al repositorio", e);
        }
    }

    @Override
    public void delete(Object id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del producto no puede ser null");
        }
        try {
            em.createNamedQuery("ProductoDetalle.deleteByIdProducto").setParameter("idProducto", id).executeUpdate();
            super.delete(id);
            return;
        } catch (Exception e) {
            throw new IllegalStateException("error en el repositorio");
        }
    }
}
