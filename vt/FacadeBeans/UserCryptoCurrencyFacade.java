/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.UserCryptoCurrency;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class UserCryptoCurrencyFacade extends AbstractFacade<UserCryptoCurrency> {
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
    public UserCryptoCurrencyFacade() {
        super(UserCryptoCurrency.class);
    }

    public List<UserCryptoCurrency> findUserCryptoCurrenciesByUserPrimaryKey(Integer primaryKey) {
        /*
        The following @NamedQuery definition is given in UserCryptoCurrency entity class file:
        @NamedQuery(name = "UserCryptoCurrency.findUserCryptoCurrencysByUserId", query = "SELECT u FROM UserCryptoCurrency u WHERE u.userId.id = :userId")

        The following statement obtains the results from the named database query.
         */
        return entityManager.createNamedQuery("UserCryptoCurrency.findByUserID")
                .setParameter("userID", primaryKey)
                .getResultList();
    }

}
