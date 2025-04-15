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
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;

/**
 *
 * @author mjlopez bean para control de entidad Orden
 */
@LocalBean
@Stateless
public class ComboBean extends AbstractDataAccess<Combo> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ComboBean() {
        super(Combo.class);
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        if (em == null) {
            throw new IllegalStateException("EntityManager no ha sido inicializado correctamente.");
        }
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idCombo";
    }

    @Override
    public Combo update(Combo combo, Object id) {
        if (getEntityManager() == null) {
            throw new RuntimeException("EntityManager no ha sido inicializado correctamente.");
        }
        if (combo == null) {
            throw new IllegalArgumentException("Combo no puede ser null.");
        }
        if (id == null || Long.parseLong(id.toString()) <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Combo registroExistente = getEntityManager().find(Combo.class, id);
        if (registroExistente == null) {
            throw new EntityNotFoundException("Registro no encontrado.");
        }

        registroExistente.setNombre(combo.getNombre());
        return getEntityManager().merge(registroExistente);
    }

    public void delete(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser null.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que 0.");
        }

        Combo registroExistente = getEntityManager().find(Combo.class, id);
        if (registroExistente == null) {
            throw new EntityNotFoundException("Registro con ID " + id + " no encontrado en la base de datos.");
        }

        getEntityManager().remove(
                getEntityManager().contains(registroExistente)
                ? registroExistente
                : getEntityManager().merge(registroExistente)
        );

    }
}