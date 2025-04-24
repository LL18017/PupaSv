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
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
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

    /**
     * Constructor que inicializa la clase padre con la entidad
     */
    public ComboDetalleBean() {
        super(ComboDetalle.class);
    }

    /**
     * Devuelve el EntityManager asociado a este bean.
     *
     * @return instancia de EntityManager
     */
    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Define el parámetro por el cual se ordenarán los resultados en las
     * consultas.
     *
     * @return el nombre del campo utilizado para ordenamiento
     */
    @Override
    public String orderParameterQuery() {
        return "cantidad";
    }

    /**
     * Permite establecer manualmente el EntityManager (útil para pruebas
     * unitarias o configuración manual).
     *
     * @param em instancia de {@link EntityManager}
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Crea un nuevo registro de {@code ComboDetalle} relacionando un Combo y un
     * Producto.
     *
     * @param detalle objeto {@link ComboDetalle} a persistir
     * @param idCombo identificador del combo
     * @param idProducto identificador del producto
     * @throws IllegalArgumentException si los parámetros son nulos o inválidos
     * @throws EntityNotFoundException si el combo o el producto no existen
     * @throws PersistenceException en caso de error de base de datos
     * @throws IllegalStateException si ocurre un error inesperado al persistir
     */
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
                throw new EntityNotFoundException("No se encontro resultado para  Combo con id " + idCombo);
            }
            Producto producto = em.find(Producto.class, idProducto);
            if (producto == null) {
                throw new EntityNotFoundException("No se encontro resultado para producto con id " + idProducto);
            }
            // Asignar la clave primaria embebida
            ComboDetallePK pk = new ComboDetallePK(idCombo, idProducto);
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

    /**
     * Obtiene un objeto {@link ComboDetalle} por su clave compuesta.
     *
     * @param idCombo identificador del combo
     * @param idProducto identificador del producto
     * @return objeto {@link ComboDetalle} encontrado
     * @throws EntityNotFoundException si no se encuentra el detalle
     * @throws IllegalArgumentException si los parámetros son nulos, cero o
     * negativos
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
            throw new EntityNotFoundException("No se encontro resultado para ComboDetalle con idCombo=" + idCombo + " e idProducto=" + idProducto);
        }
    }

    /**
     * Elimina un {@link ComboDetalle} mediante su clave compuesta.
     *
     * @param idCombo identificador del combo
     * @param idProducto identificador del producto
     * @throws IllegalArgumentException si los parámetros son nulos, cero o
     * negativos
     * @throws EntityNotFoundException si el combo o producto no existen
     * @throws PersistenceException si ocurre un error al eliminar el registro
     * en la base de datos
     * @throws IllegalStateException si ocurre un error inesperado al persistir
     */
    public void deleteByComboDetallePK(Long idCombo, Long idProducto) {
        if (idCombo == null || idCombo <= 0) {
            throw new IllegalArgumentException("idCombo no puede ser nulo, cero o negativo");
        }
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("idProducto no puede ser nulo, cero o negativo");
        }
        try {
            // Validación previa manual como en ProductoDetalleBean
            Combo combo = em.find(Combo.class, idCombo);
            if (combo == null) {
                throw new EntityNotFoundException("No se encontro resultado para el Combo con id=" + idCombo);
            }

            Producto producto = em.find(Producto.class, idProducto);
            if (producto == null) {
                throw new EntityNotFoundException("No se encontro resultado  para el Producto con id=" + idProducto);
            }
            em.createNamedQuery("ComboDetalle.deleteByComboDetallePK")
                    .setParameter("idCombo", idCombo)
                    .setParameter("idProducto", idProducto)
                    .executeUpdate();
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al eliminar ComboDetalle con idCombo " + idCombo + " e idProducto " + idProducto, e);
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("Error al persistir ComboDetalle", ex);
        }
    }

    /**
     * Actualiza los campos del objeto {@code ComboDetalle} correspondiente a la
     * clave compuesta.
     *
     * @param detalleActualizado objeto {@link ComboDetalle} con los nuevos
     * valores
     * @param idCombo identificador del combo
     * @param idProducto identificador del producto
     * @return entidad actualizada
     * @throws IllegalArgumentException si el objeto es nulo
     * @throws PersistenceException si ocurre un error al actualizar
     */
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

    /**
     * Devuelve una lista paginada de {@code ComboDetalle} para un combo
     * específico.
     *
     * @param idCombo identificador del combo
     * @param first índice inicial para paginación
     * @param max cantidad máxima de resultados
     * @return lista de objetos {@link ComboDetalle}
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws PersistenceException si ocurre un error en la consulta
     */
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
}
