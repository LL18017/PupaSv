/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.core.StreamingOutput;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

/**
 * @author mjlopez clase bean para control de la entidad OrdenDetalle
 */
@LocalBean
@Stateless
public class OrdenDetalleBean extends AbstractDataAccess<OrdenDetalle> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public OrdenDetalleBean() {
        super(OrdenDetalle.class);
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idOrdenDetalle";
    }


    public OrdenDetalle findByIdOrdenAndIdPrecioProducto(Long idOrden, Long idProductoPrecio) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("idOrden no puede ser negativo o nulo");
        }
        if (idProductoPrecio == null || idProductoPrecio <= 0) {
            throw new IllegalArgumentException("idProductoPrecio no puede ser negativo o nulo");
        }
        try {
            return em.createNamedQuery("OrdenDetalle.findByPrecioProductoAndIdOrden", OrdenDetalle.class)
                    .setParameter("idOrden", idOrden)
                    .setParameter("idProductoPrecio", idProductoPrecio)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("No se encontro resultado con el id " + idOrden);
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al obtener el resultado", e);
        }
    }

    /**
     * Método modificado para realizar la búsqueda con paginación.
     * Este método obtiene un rango de `OrdenDetalle` para una orden específica, utilizando
     * la paginación con los parámetros `first` y `max`.
     * Impacto en otros métodos:
     * Este método se basa en la consulta OrdenDetalle.findByIdOrden, que ya debería estar definida en las Named Queries
     * en tu entidad `OrdenDetalle`. La paginación se maneja mediante setFirstResult(first) y`setMaxResults(max).
     * No debería afectar otros métodos a menos que también utilicen esta misma consulta, pero en general no hay dependencia directa.
     *
     * @param idOrden Identificador de la orden para filtrar los detalles.
     * @param first   El índice del primer resultado a devolver.
     * @param max     La cantidad máxima de resultados a devolver.
     * @return Una lista de objetos `OrdenDetalle` dentro del rango especificado.
     */
    public List<OrdenDetalle> findRangeByIdOrden(Long idOrden, Integer first, Integer max) {
        if (idOrden == null) {
            throw new IllegalArgumentException("idOrden no puede ser nulo");
        }
        if (first < 0 || first == null) {
            throw new IllegalArgumentException("first no puede ser negativo o nulo");
        }
        if (max <= 0 || max == null) {
            throw new IllegalArgumentException("first no puede ser negativo o nulo");
        }
        try {
            return em.createNamedQuery("Orden.findByIdOrden", OrdenDetalle.class)
                    .setParameter("idOrden", idOrden)
                    .setFirstResult(first).setMaxResults(max).
                    getResultList();
        } catch (NoResultException e) {
            throw new NoResultException("No se encontro resultado con el id " + idOrden);
        } catch (PersistenceException e) {
            throw new PersistenceException("Error en la base de datos", e);
        }
    }

    public Long countByIdOrden(Long idOrden) {
        try {
            return em.createNamedQuery("OrdenDetalle.countByIdOrden", Long.class)
                    .setParameter("idOrden", idOrden)
                    .getSingleResult();
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("no se ha devuelto un resultado unico");
        } catch (NoResultException e) {
            throw new NoResultException("No se encontro resultado con el id " + idOrden);
        } catch (PersistenceException e) {
            throw new PersistenceException("Error en la base de datos", e);
        }
    }

    /**
     * Genera un objeto OrdenDetalle a partir de un IdOrden, un Producto y una cantidad.
     * Busca el precio del producto por su idProducto.
     * No calcula subtotal (eso se realiza en otra clase/proceso).
     *
     * @param idOrden          Objeto Orden asociado (debe tener IdOrden).
     * @param idProducto       Objeto Producto a agregar (debe tener IdProducto).
     * @param cantidadProducto Cantidad de producto (si es nula o menor a 1, se asume 1 por defecto).
     * @return Objeto OrdenDetalle generado o lanza excepción si los datos son inválidos.
     */
    public void generarOrdenDetalleProducto(Long idOrden, Long idProducto, Integer cantidadProducto) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("La orden es invalida o no tiene ID");
        }

        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("La producto es invalida o no tiene ID");
        }
        if (cantidadProducto == null || cantidadProducto < 1) {
            cantidadProducto = 1;
        }
        try {
            Object[] detalle = em.createNamedQuery("ComboDetalle.findProductoPrecioAndCantidadByIdProducto", Object[].class)
                    .setParameter("idProducto", idProducto)
                    .getSingleResult();
            if (detalle == null) {
                throw new NoResultException("No se ha encontrado detalle para este producto : " + idProducto);
            }
            ProductoPrecio precio = (ProductoPrecio) detalle[0];
            OrdenDetalle ordenDetalle = new OrdenDetalle();
            ordenDetalle.setPrecio(precio.getPrecioSugerido());
            ordenDetalle.setCantidad(cantidadProducto);
            ordenDetalle.setOrdenDetallePK(new OrdenDetallePK(idOrden, precio.getIdProductoPrecio()));
            em.persist(ordenDetalle);

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("no se ha encontrado un precio con este producto : " + idProducto);
        } catch (NoResultException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error en la base de datos", e);
        } catch (NullPointerException e) {
            throw new NullPointerException("los argumentos desde bd son invalidos" + e.getMessage());
        }

    }


    /**
     * Genera una lista de objetos OrdenDetalle a partir de un Combo y una cantidad de combo especificada.
     * Cada OrdenDetalle representa un producto dentro del combo, multiplicado por la cantidad del combo.
     * No calcula subtotal.
     *
     * @param idOrden       Objeto Orden asociado.
     * @param idCombo       Objeto Combo a procesar.
     * @param cantidadCombo Cantidad de combos a aplicar (por defecto 1 si es nulo o menor a 1).
     */
    public void generarOrdenDetalleDesdeCombo(Long idOrden, Long idCombo, Integer cantidadCombo) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("La orden es invalida o no tiene ID");
        }
        if (idCombo == null || idCombo <= 0) {
            throw new IllegalArgumentException("El combo es invalido o no tiene ID"); // Corregido "La" a "El"
        }
        if (cantidadCombo == null || cantidadCombo < 1) {
            cantidadCombo = 1;
        }


        try {
            Orden orden =  em.find(Orden.class,idOrden);
            if (orden == null) {
                throw new EntityNotFoundException("no se ha encontrado la orden :"+idOrden);
            }
            List<Object[]> detalle = em.createNamedQuery("ComboDetalle.findProductoPrecioAndCantidadByIdCombo", Object[].class)
                    .setParameter("idCombo", idCombo)
                    .getResultList();
            if (detalle == null || detalle.isEmpty()) {
                throw new NoResultException("No se ha encontrado detalle para este combo : " + idCombo);
            }
            List<OrdenDetalle> ordenDetalles = new ArrayList<>();
            for (Object[] d : detalle) {

                ProductoPrecio precio = (ProductoPrecio) d[0];
                Integer cantidad = (Integer) d[1] * cantidadCombo; // o Long según tu modelo
                OrdenDetallePK pk = new OrdenDetallePK();
                pk.setIdOrden(idOrden);
                pk.setIdProductoPrecio(precio.getIdProductoPrecio());
                OrdenDetalle ordenDetalle = new OrdenDetalle();
                ordenDetalle.setOrdenDetallePK(pk);
                ordenDetalle.setPrecio(precio.getPrecioSugerido());
                ordenDetalle.setCantidad(cantidad);
                ordenDetalles.add(ordenDetalle);
            }

            for (OrdenDetalle ordenDetalle : ordenDetalles) {
                em.persist(ordenDetalle);
            }
        } catch (EntityNotFoundException | NoResultException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error en la base de datos", e);
        } catch (NullPointerException e) {
            throw new NullPointerException("los argumentos desde bd son invalidos" + e.getMessage());
        }

    }


    /**
     * Genera una lista de objetos OrdenDetalle combinando productos individuales y productos dentro de combos.
     * Si se proporcionan ambos tipos de entrada, se generan OrdenDetalle para ambos.
     * No calcula subtotal, solo genera los detalles de la orden.
     *
     * @param idOrden       Objeto Orden asociado.
     * @param productosList Lista de productos individuales a agregar a la orden.
     * @param comboList     Lista de combos a agregar a la orden.
     * @return Lista de objetos OrdenDetalle generados combinando productos individuales y productos de los combos.
     */
    public void generarOrdenDetalleMixto(Long idOrden, List<Object[]> productosList, List<Object[]> comboList) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("La orden es inválida o no tiene ID.");
        }

        if ((productosList == null || productosList.isEmpty()) && (comboList == null || comboList.isEmpty())) {
            throw new IllegalArgumentException("ambas listas estan vacias o nulas , debe de haber almenos una lista con objetos");
        }

        Map<Producto, Object[]> productoMap = new HashMap<>();

        try {
            // Productos individuales
            if (productosList != null && !productosList.isEmpty()) {
                for (Object[] producto : productosList) {
                    Long idProducto = (Long) producto[0];
                    Integer cantidad = (Integer) producto[1];
                    if (cantidad < 1 || cantidad == null) {
                        cantidad = 1;
                    }
                    Object[] resultado = em.createNamedQuery("ProductoPrecio.findProductoProductoProductoByIdProducto", Object[].class)
                            .setParameter("idProducto", idProducto)
                            .getSingleResult();

                    ProductoPrecio precio = (ProductoPrecio) resultado[0];
                    Producto prod = (Producto) resultado[1];
                    if (productoMap.containsKey(prod)) {
                        Object[] existente = productoMap.get(prod);
                        Integer acumulada = (Integer) existente[2];
                        existente[2] = acumulada + cantidad;
                    } else {

                        productoMap.put(prod, new Object[]{precio, prod, cantidad});
                    }
                }
            }

            // Productos desde combos
            if (comboList != null && !comboList.isEmpty()) {
                for (Object[] combo : comboList) {
                    Long idCombo = (Long) combo[0];
                    Integer cantidadDeCombo = (Integer) combo[1];

                    List<Object[]> resultados = em.createNamedQuery(
                                    "ComboDetalle.findProductoPrecioProductoAndCantidadByIdCombo", Object[].class)
                            .setParameter("idCombo", idCombo)
                            .getResultList();

                    for (Object[] r : resultados) {
                        ProductoPrecio precio = (ProductoPrecio) r[0];
                        Producto prod = (Producto) r[1];
                        Integer cantidad = (Integer) r[2] * cantidadDeCombo;

                        if (productoMap.containsKey(prod)) {
                            Object[] existente = productoMap.get(prod);
                            Integer acumulada = (Integer) existente[2];
                            existente[2] = acumulada + cantidad;
                        } else {
                            productoMap.put(prod, new Object[]{precio, prod, cantidad});
                        }
                    }
                }
            }

            // Convertir el Map a lista de OrdenDetalle
            List<OrdenDetalle> detallesGenerados = new ArrayList<>();
            for (Object[] detalle : productoMap.values()) {
                ProductoPrecio precio = (ProductoPrecio) detalle[0];
                Producto prod = (Producto) detalle[1];
                Integer cantidad = (Integer) detalle[2];

                OrdenDetalle od = new OrdenDetalle();
                OrdenDetallePK pk = new OrdenDetallePK();
                pk.setIdOrden(idOrden);
                pk.setIdProductoPrecio(prod.getIdProducto());
                od.setOrdenDetallePK(pk);
                od.setCantidad(cantidad);
                od.setPrecio(precio.getPrecioSugerido());

                detallesGenerados.add(od);
            }
            for (OrdenDetalle ordenDetalle : detallesGenerados) {
                em.persist(ordenDetalle);
            }

        } catch (NoResultException e) {
            throw new NoResultException("no se encontrar detalles: " + e.getMessage());
        } catch (PersistenceException e) {
            throw new PersistenceException("error con la base de datos: " + e.getMessage());
        }
    }

    public void delete(Long idOrden, Long idProductoPrecio) {
        try {
            Orden orden = em.find(Orden.class, idOrden);
            if (orden == null) {
                throw new EntityNotFoundException("El orden no existe");
            }
            ProductoPrecio precio = em.find(ProductoPrecio.class, idProductoPrecio);
            if (precio == null) {
                throw new EntityNotFoundException("El producto precio no existe");
            }
            OrdenDetallePK pk = new OrdenDetallePK(idOrden, idProductoPrecio);
            em.createNamedQuery("OrdenDetalle.deleteOrdenDetalleByIdOrdenAndProductoPrecio")
                    .setParameter("idOrden", idOrden)
                    .setParameter("idProductoPrecio", idProductoPrecio)
                    .executeUpdate();
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("error con la base de datos: " + e.getMessage());
        }
    }

    public OrdenDetalle update(OrdenDetalle registro, Long idOrden, Long idProductoPrecio) throws IllegalStateException, IllegalArgumentException {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo");
        }
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("El idOrden no puede ser nulo o menor a cero");
        }
        if (idProductoPrecio == null || idProductoPrecio <= 0) {
            throw new IllegalArgumentException("idProductoPrecio no puede ser nulo o menor a cero");
        }
        try {
            Orden orden = em.find(Orden.class, idOrden);
            if (orden == null) {
                throw new EntityNotFoundException("El orden no existe");
            }
            ProductoPrecio precio = em.find(ProductoPrecio.class, idProductoPrecio);
            if (precio == null) {
                throw new EntityNotFoundException("El producto precio no existe");
            }
            OrdenDetallePK pk = new OrdenDetallePK(idOrden, idProductoPrecio);
            registro.setOrdenDetallePK(pk);
            return em.merge(registro);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("error con la base de datos", e);
        }
    }
}
