/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

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

    /**
     * Encuentra un objeto específico por su ID. Verifica que el ID no sea nulo
     * antes de realizar la búsqueda.
     *
     * @param idProducto     id del producto relacionado al detalle
     * @param idTipoProducto id del tipo producto relacionado al detalle
     * @return el detalle correspondiente a los ids enviados.
     * @throws IllegalArgumentException Si el ID es nulo o inválido.
     * @throws IllegalStateException    Si no se puede acceder al repositorio.
     * @throws EntityNotFoundException  Si no existe una entidad con ese id.
     * @throws PersistenceException     si hay un error general con el repositorio.
     */
    public ProductoDetalle findById(Integer idTipoProducto, Long idProducto) {
        if (idTipoProducto == null || idTipoProducto <= 0)
            throw new IllegalArgumentException("idTipoProducto no pueden ser nulos o menor a cero");
        if (idProducto == null || idProducto <= 0)
            throw new IllegalArgumentException("idTipoProducto no pueden ser nulos o menor a cero");

        try {
            Producto producto = em.find(Producto.class, idProducto);
            TipoProducto tipoProducto = em.find(TipoProducto.class, idTipoProducto);

            if (tipoProducto == null) {
                throw new EntityNotFoundException("No se encontro el registro de tipoProdcuto con el id " + idTipoProducto);
            }
            if (producto == null) {
                throw new EntityNotFoundException("no se encontro el registro de producto con el id " + idProducto);
            }
            ProductoDetalle registro = em.createNamedQuery("ProductoDetalle.findByIdTipoProductoAndIdProducto", ProductoDetalle.class)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .setParameter("idProducto", idProducto)
                    .getSingleResult();
            if (registro == null) {
                throw new EntityNotFoundException("No se encontro el registro");
            }
            return registro;
        } catch (PersistenceException ex) {
            throw new PersistenceException("error con la base de datos", ex);
        }
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


    @Override
    public List<ProductoDetalle> findRange(Integer first, Integer max) {
        if (first >= 0 && max > 0) {
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
