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


    public List<Producto> findRangeByIdTipoProductos(Integer idTipoProducto, Integer first, Integer max) {
        if (first != null && max != null && idTipoProducto != null) {
            try {
                return em.createNamedQuery("Producto.findByIdTipoProducto", Producto.class)
                        .setParameter("idTipoProducto", idTipoProducto)
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

    public Long countByIdTipoProductos(Integer idTipoProducto) {

        if (idTipoProducto != null) {
            try {
                return em.createNamedQuery("Producto.countByidTipoProducto", Long.class)
                        .setParameter("idTipoProducto", idTipoProducto)
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


}
