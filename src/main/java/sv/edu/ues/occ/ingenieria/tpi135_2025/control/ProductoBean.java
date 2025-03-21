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

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

/**
 *
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

    public List<Producto> findRangeByIdTipoProductos(Integer idTipoProducto,Integer firstResult, Integer max) {

        try {
            return em.createNamedQuery("Producto.findByidTipoProducto",Producto.class)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .setFirstResult(firstResult)
                    .setMaxResults(max)
                    .getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return List.of();
    }
    public Integer countByIdTipoProductos(Integer idTipoProducto,Integer firstResult, Integer max) {

        try {
            return em.createNamedQuery("Producto.countByidTipoProducto",Integer.class)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return 0;
    }



}
