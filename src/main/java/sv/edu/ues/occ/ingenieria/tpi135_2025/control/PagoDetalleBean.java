/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.PagoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mjlopez bean para el control de la entidadPagoDetalle
 */
@LocalBean
@Stateless
public class PagoDetalleBean extends AbstractDataAccess<PagoDetalle> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public PagoDetalleBean() {
        super(PagoDetalleBean.class);
    }

    public PagoDetalleBean(Class t) {
        super(t);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idPagoDetalle";
    }

    public List<PagoDetalle> findByIdPago(Long idPago,int first ,int max) {
        try {
            return em.createNamedQuery("PagoDetalle.findByIdPago", PagoDetalle.class)
                    .setParameter("idPago", idPago)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        }catch (Exception e) {
            Logger.getLogger(PagoDetalleBean.class.getName()).log(Level.SEVERE, null, e);
        }
        return  List.of();
    }

    public Long countByIdPago(Long idPago) {
        try {
            return em.createNamedQuery("PagoDetalle.findByIdPago",Long.class)
                    .getSingleResult();
        } catch (Exception e) {
        Logger.getLogger(PagoDetalleBean.class.getName()).log(Level.SEVERE, null, e);
        }

        return 0l;
    }



}
