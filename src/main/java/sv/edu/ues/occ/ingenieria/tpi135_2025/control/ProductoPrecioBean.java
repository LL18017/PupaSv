/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoPrecio;

/**
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


    public void setEntityManager(EntityManager em) {
        this.em = em;
    }


    /**
     * Devuelve el nombre del parámetro por el cual se ordenarán los resultados por defecto.
     *
     * @return el nombre del campo usado para ordenar: "idProductoPrecio".
     */
    @Override
    public String orderParameterQuery() {
        return "idProductoPrecio";
    }

    public ProductoPrecio findByIdProducto(Long idProducto) {
        if (idProducto != null) {
            try {
                return em.createNamedQuery("ProductoPrecio.findByIdTipoProductoAndIdProducto", ProductoPrecio.class)
                        .setParameter("idProducto", idProducto)
                        .getSingleResult();
            } catch (Exception e) {
                throw new IllegalStateException("error al acceder al repositorio ", e);
            }
        }
        throw new IllegalArgumentException("idProducto no puede ser nullo o menor que 0");
    }

    /**
     * Cuenta cuántos registros de ProductoPrecio existen asociados a un producto específico.
     *
     * @param idProducto el identificador del producto para el cual se desea contar los precios.
     *                   No puede ser nulo ni menor que 0.
     * @return la cantidad de registros de ProductoPrecio asociados al producto.
     * @throws IllegalArgumentException si el idProducto es nulo o menor que 0.
     * @throws IllegalStateException    si ocurre un error al ejecutar la consulta en la base de datos.
     */
    public Long countByIdProducto(Long idProducto) {
        if (idProducto == null || idProducto < 0) {
            throw new IllegalArgumentException("idProducto no puede ser nulo o menor que 0");
        }
        try {
            return em.createNamedQuery("ProductoPrecio.countByIdTipoProductoAndIdProducto", Long.class)
                    .setParameter("idProducto", idProducto)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException("error al acceder al repositorio ", e);
        }
    }

    public void create(ProductoPrecio registro, Long idProducto) throws IllegalStateException, IllegalArgumentException {
        if (idProducto == null) {
            throw new IllegalArgumentException("idProducto no puede ser nulo o menor que 0");
        }
        if (idProducto <= 0) {
            throw new IllegalArgumentException("idProducto no puede ser mayor que 0");
        }
        try {
            Producto existe = em.find(Producto.class, idProducto);
            if (existe == null) {
                throw new EntityNotFoundException("no existe el producto");
            }
            registro.setIdProducto(new Producto(idProducto));
            super.create(registro);
        } catch (EntityNotFoundException e) {
            throw e;
        }catch (Exception e) {
            throw e;
        }
    }
}