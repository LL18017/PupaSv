/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.*;

/**
 *
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


    public OrdenDetalle findByIdOrdenAndIdPrecioProducto(Long idOrden,Long idProductoPrecio) {
        try {
            return em.createNamedQuery("OrdenDetalle.findByPrecioProductoAndIdOrden",OrdenDetalle.class)
                    .setParameter("idOrden", idOrden)
                    .setParameter("idProductoPrecio", idProductoPrecio)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        
        return null;
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
     * @param first El índice del primer resultado a devolver.
     * @param max La cantidad máxima de resultados a devolver.
     * @return Una lista de objetos `OrdenDetalle` dentro del rango especificado.
     */
    public List<OrdenDetalle> findRangeByIdOrden(Long idOrden,int first , int max) {
        try {
            return em.createNamedQuery("Orden.findByIdOrden", OrdenDetalle.class)
                    .setParameter("idOrden",idOrden)
                    .setFirstResult(first).setMaxResults(max).
                    getResultList();
        }catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }
        return List.of();
    }
    public Long countByIdOrden(Long idOrden) {
        try {
            return em.createNamedQuery("OrdenDetalle.countByIdOrden",Long.class)
                    .setParameter("idOrden", idOrden)
                    .getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
        }

        return 0L;
    }

    /**
     * Genera un objeto OrdenDetalle a partir de un IdOrden, un Producto y una cantidad.
     * Busca el precio del producto por su idProducto.
     * No calcula subtotal (eso se realiza en otra clase/proceso).
     *
     * @param orden Objeto Orden asociado (debe tener IdOrden).
     * @param producto Objeto Producto a agregar (debe tener IdProducto).
     * @param cantidad Cantidad de producto (si es nula o menor a 1, se asume 1 por defecto).
     * @return Objeto OrdenDetalle generado o lanza excepción si los datos son inválidos.
     */
    public OrdenDetalle generarOrdenDetalleProducto(Orden orden, Producto producto, Integer cantidad) {
        if(orden==null || orden.getIdOrden()==null){
            throw new IllegalArgumentException("La orden es invalida o no tiene ID");
        }

        if(producto==null || producto.getIdProducto()==null){
            throw new IllegalArgumentException("La producto es invalida o no tiene ID");
        }
        if(cantidad==null || cantidad<1){
            cantidad=1;
        }
        ProductoPrecio productoPrecio=em.createNamedQuery("Producto.findByIdProducto",ProductoPrecio.class)
                .setParameter("idProducto",producto.getIdProducto())
                .setMaxResults(1)
                .getSingleResult();

        if(productoPrecio==null){
            throw new IllegalArgumentException("La producto es invalida o no tiene precio");
        }

        OrdenDetalle ordenDetalle = new OrdenDetalle();
        ordenDetalle.setOrden(orden);
        ordenDetalle.setProductoPrecio(productoPrecio);
        ordenDetalle.setCantidad(cantidad);

        return ordenDetalle;
    }


    /**
     * Genera una lista de objetos OrdenDetalle a partir de un Combo y una cantidad de combo especificada.
     * Cada OrdenDetalle representa un producto dentro del combo, multiplicado por la cantidad del combo.
     * No calcula subtotal.
     *
     * @param orden Objeto Orden asociado.
     * @param combo Objeto Combo a procesar.
     * @param cantidadCombo Cantidad de combos a aplicar (por defecto 1 si es nulo o menor a 1).
     * @return Lista de objetos OrdenDetalle generados, o null si hay error o combo sin productos.
     */
    public List<OrdenDetalle> generarOrdenDetalleDesdeCombo(Orden orden, Combo combo, Integer cantidadCombo) {
        if (orden == null || orden.getIdOrden() == null) {
            throw new IllegalArgumentException("La orden es invalida o no tiene ID");
        }
        if (combo == null || combo.getIdCombo() == null) {
            throw new IllegalArgumentException("El combo es invalido o no tiene ID"); // Corregido "La" a "El"
        }
        if (cantidadCombo == null || cantidadCombo < 1) {
            cantidadCombo = 1;
        }

        List<ComboDetalle> comboDetalles = em.createNamedQuery("ComboDetalle.findByIdCombo", ComboDetalle.class)
                .setParameter("idCombo", combo.getIdCombo())
                .getResultList();
        if (comboDetalles == null || comboDetalles.isEmpty()) {
            return Collections.emptyList(); // Devuelve una lista vacía, no null, para evitar NullPointerExceptions
        }
        List<OrdenDetalle> ordenDetalles = new ArrayList<>();
        for (ComboDetalle comboDetalle : comboDetalles) {
            Producto producto = comboDetalle.getProducto();
            if (producto == null || producto.getIdProducto() == null) {
                continue;
            }
            ProductoPrecio precio = null;
            try {
                precio = em.createNamedQuery("ProductoPrecio.findByIdProducto", ProductoPrecio.class)
                        .setParameter("idProducto", producto.getIdProducto())
                        .setMaxResults(1)
                        .getSingleResult();
            } catch (NoResultException e) {
                continue;
            }
            if (precio == null) {
                continue;
            }
            int cantidadFinal = comboDetalle.getCantidad() * cantidadCombo;

            OrdenDetalle detalle = new OrdenDetalle();
            detalle.setOrden(orden); // Aquí se asigna la orden
            detalle.setProductoPrecio(precio);
            detalle.setCantidad(cantidadFinal);
            detalle.setPrecio(precio.getPrecioSugerido());
            ordenDetalles.add(detalle);
        }
        return ordenDetalles;
    }



    /**
     * Genera una lista de objetos OrdenDetalle combinando productos individuales y productos dentro de combos.
     * Si se proporcionan ambos tipos de entrada, se generan OrdenDetalle para ambos.
     * No calcula subtotal, solo genera los detalles de la orden.
     *
     * @param orden Objeto Orden asociado.
     * @param productos Lista de productos individuales a agregar a la orden.
     * @param combos Lista de combos a agregar a la orden.
     * @param cantidadProductos Cantidad de productos individuales a agregar (por defecto 1 si es nulo o menor a 1).
     * @param cantidadCombo Cantidad de combos a agregar (por defecto 1 si es nulo o menor a 1).
     * @return Lista de objetos OrdenDetalle generados combinando productos individuales y productos de los combos.
     */
    public List<OrdenDetalle> generarOrdenDetalleMixto(Orden orden, List<Producto> productos, List<Combo> combos, Integer cantidadProductos, Integer cantidadCombo) {
        if (orden == null || orden.getIdOrden() == null) {
            throw new IllegalArgumentException("La orden es inválida o no tiene ID.");
        }
        // Validar la cantidad de productos (por defecto 1)
        if (cantidadProductos == null || cantidadProductos < 1) {
            cantidadProductos = 1;
        }
        // Validar la cantidad de combos (por defecto 1)
        if (cantidadCombo == null || cantidadCombo < 1) {
            cantidadCombo = 1;
        }
        // Lista para almacenar todos los detalles generados
        List<OrdenDetalle> detallesGenerados = new ArrayList<>();
        // 1. Procesar los productos individuales
        if (productos != null && !productos.isEmpty()) {
            for (Producto producto : productos) {
                // Validar que el producto tenga un precio asignado
                ProductoPrecio productoPrecio = em.createNamedQuery("Producto.findByIdProducto", ProductoPrecio.class)
                        .setParameter("idProducto", producto.getIdProducto())
                        .setMaxResults(1)
                        .getSingleResult();

                if (productoPrecio == null) {
                    continue; // Si el producto no tiene precio, lo ignoramos
                }
                // Crear el OrdenDetalle para el producto
                OrdenDetalle detalleProducto = new OrdenDetalle();
                detalleProducto.setOrden(orden);
                detalleProducto.setProductoPrecio(productoPrecio);
                detalleProducto.setCantidad(cantidadProductos);

                detallesGenerados.add(detalleProducto); // Agregar a la lista de detalles generados
            }
        }
        // 2. Procesar los productos dentro de los combos
        if (combos != null && !combos.isEmpty()) {
            for (Combo combo : combos) {
                // Obtener los detalles del combo (productos dentro del combo)
                List<ComboDetalle> comboDetalles = em.createNamedQuery("ComboDetalle.findByIdCombo", ComboDetalle.class)
                        .setParameter("idCombo", combo.getIdCombo())
                        .getResultList();

                if (comboDetalles != null && !comboDetalles.isEmpty()) {
                    for (ComboDetalle comboDetalle : comboDetalles) {
                        Producto productoCombo = comboDetalle.getProducto();
                        // Validar que el producto del combo tenga un precio asignado
                        ProductoPrecio precioCombo = em.createNamedQuery("Producto.findByIdProducto", ProductoPrecio.class)
                                .setParameter("idProducto", productoCombo.getIdProducto())
                                .setMaxResults(1)
                                .getSingleResult();

                        if (precioCombo == null) {
                            continue; // Si el producto no tiene precio, lo ignoramos
                        }

                        // Calcular la cantidad final del producto = cantidad en combo * cantidad de combos
                        int cantidadFinalCombo = comboDetalle.getCantidad() * cantidadCombo;

                        // Crear el OrdenDetalle para el producto dentro del combo
                        OrdenDetalle detalleCombo = new OrdenDetalle();
                        detalleCombo.setOrden(orden);
                        detalleCombo.setProductoPrecio(precioCombo);
                        detalleCombo.setCantidad(cantidadFinalCombo);

                        detallesGenerados.add(detalleCombo); // Agregar a la lista de detalles generados
                    }
                }
            }
        }
        // Retornar la lista combinada de detalles generados
        return detallesGenerados;
    }

}
