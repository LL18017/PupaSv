/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import com.fasterxml.jackson.databind.ser.std.NonTypedScalarSerializerBase;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;

/**
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

    public List<Pago> findByIdOrden(Long idOrden, Integer first, Integer max) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("idPago no puede ser negativo o nulo");
        }
        if (first == null || first < 0) {
            throw new IllegalArgumentException("first no puede ser negativo o nulo");
        }
        if (max == null || max <= 0) {
            throw new IllegalArgumentException("first no puede ser negativo o nulo");
        }
        try {
            List<Pago> resultados= em.createNamedQuery("Pago.findByIdOrden", Pago.class).
                    setParameter("idOrden", idOrden).setFirstResult(first).setMaxResults(max).getResultList();
            if (resultados.isEmpty()){
                throw new NoResultException("No se encontro resultado con el id " + idOrden);
            }
            return resultados;
        } catch (NoResultException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("error con la base de datos" + e.getMessage());
        }

    }

    public Long countByIdOrden(Long idOrden) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("idPago no puede ser negativo o nulo");
        }
        try {
            return em.createNamedQuery("Pago.countByIdOrden", Long.class).
                    setParameter("idOrden", idOrden).getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("No se pudo encontrar pagos relacionados a esta orden" + e.getMessage());
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("el dato devuelto no es unico" + e.getMessage());
        } catch (PersistenceException e) {
            throw new PersistenceException("error con la base de datos" + e.getMessage());
        }


    }

}
