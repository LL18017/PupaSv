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
import java.util.logging.Level;
import java.util.logging.Logger;

import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.Producto;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetalle;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.ProductoDetallePK;
import sv.edu.ues.occ.ingenieria.tpi135_2025.entity.TipoProducto;

/**
 * @author mjlopez bean para control de entidad ProductoDetalle
 */
@LocalBean
@Stateless
public class ProductoDetalleBean extends AbstractDataAccess<ProductoDetalle> implements Serializable {

    @PersistenceContext(unitName = "PupaSV-PU")
    EntityManager em;

    public ProductoDetalleBean() {
        super(ProductoDetalle.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public String orderParameterQuery() {
        return "idProductoDetalle";
    }

    /**
     * Encuentra un Producto detalle específico por su dupla de ids(idTipoProducto e idProducto)
     *
     * @param idProducto     id del producto relacionado al detalle
     * @param idTipoProducto id del tipo producto relacionado al detalle
     * @return el detalle correspondiente a los ids enviados.
     * @throws IllegalArgumentException Si el ID es nulo o inválido.
     * @throws IllegalStateException    Si no se puede acceder al repositorio.
     * @throws NoResultException  Si no existe una entidad con ese id.
     * @throws PersistenceException     si hay un error general con la base de datos.
     */
    public ProductoDetalle findById(Integer idTipoProducto, Long idProducto) {
        if (idTipoProducto == null || idTipoProducto <= 0)
            throw new IllegalArgumentException("idTipoProducto no pueden ser nulos o menor a cero");
        if (idProducto == null || idProducto <= 0)
            throw new IllegalArgumentException("idTipoProducto no pueden ser nulos o menor a cero");
        try {
            Producto producto = em.find(Producto.class, idProducto);
            TipoProducto tipoProducto = em.find(TipoProducto.class, idTipoProducto);

            if (tipoProducto == null) {
                throw new EntityNotFoundException("No se encontro el registro de tipoProdcuto con el id " + idTipoProducto);
            }
            if (producto == null) {
                throw new EntityNotFoundException("no se encontro el registro de producto con el id " + idProducto);
            }
            return em.createNamedQuery("ProductoDetalle.findByIdTipoProductoAndIdProducto", ProductoDetalle.class)
                    .setParameter("idTipoProducto", idTipoProducto)
                    .setParameter("idProducto", idProducto)
                    .getSingleResult();
        } catch (EntityNotFoundException | IllegalStateException | IllegalArgumentException | NoResultException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new PersistenceException("error con la base de datos", ex);
        }
    }

    /**
     * Encuentra un Producto detalle específico por su dupla de ids(idTipoProducto e idProducto)
     *
     * @param idProducto     id del producto relacionado al detalle
     * @param idTipoProducto id del tipo producto relacionado al detalle
     * @throws IllegalArgumentException Si el ID es nulo o inválido.
     * @throws EntityNotFoundException  si algulo de los ids no existe
     * @throws IllegalStateException    Si no se puede acceder al repositorio.
     * @throws EntityNotFoundException  Si no existe una entidad con ese id.
     * @throws PersistenceException     si hay un error general con la base de datos.
     */
    public void deleteByIdTipoProductoAndIdProducto(Integer idTipoProducto, Long idProducto) {
        if (idTipoProducto == null || idTipoProducto <= 0) {
            throw new IllegalArgumentException("idTipoProducto no pueden ser nulos  o menor o igual a cero");
        }
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("idProducto no pueden ser nulos o menor o igual a cero");
        }
        try {
            TipoProducto tipoProducto = em.find(TipoProducto.class, idTipoProducto);
            if (tipoProducto == null) {
                throw new EntityNotFoundException("no existe el tipoProducto con el id " + idTipoProducto);
            }

            Producto producto = em.find(Producto.class, idProducto);
            if (producto == null) {
                throw new EntityNotFoundException("no existe el producto con el id " + idProducto);
            }
            em.createNamedQuery("ProductoDetalle.deleteByIdProductoAndIdProducto").setParameter("idProducto", idProducto).setParameter("idTipoProducto", idTipoProducto).executeUpdate();
            return;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException ex) {
            throw new PersistenceException("error con la base de datos", ex);
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("error al persistir", ex);
        }
    }

    public ProductoDetalle update(ProductoDetalle registro, Integer idTipoProducto, Long idProducto) throws IllegalStateException, IllegalArgumentException {
        if (registro == null) {
            throw new IllegalArgumentException("registro no puede ser nulo");
        }
        if (idTipoProducto == null || idTipoProducto <= 0) {
            throw new IllegalArgumentException("idTipoProdcuto no puede ser nulo o menor que 0");
        }
        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException("idProdcuto no puede ser nulo o menor que 0");
        }
        try {
            Producto producto = em.find(Producto.class, idProducto);
            if (producto == null) {
                throw new EntityNotFoundException("No existe el producto con el id " + idProducto);
            }
            TipoProducto tipoProducto = em.find(TipoProducto.class, idTipoProducto);
            if (tipoProducto == null) {
                throw new EntityNotFoundException("No existe el tipoProdcuto con el id " + idProducto);
            }
            ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
            registro.setProductoDetallePK(pk);
            em.merge(registro);
            return registro;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw new IllegalStateException("error al persistir el registro", e);
        } catch (PersistenceException ex) {
            throw new PersistenceException("error con la base de datos", ex);
        }
    }

    /**
     * Encuentra un una lista de ProductoDetalle segun un rango dado.
     *
     * @param first rango de inicio de los registros totales.
     * @param max   cantidad maxima de registros
     * @return lista de tipo ProductoDetalle correspondiente al rango.
     * @throws IllegalStateException    Si no se puede acceder al repositorio.
     * @throws IllegalArgumentException si los valores de first o max son incorrectos
     * @throws PersistenceException     si ocurre un error con la base d edatos
     */
    @Override
    public List<ProductoDetalle> findRange(Integer first, Integer max) {
        if (!verificarnull(first, max)) {
            throw new IllegalArgumentException("los valores de first y max no pueden ser nulos");
        }
        if (!verificarMayor(first, max)) {
            throw new IllegalArgumentException("los valores de first deben ser mayores a cero y max no puede ser menor o igual a cero");
        }
        try {
            return em.createNamedQuery("ProductoDetalle.findAll", ProductoDetalle.class)
                    .setFirstResult(first)
                    .setMaxResults(max)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new PersistenceException("error al aceder al base de datos", e);
        }
    }

    public void create(ProductoDetalle registro, Integer idTipoProducto, Long idProducto) throws IllegalStateException, IllegalArgumentException {
        try {
            if (registro == null) {
                throw new IllegalArgumentException("registro no puede ser nulo");
            }
            if (idTipoProducto == null || idTipoProducto <= 0) {
                throw new IllegalArgumentException("idTipoProducto no puede ser nulo o menor a cero o igual a cero");
            }
            if (idProducto == null || idProducto <= 0) {
                throw new IllegalArgumentException("idProducto no puede ser nulo o menor a cero o igual a cero");
            }
            TipoProducto tipoProducto = em.find(TipoProducto.class, idTipoProducto);
            if (tipoProducto == null) {
                throw new EntityNotFoundException("el tipoProducto no existe el id " + idProducto);
            }
            Producto producto = em.find(Producto.class, idProducto);
            if (producto == null) {
                throw new EntityNotFoundException("el producto no existe el id " + idProducto);
            }
            ProductoDetallePK pk = new ProductoDetallePK(idTipoProducto, idProducto);
            registro.setProductoDetallePK(pk);
            super.create(registro);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("error con la base de datos", e);
        } catch ( IllegalStateException e) {
            throw new IllegalStateException("error al persistir el registro");
        }
    }
}
