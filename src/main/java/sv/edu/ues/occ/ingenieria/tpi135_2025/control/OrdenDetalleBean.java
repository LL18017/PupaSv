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


    /**
     * Busca un objeto {Ordendetalle} según su id de una orden y id de un productoprecio
     *
     * @param idOrden el ID de la orden. No pueder ser Null ni menor o igual a cero
     * @param idProductoPrecio el ID del ProductoPrecio. No puede ser Null ni menor o igual a cero
     * @return  el objeto {OrdenDetalle} que coincida con los parámetros especificados.
     * @throws IllegalArgumentException si {idOrden} o {idProductoPrecio} son nulos o menores o iguales a cero.
     * @throws NoResultException si no se encuentra ninguna coincidencia en la base de datos.
     * @throws PersistenceException si ocurre un error en la consulta a la base de datos.
     */
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
     * Recupera una lista de ordenDetalle asociados a una orden específica
     *
     * @param idOrden el ID de la orden para la cual se desean obtener los detalles. No puede ser Null
     * @param first se asigna el valor inicial de los resultados a recuperar. Debe ser cero o mayor
     * @param max se asigna el valor maximo de resultados a retornar. Debe ser mayor para cero
     * @return una lista de objetos que pertenecen a la orden específicada
     * @throws IllegalArgumentException si {idOrden} es {null}, o si {first} es {null} o negativo,
     *                                   o si {max} es {null} o menor o igual a cero.
     * @throws NoResultException si no se encuentran resultados para la orden indicada.
     * @throws PersistenceException si ocurre un error en la ejecución de la consulta.
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

    /**
     * Cuenta la cantidad de registros asociados a una orden especificada.
     *
     * @param idOrden el ID de la orden por la cual se desea contar los detalles.
     * @return el número total de registros asociados al ID de la orden
     * @throws NonUniqueResultException si la consulta devuelve más de un resultado (lo cual no debería suceder en una consulta de conteo).
     * @throws NoResultException si no se encuentra ningún resultado para el ID de orden proporcionado.
     * @throws PersistenceException si ocurre un error en la ejecución de la consulta o al acceder a la base de datos.
     */
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
     * Genera un nuevo registro de {OrdenDetalle} para una orden y producto específicos utilizando la información de
     * precio obtenida desde la entidad{ComboDetalle}.
     *
     * @param idOrden el ID de la orden a la cual se asociará el detalle. No debe de ser null ni menor o0 igual a cero
     * @param idProducto EL ID del producto que se desea agregara la orden. No debe ser Null ni menor o igual a cero
     * @param cantidadProducto la cantidad del producto a agregar. Si es NUll o menor a 1, se asigna 1 por defecto.
     * @throws IllegalArgumentException si {idOrden} o {idProducto} son nulos o menores o iguales a cero.
     * @throws NoResultException si no se encuentra el precio correspondiente para el producto especificado.
     * @throws EntityNotFoundException si el producto no tiene un precio asociado válido.
     * @throws PersistenceException si ocurre un error durante la persistencia del {OrdenDetalle}.
     * @throws NullPointerException si alguno de los datos obtenidos de la base de datos es inválido.
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
     * Genera y preserban  múltiples registros de {OrdenDetalle} en base a un combo seleccionado.
     * Cada combo puede contener múltiples productos con cantidades y precios definidos.
     * @param idOrden el ID de la orden a la que se asociarán los detalles del combo
     * @param idCombo el ID del combo selecccionado que contiene los productos a agregar
     * @param cantidadCombo cantidad que desea agregar del combo a la orden. Si es null o menor a 1, se asignára 1 por defecto.
     * @throws IllegalArgumentException si {idOrden} o {idCombo} es nulo o menor o igual a cero.
     * @throws EntityNotFoundException si no se encuentra la orden con el ID proporcionado.
     * @throws NoResultException si no se encuentran productos asociados al combo especificado.
     * @throws PersistenceException si ocurre un error al persistir los datos en la base de datos.
     * @throws NullPointerException si ocurre un error inesperado con valores nulos al acceder a los resultados de la base de datos.
     */
    public void generarOrdenDetalleDesdeCombo(Long idOrden, Long idCombo, Integer cantidadCombo) {
        if (idOrden == null || idOrden <= 0) {
            throw new IllegalArgumentException("La orden es invalida o no tiene ID");
        }
        if (idCombo == null || idCombo <= 0) {
            throw new IllegalArgumentException("El combo es invalido o no tiene ID");
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
                Integer cantidad = (Integer) d[1] * cantidadCombo;
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
     * Genera y conserva los detalles de una orden a partir de una mezcla de Productos individuales y Combos seleccionados
     * Evita duplicados acumilando las catidades de los prodcutos repetidos.
     *
     * @param idOrden el ID de la orden la que se asociaran los detalles
     * @param productosList Lista de prodcutos individuales
     * @param comboList Lista de combos.
     * @throws IllegalArgumentException si {idOrden} es nulo o menor o igual a cero, o si ambas listas están vacías o nulas.
     * @throws NoResultException si no se encuentra información necesaria para productos o combos en la base de datos.
     * @throws PersistenceException si ocurre un error al persistir los datos en la base de datos.
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

    /**
     *  Elimina un registro específico en función del ID de la orden
     *  el ID de producto-precio asociado. Verifica previamente que tanto la orden como
     *  el producto-precio existan en la base de datos.
     * @param idOrden el ID de la orden a la que pertenece el detalle a eliminar
     * @param idProductoPrecio el ID del prodcuto-precio asociado al detalle a eliminar
     * @throws EntityNotFoundException si la orden o el producto-precio no existen en la base de datos.
     * @throws PersistenceException si ocurre un error al ejecutar la eliminación en la base de datos.
     */
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

    /**
     * Actualizacion de un registro existente se utiliza el ID de la orden y el ID de producto-precio como clave compuesta
     * @param registro  el objeto con los nuevos datos que se desean actualizar.
     * @param idOrden el ID d ela orden a la que pertemece el detalle
     * @param idProductoPrecio el ID del producto-precio asociado al detalle
     * @return el objeto actualizado
     * @throws IllegalArgumentException si alguno de los parámetros es nulo o inválido.
     * @throws EntityNotFoundException si no se encuentra la orden o el producto-precio especificado.
     * @throws PersistenceException si ocurre un error al intentar actualizar el registro en la base de datos.
     */
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
