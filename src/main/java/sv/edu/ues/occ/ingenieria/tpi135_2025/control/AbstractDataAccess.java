/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.tpi135_2025.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mjlopez Clase abstracta que implementa el patrón de diseño Fabrica
 * Abstracta. Proporciona métodos genéricos para el acceso a datos usando JPA
 * Criterial Query.
 *
 * @param <T> Tipo de entidad manejada por la clase hija.
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
     */
    public List<T> findAll() {
        EntityManager em = null;
        List<T> resultados = null;
        try {
            em = getEntityManager();
            if (em == null) {
                throw new IllegalStateException("error al aceder al repositorio");
            }
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery cq = cb.createQuery(tipoDato);
            Root<T> r = cq.from(tipoDato);
            cq.select(r).orderBy(cb.asc(r.get(orderParameterQuery())));
            TypedQuery<T> q = em.createQuery(cq);
            resultados = q.getResultList();
            return resultados;
        } catch (java.lang.IllegalStateException e) {
            throw new IllegalStateException("error al aceder al repositorio", e);
        }
    }

    /**
     * Encuentra un objeto específico por su ID. Verifica que el ID no sea nulo
     * antes de realizar la búsqueda.
     *
     * @param id ID del objeto a buscar el cual no debe ser nulo.
     * @return Objeto de tipo T correspondiente al ID.
     * @throws IllegalArgumentException Si el ID es nulo o inválido.
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     */
    public T findById(Object id) throws IllegalArgumentException, IllegalStateException {
        EntityManager em = null;
        if (id == null) {
            throw new IllegalArgumentException("parametro no valido");
        }
        try {
            em = getEntityManager();
            if (em == null) {
                throw new IllegalStateException("error al aceder al repositorio");
            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException("error al aceder al repositorio", e);

        }
        return (T) em.find(this.tipoDato, id);
    }

    /**
     * Encuentra un una lista de registros de la entidad segun una rango.
     *
     * @param first rango de inicio de los registros totales.
     * @param max cantidad maxima de registros
     * @return lista de tipo T correspondiente al rango. devuelve una lista
     * vacion si no hay registros o si el rango es incorrecto
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     */
    public List<T> findRange(int first, int max) throws IllegalStateException, IllegalArgumentException {
        EntityManager em = null;
        if (first >= 0 && max > 0) {
            try {
                em = getEntityManager();
                if (em == null) {
                    throw new IllegalStateException("error al aceder al repositorio");
                }
                 // Construir consultas de criterios
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<T> cq = cb.createQuery(this.tipoDato);
                    Root<T> raiz = cq.from(this.tipoDato);
                    cq.select(raiz).orderBy(cb.asc(raiz.get(orderParameterQuery())));
                    TypedQuery<T> query = em.createQuery(cq);
                    query.setFirstResult(first);
                    query.setMaxResults(max);
                    return query.getResultList();
            } catch (IllegalStateException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                 throw new IllegalStateException("error al aceder al repositorio");
            }
        }
        return List.of();
    }

    /**
     * Busca la cantidad de registros de la cantidad de registros T.
     *
     * @return un int con la cantidda de registros de tipo T totales. devuelve 0
     * si no hay registros o si em es nulo
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     */
public Long count() {
    EntityManager em = null;
    try {
        em = getEntityManager();

        if (em == null) {
            throw new IllegalStateException("error al aceder al repositorio");
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class); // Parametrizar con Long
        Root<T> raiz = cq.from(this.tipoDato);
        cq.select(cb.count(raiz)); // Contar registros

        TypedQuery<Long> q = em.createQuery(cq);

        return q.getSingleResult();
    } catch (IllegalStateException e) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
        throw new IllegalStateException("error al aceder al repositorio");
    }
}

    /**
     * persite un registro T en la base de datos.
     *
     * @param registro entidad a ser persistida. devuelve una lista vacion si no
     * hay registros o si el rango es incorrecto
     * @throws IllegalStateException Si no se puede acceder al repositorio o la
     * entidad no ha sido persistida.
     * @throws IllegalArgumentException si el registro T es nulo.
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
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, e.getMessage(), e);
             throw new IllegalStateException("Error al acceder al repositorio");
        }
    }

    /**
     * Actualiza un registro del repositorio.
     *
     * @param registro entidad a ser actualizada.
     * @return la entidad midificada.
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     * @throws IllegalArgumentException si el registro T es nulo.
     */

    public T update(T registro) {
        T modificado = null;
        EntityManager em = null;
        if (registro == null) {
            throw new IllegalArgumentException("El registro no puede ser nulo");
        }
        try {
            em = getEntityManager();
            if (em == null) {
            throw new IllegalStateException("Error al acceder al repositorio");
            }
            modificado = em.merge(registro);
        } catch (IllegalStateException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,e.getMessage(),e);
            throw new IllegalStateException("Error al acceder al repositorio", e);
        }
        return modificado;
    } 
    
    /**
     * Elimina un registro T de la base d edatos.
     *
     * @param registro entidad a ser borrada.
     * @throws IllegalStateException Si no se puede acceder al repositorio.
     * @throws IllegalArgumentException si el registro T es nulo.
     */
    public void delete(T registro) {
        if (registro != null) {
            EntityManager em = null;
            try {
                em = getEntityManager();
                if (em == null) {
                    throw new IllegalStateException("Error al acceder al repositorio");
                }
                 CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaDelete<T> cd = cb.createCriteriaDelete(this.tipoDato);
                    Root<T> raiz = cd.from(this.tipoDato);
                    cd.where(cb.equal(raiz, registro));
                em.createQuery(cd).executeUpdate();
                    return;
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                 throw new IllegalStateException("Error al acceder al repositorio", e);
            }
        }
        throw new IllegalArgumentException();
    }

}
