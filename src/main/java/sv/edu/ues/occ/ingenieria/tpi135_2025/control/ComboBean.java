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

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;

/**
 * @author hdz bean para control de entidad Combo
 */
@LocalBean
@Stateless
public class ComboBean extends AbstractDataAccess<Combo> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    /**
     * Constructor por defecto. Establece la clase de la entidad administrada a
     * Combo
     */
    public ComboBean() {
        super(Combo.class);
    }

    /**
     * Establece el EntityManager manualmente. Útil para pruebas o entornos
     * donde no se inyecta automáticamente.
     *
     * @param em EntityManager a utilizar.
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Obtiene el {EntityManager} actual.
     *
     * @return el EntityManager inyectado.
     * @throws IllegalStateException si el EntityManager no ha sido
     * inicializado.
     */
    @Override
    public EntityManager getEntityManager() {
        if (em == null) {
            throw new IllegalStateException("EntityManager no ha sido inicializado correctamente.");
        }
        return em;
    }

    /**
     * Define el nombre del parámetro por el cual se ordenan las consultas por
     * defecto.
     *
     * @return nombre del campo utilizado para ordenamiento (idCombo).
     */
    @Override
    public String orderParameterQuery() {
        return "idCombo";
    }

    /**
     * Actualiza un registro existente de la entidad {Combo}.
     *
     * Verifica que el EntityManager esté disponible, que el objeto no sea nulo,
     * y que el ID proporcionado sea válido. Si el registro existe, se actualiza
     * su nombre.
     *
     * @param combo objeto con los nuevos datos a actualizar.
     * @param id identificador del registro a modificar.
     * @return la entidad {@link Combo} actualizada.
     * @throws IllegalArgumentException si los parámetros son inválidos.
     * @throws EntityNotFoundException si no se encuentra el registro con el ID
     * especificado.
     * @throws ConstraintViolationException si existen violaciones a
     * restricciones de la entidad.
     * @throws PersistenceException si ocurre un error con la base de datos.
     */
    @Override
    public Combo update(Combo combo, Object id) {
        if (getEntityManager() == null) {
            throw new IllegalStateException("EntityManager no ha sido inicializado correctamente.");
        }
        if (combo == null) {
            throw new IllegalArgumentException("Combo no puede ser null.");
        }
        if (id == null || Long.parseLong(id.toString()) <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        try {
            Combo registroExistente = getEntityManager().find(Combo.class, id);
            if (registroExistente == null) {
                throw new EntityNotFoundException("Registro no encontrado.");
            }
            registroExistente.setNombre(combo.getNombre());
            return em.merge(registroExistente);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (ConstraintViolationException e) {
            throw new ConstraintViolationException(e.getConstraintViolations());
        } catch (PersistenceException e) {
            throw new PersistenceException("errror con la base d edatos: " + e.getMessage());
        }
    }

}
