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

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;

/**
 *
 * @author mjlopez bean para control de la entidad Pago
 */
@LocalBean
@Stateless
public class PagoBean extends AbstractDataAccess<Pago> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public PagoBean() {
        super(Pago.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idPago";
    }

    public List<Pago> findByIdOrden(Long idPago,int first, int max) {

        try {
            return em.createNamedQuery("Pago.findByIdOrden", Pago.class).
                    setParameter("idOrden", idPago).setFirstResult(first).setMaxResults(max).getResultList();
        }catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return List.of();
    }
    public Long countByIdOrden(Long idOrden) {

        try {
            return em.createNamedQuery("Pago.countByIdOrden", Long.class).
                    setParameter("idOrden", idOrden).getSingleResult();
        }catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return 0L;
    }

}
