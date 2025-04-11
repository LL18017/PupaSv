/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        } catch (PersistenceException e) {
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
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("El valor devuelto no es un resultado único");
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }


    public void createDetalles(Long idOrden) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("idPago no puede ser nulo o menor a cero");
        }
        try {
            Orden orden = em.find(Orden.class, idOrden);
            if (orden == null) {
                throw new EntityNotFoundException("Orden no encontrado con id: " + idOrden);
            }
            List<Pago> pagos = em.createNamedQuery("Pago.findByIdOrden", Pago.class).setParameter("idOrden", idOrden).getResultList();

            pagos.forEach(pago -> {
                PagoDetalle detalle = new PagoDetalle();
                BigDecimal total = calculoPorOrden(idOrden);
                detalle.setIdPago(pago);
                detalle.setMonto(total);
                em.persist(detalle);
            });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("El valor devuelto no es un resultado");
        } catch (NoResultException e) {
            throw new NoResultException("El valor devuelto no es un resultado");
        } catch (PersistenceException e) {
            throw new PersistenceException(e);
        }

    }

    public BigDecimal calculoPorOrden(Long idOrden) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("idOrden no puede ser nulo o menor a cero");
        }
        try {
            Orden orden = em.find(Orden.class, idOrden);
            if (orden == null) {
                throw new EntityNotFoundException("Orden no encontrado con id: " + idOrden);
            }
            List<OrdenDetalle> detalles = em.createNamedQuery("OrdenDetalle.findByIdOrden", OrdenDetalle.class).setParameter("idOrden", idOrden).getResultList();
            return detalles.stream()
                    .map(d -> d.getPrecio().multiply(BigDecimal.valueOf(d.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("El valor devuelto no es un resultado");
        } catch (NoResultException e) {
            throw new NoResultException("El valor devuelto no es un resultado");
        } catch (PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    public BigDecimal calculoProductos(Map<Producto, Integer> productos) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Producto, Integer> entry : productos.entrySet()) {
            Producto producto = entry.getKey();
            Integer cantidad = entry.getValue();
            total = total.add(calculoProducto(producto, cantidad));  // Usa add para acumular
        }
        return total;  // Retorna BigDecimal
    }

    public BigDecimal calculoProducto(Producto producto, Integer cantidad) {
        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("productos o su id no puede ser nulo o menor a cero");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("cantidad no puede ser menor a cero");
        }
        try {
            ProductoPrecio precio = em.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class).setParameter("idProducto", producto.getIdProducto())
                    .getSingleResult();
            return precio.getPrecioSugerido().multiply(BigDecimal.valueOf(cantidad));
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("El valor devuelto no es un resultado");
        } catch (NoResultException e) {
            throw new NoResultException("El valor devuelto no es un resultado");
        } catch (PersistenceException e) {
            throw new PersistenceException(e);
        }
    }


}
