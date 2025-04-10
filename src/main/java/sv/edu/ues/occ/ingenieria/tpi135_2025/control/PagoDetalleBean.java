/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Pago;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.PagoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mjlopez bean para el control de la entidadPagoDetalle
 */
@LocalBean
@Stateless
public class PagoDetalleBean extends AbstractDataAccess<PagoDetalle> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public PagoDetalleBean() {
        super(PagoDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idPagoDetalle";
    }

    public List<PagoDetalle> findRangeByIdPago(Long idPago, Integer first, Integer max) {
        if (idPago == null || idPago <= 0) {
            throw new IllegalArgumentException("idPago no puede ser nulo o menor a cero");
        }
        if (!verificarnull(first, max)) {
            throw new IllegalArgumentException("first y max no pueden ser nulos");
        }
        if (!verificarMayor(first, max)) {
            throw new IllegalArgumentException("first y max no pueden ser menores a cero");
        }
        try {
            Pago existe = em.find(Pago.class, idPago);
            if (existe == null) {
                throw new EntityNotFoundException("Pago no encontrado con id: " + idPago);
            }
            return em.createNamedQuery("PagoDetalle.findByIdPago", PagoDetalle.class)
                    .setParameter("idPago", idPago)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Pago no encontrado con id: " + idPago);
        }catch (PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    public Long countByIdPago(Long idPago) {
        if (idPago == null || idPago <= 0) {
            throw new IllegalArgumentException("idPago no puede ser nulo o menor a cero");
        }
        try {
            Pago existe = em.find(Pago.class, idPago);
            if (existe == null) {
                throw new EntityNotFoundException("Pago no encontrado con id: " + idPago);
            }
            return em.createNamedQuery("PagoDetalle.countByIdPago", Long.class)
                    .setParameter("idPago", idPago)
                    .getSingleResult();
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("no existe pago con el id: " + idPago);
        }
        catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("El valor devuelto no es un resultado único");
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }


}
