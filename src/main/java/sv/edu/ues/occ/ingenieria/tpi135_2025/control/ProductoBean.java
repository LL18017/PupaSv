/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

/**
 * @author mjlopez bean para control de entidad Producto
 */
@Stateless
@LocalBean
public class ProductoBean extends AbstractDataAccess<Producto> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ProductoBean() {
        super(Producto.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idProducto";
    }

    /**
     * Encuentra un una lista de Productos segun un rango dado, un
     * idTipoProducto y la propiedad activo.
     *
     * @param first rango de inicio de los registros totales.
     * @param max cantidad maxima de registros
     * @param idTipoProducto id del tipoProducto relacionado con el producto
     * @param activo si registro cuenta con la propiedda activo true o false
     * @return lista de tipo T correspondiente al rango. devuelve una lista
     * vacion si no hay registros o si el rango es incorrecto
     * @throws IllegalArgumentException si se envian datos erroneos
     * @throws EntityNotFoundException si no existe el tipoProducto con el id
     * dado
     * @throws PersistenceException si existe un error con la base de datos
     * @throws IllegalStateException si existe error al persistir dato
     */
    public List<Producto> findRangeByIdTipoProductosAndActivo(Integer idTipoProducto, boolean activo, Integer first, Integer max) {
        try {
            if (verificarDatos(idTipoProducto, first, max)) {
                TipoProducto tp = em.find(TipoProducto.class, idTipoProducto);
                if (tp == null) {
                    throw new EntityNotFoundException("Tipo producto no encontrado");
                }
            }
            return em.createNamedQuery("Producto.findActivosAndIdTipoProducto", Producto.class)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .setParameter("activo", activo)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("error al aceder al la base de datos", e);
        }
    }

    /**
     * Busca la cantidad de registros de la cantidad de Producto que cuentas con
     * idTipoProdcuto especifico y la propiedad activo.
     *
     * @return la cantidda de registros de productos. devuelve 0
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     * @throws EntityNotFoundException Si no existe registros con ese
     * idTipoProducto.
     * @throws NonUniqueResultException si se recibe mas de un dato
     * @throws PersistenceException si hay un error general con la base de
     * datos.
     */
    public Long countByIdTipoProductosAndActivo(Integer idTipoProducto, boolean activo) {
        try {
            if (idTipoProducto == null || idTipoProducto <= 0) {
                throw new IllegalArgumentException("idTipoProducto no puede ser nulo o menor que cero");
            }
            TipoProducto tp = em.find(TipoProducto.class, idTipoProducto);
            if (tp == null) {
                throw new EntityNotFoundException("Tipo producto no encontrado");
            }
            return em.createNamedQuery("Producto.countActivosAndIdTipoProducto", Long.class)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .setParameter("activo", activo)
                    .getSingleResult();
        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("El valor devuelto no es un resultado único");
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }

    }

    /**
     * persite un registro Producto en la base de datos ademas de realizar su
     * detalle.
     *
     * @param registro Producto a ser persistida. devuelve una lista vacion si
     * no hay registros o si el rango es incorrecto
     * @throws IllegalStateException Si no se puede acceder al repositorio o la
     * @throws IllegalArgumentException si el registro es nulo.
     * @throws EntityExistsException si el producto ya existe ya existe
     * @throws PersistenceException si ocurrio un error con la base de datos
     * @throws EntityNotFoundException si el registro tipoProducto no existe
     */
    public void createProductoAndDetail(Producto registro, Integer idTipoProducto) {
        try {
            if (registro == null || idTipoProducto == null) {
                throw new IllegalArgumentException("El registro y el idTipoProducto no pueden ser nulos");
            }
            if (idTipoProducto <= 0) {
                throw new IllegalArgumentException("idTipoProducto no puede ser menor a cero");
            }
            TipoProducto tp = em.find(TipoProducto.class, idTipoProducto);
            if (tp == null) {
                throw new EntityNotFoundException("Tipo producto no encontrado");
            }
            em.persist(registro);
            em.flush();
            em.refresh(registro);

            ProductoDetalle detalle = new ProductoDetalle(idTipoProducto, registro.getIdProducto());
            detalle.setActivo(true);
            em.persist(detalle);
        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (EntityExistsException e) {
            throw new EntityExistsException("la entidad ya existe en la base de datos", e);
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

    /**
     * Encuentra un una lista de Productos segun un rango dado y la propiedad
     * activo.
     *
     * @param first rango de inicio de los registros totales.
     * @param max cantidad maxima de registros
     * @param activo si registro cuenta con la propiedda activo true o false
     * @return lista de tipo T correspondiente al rango. devuelve una lista
     * vacion si no hay registros o si el rango es incorrecto
     * @throws IllegalArgumentException si se envian datos erroneos
     * @throws EntityNotFoundException si no existe el tipoProducto con el id
     * dado
     * @throws PersistenceException si existe un error con la base de datos
     * @throws IllegalStateException si existe error al persistir dato
     */
    public List<Producto> findRangeProductoActivos(Integer first, Integer max, Boolean activo) {
        if (first == null || max == null || first < 0 || max <= 0 || max > 50) {
            throw new IllegalArgumentException("first , max no pueden ser nulos o menores que cero");
        }
        try {
            return em.createNamedQuery("Producto.findByAnyActivo", Producto.class)
                    .setParameter("activo", activo)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new PersistenceException("error al aceder al repositorio", e);
        }
    }

    /**
     * Busca la cantidad de registros de la cantidad de Producto que cuentas la
     * propiedad activo.
     *
     * @return la cantidda de registros de productos. devuelve 0
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     * @throws EntityNotFoundException Si no existe registros con ese
     * idTipoProducto.
     * @throws NonUniqueResultException si se recibe mas de un dato
     * @throws PersistenceException si hay un error general con la base de
     * datos.
     */
    public Long countProductoActivos(Boolean activo) {
        try {
            return em.createNamedQuery("Producto.countByAnyActivo", Long.class)
                    .setParameter("activo", activo)
                    .getSingleResult();
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("El valor devuelto no es un resultado único");
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

    /**
     * elimina un producto y su detalle
     *
     * @param idProducto id del producto a borrar
     * @param idTipoProducto id del tipo producto relaionado
     * @return lista de tipo T correspondiente al rango. devuelve una lista
     * vacion si no hay registros o si el rango es incorrecto
     * @throws IllegalArgumentException si se envian datos erroneos
     * @throws EntityNotFoundException si no existe el tipoProducto con el id
     * dado
     * @throws PersistenceException si existe un error con la base de datos
     * @throws IllegalStateException si existe error al persistir dato
     */
    public void deleteProductoAndDetail(Long idProducto, Integer idTipoProducto) {
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("El id del producto no puede ser null O MENOR A CERO");
        }
        if (idTipoProducto == null || idTipoProducto <= 0) {
            throw new IllegalArgumentException("El id del Tipoproducto no puede ser null O MENOR A CERO");
        }
        try {
            int detalleBorrado = em.createNamedQuery("ProductoDetalle.deleteByIdProductoAndIdProducto")
                    .setParameter("idProducto", idProducto)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .executeUpdate();
            if (detalleBorrado == 1) {
                delete(idProducto);
                return;
            }
            throw new EntityNotFoundException("detalle no pudo ser borrado");
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

    /**
     * evalua si los propiedades cuentas con los requisitos para ejecutar sus
     * respectivas operaciones
     *
     * @param idTipoProducto id del TipoProducto a evaluar
     * @param first primer dato requerido
     * @param max cantidda de datos que se requieren a evaluar
     * @return true si se han enviado de forma correcta
     * @throws IllegalArgumentException si se envian datos erroneos
     */
    public Boolean verificarDatos(Integer idTipoProducto, Integer first, Integer max) {
        if (first == null || first < 0) {
            throw new IllegalArgumentException("first no pueden ser null o menor que cero");
        }
        if (max == null || max <= 0) {
            throw new IllegalArgumentException("max no pueden ser null o menor que cero");
        }
        if (idTipoProducto == null || idTipoProducto <= 0) {
            throw new IllegalArgumentException("idTipoProducto no puede ser null o menor que cero");
        }
        return true;
    }

    public List<Producto> findListByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }
        try {
            return em.createNamedQuery("Producto.findByNombre", Producto.class)
                    .setParameter("nombre", nombre.trim())
                    .getResultList();
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

    public Producto findByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo o vacío");
        }
        try {
            return em.createNamedQuery("Producto.findByNombre", Producto.class)
                    .setParameter("nombre", nombre.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // O puedes lanzar una excepción si prefieres
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("Se encontraron múltiples productos con el mismo nombre");
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

}
