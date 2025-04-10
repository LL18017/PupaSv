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
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Orden;

/**
 *
 * @author mjlopez
 * bean para control de entidad Orden
 */
@LocalBean
@Stateless
public class OrdenBean extends AbstractDataAccess<Orden> implements Serializable{
    @PersistenceContext(unitName = "PupaSV-PU")
    public EntityManager em;

    public OrdenBean() {
        super(Orden.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idOrden";
    }
    
}
