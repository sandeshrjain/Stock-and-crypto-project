/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.CryptoCurrency;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class CryptoCurrencyFacade extends AbstractFacade<CryptoCurrency> {
    /*
    ---------------------------------------------------------------------------------------------
    The EntityManager is an API that enables database CRUD (Create Read Update Delete) operations
    and complex database searches. An EntityManager instance is created to manage entities
    that are defined by a persistence unit. The @PersistenceContext annotation below associates
    the entityManager instance with the persistence unitName identified below.
    ---------------------------------------------------------------------------------------------
     */
    @PersistenceContext(unitName = "CryptoCurrenciesAndCompaniesPU")
    private EntityManager entityManager;

    // Obtain the object reference of the EntityManager instance in charge of
    // managing the entities in the persistence context identified above.
    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    /*
    This constructor method invokes its parent AbstractFacade's constructor method,
    which in turn initializes its entity class type T and entityClass instance variable.
     */
    public CryptoCurrencyFacade() {
        super(CryptoCurrency.class);
    }

    public List<CryptoCurrency> nameQuery(String searchString) {
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                "SELECT u FROM CryptoCurrency u WHERE u.name LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    public List<CryptoCurrency> symbolQuery(String searchString) {
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT u FROM CryptoCurrency u WHERE u.symbol LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    public List<CryptoCurrency> descriptionQuery(String searchString) {
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT u FROM CryptoCurrency u WHERE u.description LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    public List<CryptoCurrency> allQuery(String searchString) {
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT u FROM CryptoCurrency u WHERE u.description LIKE :searchString OR u.symbol LIKE :searchString OR u.name LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    public List<CryptoCurrency> type2SearchQuery(String coinDescriptionQ, String obtainmentMethodQ) {
        coinDescriptionQ = "%" + coinDescriptionQ + "%";
        obtainmentMethodQ = "%" + obtainmentMethodQ + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT u FROM CryptoCurrency u WHERE u.description LIKE :coinDescriptionQ AND u.obtainmentMethod LIKE :obtainmentMethodQ")
                .setParameter("coinDescriptionQ", coinDescriptionQ)
                .setParameter("obtainmentMethodQ", obtainmentMethodQ)
                .getResultList();
    }

    public List<CryptoCurrency> type3SearchQuery(Float fromPriceQ, Float toPriceQ) {
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT u FROM CryptoCurrency u WHERE u.price >= :fromPriceQ AND u.price <= :toPriceQ")
                .setParameter("fromPriceQ", fromPriceQ)
                .setParameter("toPriceQ", toPriceQ)
                .getResultList();
    }

    public List<CryptoCurrency> type4SearchQuery(Date fromReleaseDateQ, Date toReleaseDateQ) {
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT u FROM CryptoCurrency u WHERE u.releaseDate BETWEEN :fromReleaseDateQ AND :toReleaseDateQ")
                .setParameter("fromReleaseDateQ", fromReleaseDateQ)
                .setParameter("toReleaseDateQ", toReleaseDateQ)
                .getResultList();
    }

}
