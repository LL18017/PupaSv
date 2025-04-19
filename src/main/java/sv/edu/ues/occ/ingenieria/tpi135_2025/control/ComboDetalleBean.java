/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetallePK;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

/**
 * 
 * @author hdz bean para control de entidad ComboDetalle
 */
@LocalBean
@Stateless
public class ComboDetalleBean extends AbstractDataAccess<ComboDetalle> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ComboDetalleBean() {
        super(ComboDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idComboDetalle";
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
/**
 * 
 * @param idCombo
 * @param idProducto
 * @return 
 * @throws 
 * @throws 
 * @throws 
 * 
 */
    public ComboDetalle findByIdComboAndIdProducto(Long idCombo, Long idProducto) {
        if (idCombo == null || idCombo <= 0) {
            throw new IllegalArgumentException("idCombo no puede ser nulo, cero o negativo");
        }
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("idProducto no puede ser nulo, cero o negativo");
        }
        try {
            return em.createNamedQuery("ComboDetalle.findByIdComboAndIdProducto", ComboDetalle.class)
                    .setParameter("idCombo", idCombo)
                    .setParameter("idProducto", idProducto)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("No se encontró ComboDetalle con idCombo=" + idCombo + " e idProducto=" + idProducto);
        }
    }

    public void deleteByComboDetallePK(Long idCombo, Long idProducto) {
        if (idCombo == null || idCombo <= 0) {
            throw new IllegalArgumentException("idCombo no puede ser nulo, cero o negativo");
        }
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("idProducto no puede ser nulo, cero o negativo");
        }
        try {
            em.createNamedQuery("ComboDetalle.deleteByComboDetallePK")
                    .setParameter("idCombo", idCombo)
                    .setParameter("idProducto", idProducto)
                    .executeUpdate();
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al eliminar ComboDetalle con idCombo " + idCombo + " e idProducto " + idProducto, e);
        }
    }

    public ComboDetalle updateByComboDetallePK(ComboDetalle detalleActualizado, Long idCombo, Long idProducto) {
        // Validamos que el detalle actualizado no sea null
        if (detalleActualizado == null) {
            throw new IllegalArgumentException("El objeto ComboDetalle no puede ser nulo");
        }
        ComboDetalle existente = findByIdComboAndIdProducto(idCombo, idProducto);
        
        try {
            // Reemplaza solo los campos que deseas actualizar
            existente.setCantidad(detalleActualizado.getCantidad());
            existente.setActivo(detalleActualizado.getActivo());
            return em.merge(existente);
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al actualizar ComboDetalle", e);
        }
    }

    public List<ComboDetalle> findRangeByCombo(Long idCombo, int first, int max) {
        if (idCombo == null || first < 0 || max <= 0) {
            throw new IllegalArgumentException("Parámetros no válidos para paginación.");
        }
        try {
            return em.createNamedQuery("ComboDetalle.findByIdCombo", ComboDetalle.class)
                    .setParameter("idCombo", idCombo)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al obtener el rango de ComboDetalle", e);
        }
    }

    public void create(ComboDetalle detalle, Long idCombo, Long idProducto) throws IllegalStateException, IllegalArgumentException {
        try {
            if (detalle == null) {
                throw new IllegalArgumentException("El detalle no puede ser nulo.");
            }
            if (idCombo == null || idCombo <= 0) {
                throw new IllegalArgumentException("idCombo no puede ser nulo, menor o igual a cero.");
            }
            if (idProducto == null || idProducto <= 0) {
                throw new IllegalArgumentException("idProducto no puede ser nulo, menor o igual a cero.");
            }
            // Validar existencia de Combo y Producto
            Combo combo = em.find(Combo.class, idCombo);
            if (combo == null) {
                throw new EntityNotFoundException("No se encontró Combo con id " + idCombo);
            }
            Producto producto = em.find(Producto.class, idProducto);
            if (producto == null) {
                throw new EntityNotFoundException("No se encontró Producto con id " + idProducto);
            }
            // Asignar la clave primaria embebida
            ComboDetallePK pk = new ComboDetallePK();
            pk.setIdCombo(idCombo);
            pk.setIdProducto(idProducto);
            detalle.setComboDetallePK(pk);
            // Persistir usando el método del padre
            super.create(detalle);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error con la base de datos", e);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Error al persistir el registro");
        }
    }
}
