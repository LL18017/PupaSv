/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.ConstraintViolationException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <T> Tipo de entidad manejada por la clase hija.
 * @author mjlopez Clase abstracta que implementa el patrón de diseño Fabrica
 * Abstracta. Proporciona métodos genéricos para el acceso a datos usando JPA
 * Criterial Query.
 */
public abstract class AbstractDataAccess<T> {

    final Class tipoDato;

    /**
     * Constructor que recibe el tipo de dato manejado.
     *
     * @param t Clase del tipo de entidad que se maneja.
     */
    public AbstractDataAccess(Class t) {
        this.tipoDato = t;
    }

    /**
     * Método abstracto que debe ser implementado por la clase hija para
     * proporcionar un EntityManager específico.
     *
     * @return EntityManager configurado para la entidad.
     */
    public abstract EntityManager getEntityManager();

    /**
     * metodo para obtener el nombre del valor del identificador de entidad para
     * ordenar
     *
     * @return
     */
    public abstract String orderParameterQuery();


    /**
     * Encuentra todos los registros de la entidad. Realiza una consulta
     * utilizando Criteria API y ordena los resultados de acuerdo con el
     * parámetro definido en orderParameterQuery().
     *
     * @return Lista de entidades de tipo T.
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     * @throws PersistenceException  si hubo un error relacionado a la base de datos.
     */
    public List<T> findAll() {
        EntityManager em = null;
        List<T> resultados = null;
        em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("error al aceder al repositorio");
        }
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery(tipoDato);
            Root<T> r = cq.from(tipoDato);
            cq.select(r).orderBy(cb.asc(r.get(orderParameterQuery())));
            TypedQuery<T> q = em.createQuery(cq);
            resultados = q.getResultList();
            return resultados;
        } catch (PersistenceException e) {
            throw new PersistenceException("error al aceder a la base de datos", e);
        }
    }

    /**
     * Encuentra un objeto específico por su ID. Verifica que el ID no sea nulo
     * antes de realizar la búsqueda.
     *
     * @param id ID del objeto a buscar el cual no debe ser nulo.
     * @return Objeto de tipo T correspondiente al ID.
     * @throws IllegalArgumentException Si el ID es nulo o inválido.
     * @throws IllegalStateException    Si no se puede acceder al repositorio.
     * @throws EntityNotFoundException  Si no existe una entidad con ese id.
     * @throws PersistenceException     si hay un error general con el repositorio.
     */
    public T findById(Object id) throws IllegalStateException {
        EntityManager em = null;
        if (id == null) {
            throw new IllegalArgumentException("parametro no valido");
        }
        if (Long.parseLong(id.toString()) <= 0) {
            throw new IllegalArgumentException("parametro no puede ser menor a cero");
        }
        em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("error al aceder al repositorio");
        }
        try {
            Object entidad = em.find(this.tipoDato, id);
            if (entidad != null) {
                return (T) entidad;
            }
            throw new EntityNotFoundException("El registro con el ID proporcionado no existe en la base de datos");
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("error al aceder a la base de datos", e);
        }
    }

    /**
     * Encuentra un una lista de registros de la entidad segun una rango.
     *
     * @param first rango de inicio de los registros totales.
     * @param max   cantidad maxima de registros
     * @return lista de tipo T correspondiente al rango. devuelve una lista
     * vacion si no hay registros o si el rango es incorrecto
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     */
    public List<T> findRange(Integer first, Integer max) throws IllegalStateException, IllegalArgumentException {
        EntityManager em = null;
        if (!verificarnull(first, max))
            throw new IllegalArgumentException("parametro no valido first y max no puede ser null");
        if (!verificarMayor(first, max))
            throw new IllegalArgumentException("parametro no valido first y max no puede ser menores de cero");

        em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("error al aceder al repositorio");
        }
        try {
            // Construir consultas de criterios
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(this.tipoDato);
            Root<T> raiz = cq.from(this.tipoDato);
            cq.select(raiz).orderBy(cb.asc(raiz.get(orderParameterQuery())));
            TypedQuery<T> query = em.createQuery(cq);
            query.setFirstResult(first);
            query.setMaxResults(max);
            return query.getResultList();
        } catch (PersistenceException e) {
            throw new PersistenceException("error al aceder al la base de datos", e);
        }
    }


    /**
     * Busca la cantidad de registros de la cantidad de registros T.
     *
     * @return  la cantidda de registros de tipo T totales. devuelve 0
     * si no hay registros o si em es nulo
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     * @throws PersistenceException  si hay un error general con la base de datos.
     */
    public Long count() {
        EntityManager em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("Error al acceder al repositorio");
        }
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> raiz = cq.from(this.tipoDato);
            cq.select(cb.count(raiz));

            return em.createQuery(cq).getSingleResult();
        } catch (NonUniqueResultException e) {
            throw new NonUniqueResultException("El valor devuelto no es un resultado único");
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }


    /**
     * persite un registro T en la base de datos.
     * @param registro entidad a ser persistida. devuelve una lista vacion si no hay registros o si el rango es incorrecto
     * @throws IllegalStateException        Si no se puede acceder al repositorio o la
     * @throws IllegalArgumentException     si el registro T es nulo.
     * @throws EntityExistsException        si la entidda ya existe
     * @throws ConstraintViolationException si la entidda no cumple con algun campo necesio para su persistencia
     */
    public void create(T registro) throws IllegalStateException, IllegalArgumentException {
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo");
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            if (em == null) {
                throw new IllegalStateException("Error al acceder al repositorio");
            }
            em.persist(registro);
        } catch (EntityExistsException e) {
            throw new EntityExistsException("la entidad ya existe en la base de datos", e);
        } catch (ConstraintViolationException e) {
            throw new ConstraintViolationException("La entidad no cuenta con todos los valores necesarios para persistir", e.getConstraintViolations());
        }catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }
    }

    /**
     * Actualiza un registro del repositorio.
     *
     * @param registro entidad a ser actualizada.
     * @return registro modificado
     * @throws IllegalStateException        Si no se puede acceder al repositorio.
     * @throws IllegalArgumentException     si el registro T es nulo.
     * @throws EntityNotFoundException      si el registro no se encontraba previamente persistido.
     * @throws ConstraintViolationException si el registro T no ha agregado alguno de los campos obligatorios
     * @throws PersistenceException         si existe problemas con base de datos
     */

    public T update(T registro, Object id) throws IllegalStateException, IllegalArgumentException {
        T modificado = null;
        EntityManager em = null;
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo");
        }
        if (id == null || Long.parseLong(id.toString()) <= 0) {
            throw new IllegalArgumentException("El registro debe poseer un id mayor que 0");
        }
        em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("Error al acceder al repositorio");
        }
        try {
            T existente = (T) em.find(tipoDato, id);
            if (existente == null) {
                throw new EntityNotFoundException("El registro con el ID proporcionado no existe en la base de datos");
            }
            modificado = em.merge(registro);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException("Error al acceder a la base de datos", e);
        }

        return modificado;
    }


    /**
     * Elimina un registro T de la base d edatos.
     *
     * @param id entidad a ser borrada.
     * @throws IllegalStateException        Si no se puede acceder al repositorio.
     * @throws IllegalArgumentException     si el registro T es nulo.
     * @throws EntityNotFoundException      si el registro no se encontraba previamente persistido.
     * @throws PersistenceException         si existe problemas con base de datos
     * @throws ConstraintViolationException si existe problemas de restricion con base de datos
     */
    public void delete(Object id) {
        if (id == null || Long.parseLong(id.toString()) <= 0) {
            throw new IllegalArgumentException("El registro debe poseer un id mayor que 0");
        }
        EntityManager em = null;
        em = getEntityManager();
        if (em == null) {
            throw new IllegalStateException("Error al acceder al repositorio");
        }
        try {
            T registro = (T) em.find(tipoDato, id);
            if (registro == null) {
                throw new EntityNotFoundException("El registro con el ID proporcionado no existe en la base de datos");
            }
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaDelete<T> cd = cb.createCriteriaDelete(this.tipoDato);
            Root<T> raiz = cd.from(this.tipoDato);
            cd.where(cb.equal(raiz, registro));
            em.createQuery(cd).executeUpdate();
            return;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (PersistenceException e) {
            throw new PersistenceException(e);
        }
    }

    public boolean verificarnull(Integer first, Integer max) {
        return first != null && max != null;
    }

    public boolean verificarMayor(Integer first, Integer max) {
        return first >= 0 && max > 0;
    }
}
