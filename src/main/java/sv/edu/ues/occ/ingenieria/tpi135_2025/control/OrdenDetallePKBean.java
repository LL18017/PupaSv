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
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.OrdenDetallePK;

/**
 *
 * @author mjlopez 
 * bean para control de llave primaria compuesta de orden y
 * productoPrecio
 */
@LocalBean
@Stateless
public class OrdenDetallePKBean extends AbstractDataAccess<OrdenDetallePK> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public OrdenDetallePKBean() {
        super(OrdenDetallePK.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idOrdenDetallePk";
    }

}
