/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.FacadeBeans;

import edu.vt.EntityBeans.Company;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

// @Stateless annotation implies that the conversational state with the client shall NOT be maintained.
@Stateless
public class CompanyFacade extends AbstractFacade<Company> {
    /*
    ---------------------------------------------------------------------------------------------
    The EntityManager is an API that enables database CRUD (Create Read Update Delete) operations
    and complex database searches. An EntityManager instance is created to manage entities
    that are defined by a persistence unit. The @PersistenceContext annotation below associates
    the entityManager instance with the persistence unitName identified below.
    ---------------------------------------------------------------------------------------------
     */
    @PersistenceContext(unitName = "CryptoCurrenciesAndCompaniesPU")

    // 'entityManager' holds the object reference to the instantiated EntityManager object.
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
    public CompanyFacade() {
        super(Company.class);
    }
    
    /*
    ***********************************************************************************
    *   Java Persistence API (JPA) Query Formulation for Searching a MySQL Database   *
    ***********************************************************************************
    By default, MySQL does not distinguish between upper and lower case letters in searches.
    Therefore, searches based on the queries below are all case insensitive by default.

    The LIKE Expression
        SELECT c FROM Company c WHERE c.name LIKE :'gen%'       All companies whose names begin with "gen"
        SELECT c FROM Company c WHERE c.name LIKE :'%tion'      All companies whose names end with "tion"
        SELECT c FROM Company c WHERE c.name LIKE :'%com%'      All companies whose names contain "com"
    
    The LIKE expression uses wildcard character % to search for strings that match the wildcard pattern.

    ================================
    EntityManager Method createQuery
    ================================
    Query createQuery(String qlString)
        Create an instance of Query for executing a Java Persistence (JPA) query language statement.
    Parameter:
        qlString - a Java Persistence query string, e.g., "SELECT c FROM Company c WHERE c.name LIKE :searchString"
    Returns:
        the object reference of the newly created Query object

    =========================
    Query Method setParameter
    =========================
    Query setParameter(String name, Object value)
        Bind an argument value to a named parameter
    Parameters:
        name - parameter name (e.g., "searchString")
        value - parameter value (e.g., the searchString parameter that contains the search string the user entered for searching)
    Returns:
        the same object reference of the newly created Query object

    ==========================
    Query Method getResultList
    ==========================
    List getResultList()
        Execute a SELECT query and return the query results as an untyped List
    Returns:
        the object reference of the newly created List containing the search results
    */

    /*
     ***************************
     *   Search Query Type 1   *
     ***************************
     */

    /*
    -----------------------------
    Search Category: COMPANY NAME
    -----------------------------
     */
    // Searches CompaniesDB for companies where company name contains the searchString entered by the user.
    public List<Company> nameQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the company name
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT c FROM Company c WHERE c.name LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    /*
    --------------------------------------
    Search Category: STOCK SYMBOL (TICKER)
    --------------------------------------
     */
    // Searches CompaniesDB for companies where company's stock ticker name contains the searchString entered by the user.
    public List<Company> tickerQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the stock ticker name
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT c FROM Company c WHERE c.ticker LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    /*
    -------------------------------------
    Search Category: BUSINESS SECTOR NAME
    -------------------------------------
     */
    // Searches CompaniesDB for companies where business Sector name contains the searchString entered by the user.
    public List<Company> sectorQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in the business Sector name
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT c FROM Company c WHERE c.sector LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    /*
    --------------------
    Search Category: ALL
    --------------------
     */
    // Searches CompaniesDB for companies where company name OR ticker OR sector name contains the searchString entered by the user.
    public List<Company> allQuery(String searchString) {
        // Place the % wildcard before and after the search string to search for it anywhere in company name, ticker, or sector name
        searchString = "%" + searchString + "%";
        // Conduct the search in a case-insensitive manner and return the results in a list.
        return getEntityManager().createQuery(
                        "SELECT c FROM Company c WHERE c.name LIKE :searchString OR c.ticker LIKE :searchString OR c.sector LIKE :searchString")
                .setParameter("searchString", searchString)
                .getResultList();
    }

    /*
     ***************************
     *   Search Query Type 2   *
     ***************************
     */
    // Company name contains companyNameQ AND sector contains sectorQ
    public List<Company> type2SearchQuery(String companyNameQ, String sectorQ) {

        companyNameQ = "%" + companyNameQ + "%";
        sectorQ = "%" + sectorQ + "%";

        return getEntityManager().createQuery(
                        "SELECT c FROM Company c WHERE c.name LIKE :companyNameQ AND c.sector LIKE :sectorQ")
                .setParameter("companyNameQ", companyNameQ)
                .setParameter("sectorQ", sectorQ)
                .getResultList();
    }

    /*
     ***************************
     *   Search Query Type 3   *
     ***************************
     */
    // Company name contains companyNameQ AND number of employees >= numberOfEmployeesQ
    public List<Company> type3SearchQuery(String companyNameQ, Integer numberOfEmployeesQ) {

        companyNameQ = "%" + companyNameQ + "%";

        return getEntityManager().createQuery(
                        "SELECT c FROM Company c WHERE c.name LIKE :companyNameQ AND c.employees >= :numberOfEmployeesQ")
                .setParameter("companyNameQ", companyNameQ)
                .setParameter("numberOfEmployeesQ", numberOfEmployeesQ)
                .getResultList();
    }

    /*
     ***************************
     *   Search Query Type 4   *
     ***************************
     */
    // Company name contains companyNameQ AND revenues in millions <= revenuesQ
    public List<Company> type4SearchQuery(String companyNameQ, Integer revenuesQ) {

        companyNameQ = "%" + companyNameQ + "%";

        return getEntityManager().createQuery(
                        "SELECT c FROM Company c WHERE c.name LIKE :companyNameQ AND c.revenues <= :revenuesQ")
                .setParameter("companyNameQ", companyNameQ)
                .setParameter("revenuesQ", revenuesQ)
                .getResultList();
    }

    /*
     ***************************
     *   Search Query Type 5   *
     ***************************
     */
    // Company name contains companyNameQ AND sector contains sectorQ AND number of employees >= numberOfEmployeesQ
    public List<Company> type5SearchQuery(String companyNameQ, String sectorQ, Integer numberOfEmployeesQ) {

        companyNameQ = "%" + companyNameQ + "%";
        sectorQ = "%" + sectorQ + "%";

        return getEntityManager().createQuery(
                        "SELECT c FROM Company c WHERE c.name LIKE :companyNameQ AND c.sector LIKE :sectorQ AND c.employees >= :numberOfEmployeesQ")
                .setParameter("companyNameQ", companyNameQ)
                .setParameter("sectorQ", sectorQ)
                .setParameter("numberOfEmployeesQ", numberOfEmployeesQ)
                .getResultList();
    }

    /*
     ***************************
     *   Search Query Type 6   *
     ***************************
     */
    // Company name contains companyNameQ AND sector contains sectorQ AND revenues in millions <= revenuesQ
    public List<Company> type6SearchQuery(String companyNameQ, String sectorQ, Integer revenuesQ) {

        companyNameQ = "%" + companyNameQ + "%";
        sectorQ = "%" + sectorQ + "%";

        return getEntityManager().createQuery(
                        "SELECT c FROM Company c WHERE c.name LIKE :companyNameQ AND c.sector LIKE :sectorQ AND c.revenues <= :revenuesQ")
                .setParameter("companyNameQ", companyNameQ)
                .setParameter("sectorQ", sectorQ)
                .setParameter("revenuesQ", revenuesQ)
                .getResultList();
    }

}
