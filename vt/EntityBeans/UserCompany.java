/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.EntityBeans;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/*
The @Entity annotation designates this class as a JPA Entity POJO class
representing the Company table in the CompaniesDB database.
 */
@Entity

// Name of the database table represented
@Table(name = "UserCompany")

@NamedQueries({
        @NamedQuery(name = "UserCompany.findAll", query = "SELECT c FROM Company c")
        , @NamedQuery(name = "UserCompany.findById", query = "SELECT c FROM Company c WHERE c.id = :id")
        , @NamedQuery(name = "UserCompany.findByName", query = "SELECT c FROM Company c WHERE c.name = :name")
        , @NamedQuery(name = "UserCompany.findByExchange", query = "SELECT c FROM Company c WHERE c.exchange = :exchange")
        , @NamedQuery(name = "UserCompany.findByTicker", query = "SELECT c FROM Company c WHERE c.ticker = :ticker")
        , @NamedQuery(name = "UserCompany.findByRevenues", query = "SELECT c FROM Company c WHERE c.revenues = :revenues")
        , @NamedQuery(name = "UserCompany.findByDescription", query = "SELECT c FROM Company c WHERE c.description = :description")
        , @NamedQuery(name = "UserCompany.findBySector", query = "SELECT c FROM Company c WHERE c.sector = :sector")
        , @NamedQuery(name = "UserCompany.findByWebsite", query = "SELECT c FROM Company c WHERE c.website = :website")
        , @NamedQuery(name = "UserCompany.findUserCompaniesByUserId", query = "SELECT c FROM UserCompany c WHERE c.userId.id = :userId")
})

public class UserCompany implements Serializable {

    /*
    ========================================================
    Instance variables representing the attributes (columns)
    of the Company table in the CompaniesDB database.
    ========================================================
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;


    // user_id INT UNSIGNED
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    private User userId;


    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)

    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "exchange")
    private String exchange;
    @Basic(optional = false)

    @NotNull
    @Size(min = 1, max = 8)
    @Column(name = "ticker")
    private String ticker;
    @Basic(optional = false)

    @NotNull
    @Size(min = 1, max = 12)
    @Column(name = "price")
    private String price;
    @Basic(optional = false)

    @NotNull
    @Size(min = 1, max = 2048)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)

    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "logo")
    private String logo;
    @Basic(optional = false)

    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "analystPrice")
    private String analystPrice;
    @Basic(optional = false)

    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "revenues")
    private String revenues;
    @Basic(optional = false)

    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "sector")
    private String sector;
    @Basic(optional = false)


    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "website")
    private String website;




    /*
    =================================================================
    Class constructors for instantiating a Company entity object to
    represent a row in the Company table in the CompaniesDB database.
    =================================================================
    A constructor method is invoked to instantiate an object from the Company class
     */
    public UserCompany() {
    }

    public UserCompany(User id) {
        this.userId = id;
    }

    public UserCompany(Integer id, User userId, String name, String exchange, String ticker, String price, String description, String logo, String analystPrice, String revenues, String sector, String website) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.price = price;
        this.exchange = exchange;
        this.ticker = ticker;
        this.revenues = revenues;
        this.sector = sector;
        this.website = website;
        this.description = description;
        this.logo=logo;
        this.analystPrice = analystPrice;
    }

    public UserCompany(String name, User userId, String exchange, String ticker, String price, String description, String logo, String analystPrice, String revenues, String sector, String website) {
        this.name = name;
        this.userId = userId;
        this.price = price;
        this.exchange = exchange;
        this.ticker = ticker;
        this.revenues = revenues;
        this.sector = sector;
        this.website = website;
        this.description = description;
        this.logo=logo;
        this.analystPrice = analystPrice;
    }
    /*
    ======================================================
    Getter and Setter methods for the attributes (columns)
    of the Company table in the CompaniesDB database.
    ======================================================
     */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAnalystPrice() {
        return analystPrice;
    }

    public void setAnalystPrice(String analystPrice) {
        this.analystPrice = analystPrice;
    }

    public String getRevenues() {
        return revenues;
    }

    public void setRevenues(String revenues) {
        this.revenues = revenues;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
/*
    ================================
    Instance Methods Used Internally
    ================================
     */

    // Generate and return a hash code value for the object with database primary key id
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /*
     Checks if the Company object identified by 'object' is the same as the Company object identified by 'id'
     Parameter object = Company object identified by 'object'
     Returns True if the Company 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserCompany)) {
            return false;
        }
        UserCompany other = (UserCompany) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }

}
