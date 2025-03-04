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

/**
 *
 * @author mjlopez bean para el control de la entidadPagoDetalle
 */
@LocalBean
@Stateless
public class PagoDetalleBean extends AbstractDataAccess<PagoDetalleBean> implements Serializable {

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

}
