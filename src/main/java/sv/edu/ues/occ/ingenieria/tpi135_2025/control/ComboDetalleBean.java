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

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Combo;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ComboDetallePK;

/**
 *
 * @author mjlopez clase bean para control de la entidad ComboDetalle
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


    public void deleteByComboDetallePK(ComboDetallePK id) {
        try {
            em.createNamedQuery("ComboDetalle.deleteByComboDetallePK").
                    setParameter("idCombo", id.getIdCombo()).
                    setParameter("idProducto", id.getIdProducto())
                    .executeUpdate();
        } catch (Exception e) {
            Logger.getLogger(ComboDetalleBean.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<ComboDetalle> findByIdProducto(Long idProducto, int first, int max) {

        try {
            return em.createNamedQuery("ComboDetalle.findByIdProducto", ComboDetalle.class)
                    .setParameter("idProducto",idProducto)
                    .setFirstResult(first).setMaxResults(max).getResultList();
        }catch (Exception e) {
            Logger.getLogger(ComboDetalleBean.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return List.of();
    }
    public Long countByIdProducto(Long idProducto) {

        try {
            return em.createNamedQuery("ComboDetalle.countByIdProducto", Long.class)
                    .setParameter("idProducto",idProducto).getSingleResult();
        }catch (Exception e) {
            Logger.getLogger(ComboDetalleBean.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return 0L;
    }
    public List<ComboDetalle> findByIdCombo(Long idCombo,int first,int max) {

        try {
            return em.createNamedQuery("ComboDetalle.countByIdCombo", ComboDetalle.class)
                    .setParameter("idCombo",idCombo)
                    .setFirstResult(first).setMaxResults(max).getResultList();
        }catch (Exception e) {
            Logger.getLogger(ComboDetalleBean.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return List.of();
    }
    public Long countByIdCombo(Long idCombo) {

        try {
            return em.createNamedQuery("ComboDetalle.countByIdCombo", Long.class)
                    .setParameter("idCombo",idCombo).getSingleResult();
        }catch (Exception e) {
            Logger.getLogger(ComboDetalleBean.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return 0L;
    }
    public List<ComboDetalle> findByIdComboAndIdProducto(Long idCombo,Long idProducto,int first,int max) {

        try {
            return em.createNamedQuery("ComboDetalle.countByIdComboAndIdProducto", ComboDetalle.class)
                    .setParameter("idCombo",idCombo)
                    .setFirstResult(first).setMaxResults(max).getResultList();
        }catch (Exception e) {
            Logger.getLogger(ComboDetalleBean.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return List.of();
    }
    public Long countByIdComboAndIdProducto(Long idCombo,Long idProducto) {

        try {
            return em.createNamedQuery("ComboDetalle.countByIdComboAndIdProducto", Long.class)
                    .setParameter("idCombo",idCombo).getSingleResult();
        }catch (Exception e) {
            Logger.getLogger(ComboDetalleBean.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }

        return 0L;
    }

}
