/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.EntityBeans;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/*
The @Entity annotation designates this class as a JPA Entity POJO class
 */
@Entity

// Name of the database table represented
@Table(name = "CryptoCurrency")

@NamedQueries({
        @NamedQuery(name = "CryptoCurrency.findAll", query = "SELECT u FROM CryptoCurrency u")
        , @NamedQuery(name = "CryptoCurrency.findByName", query = "SELECT u FROM CryptoCurrency u WHERE u.name = :name")
        , @NamedQuery(name = "CryptoCurrency.findBySymbol", query = "SELECT u FROM CryptoCurrency u WHERE u.symbol = :symbol")
        , @NamedQuery(name = "CryptoCurrency.findByObtainmentMethod", query = "SELECT u FROM CryptoCurrency u WHERE u.obtainmentMethod = :obtainmentMethod")
        , @NamedQuery(name = "CryptoCurrency.findByWebsiteLink", query = "SELECT u FROM CryptoCurrency u WHERE u.websiteLink = :websiteLink")
        , @NamedQuery(name = "CryptoCurrency.findByPrice", query = "SELECT u FROM CryptoCurrency u WHERE u.price = :price")
        , @NamedQuery(name = "CryptoCurrency.findByCoinID", query = "SELECT u FROM CryptoCurrency u WHERE u.coinID = :coinID")
        , @NamedQuery(name = "CryptoCurrency.findByDescription", query = "SELECT u FROM CryptoCurrency u WHERE u.description = :description")
        , @NamedQuery(name = "CryptoCurrency.findByReleaseDate", query = "SELECT u FROM CryptoCurrency u WHERE u.releaseDate = :releaseDate")
        , @NamedQuery(name = "CryptoCurrency.findByLastUpdatedDate", query = "SELECT u FROM CryptoCurrency u WHERE u.lastUpdatedDate = :lastUpdatedDate")
})

public class CryptoCurrency implements Serializable {
    /*
    ========================================================
    Instance variables representing the attributes (columns)

    CREATE TABLE CryptoCurrency
    (
      id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
      name VARCHAR(64) NOT NULL,
      symbol VARCHAR(10) NOT NULL,
      obtainment_method VARCHAR(32) NOT NULL,
      website_link VARCHAR(128) NOT NULL,
      price BIGINT NOT NULL,
      coin_id INT NOT NULL,
      description VARCHAR(1028) NOT NULL,
      release_date DATE NOT NULL,
      last_updated_date DATE NOT NULL
    );
    ========================================================
     */
    private static final long serialVersionUID = 1L;
    /*
    Primary Key id is auto generated by the system as an Integer value
    starting with 1 and incremented by 1, i.e., 1,2,3,...
    A deleted entity object's primary key number is not reused.
     */
    // id INT UNSIGNED NOT NULL AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    // name VARCHAR(64) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "name")
    private String name;

    // symbol VARCHAR(10) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "symbol")
    private String symbol;

    // obtainment_method VARCHAR(32) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "obtainment_method")
    private String obtainmentMethod;

    // website_link VARCHAR(128) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "website_link")
    private String websiteLink;

    // price BIGINT NOT NULL
    @Basic(optional = false)
    @NotNull
    @Column(name = "price")
    private Float price;

    // coin_id INT NOT NULL
    @Basic(optional = false)
    @NotNull
    @Column(name = "coin_id")
    private Integer coinID;

    // description VARCHAR(256) NOT NULL
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1028)
    @Column(name = "description")
    private String description;

    // release_date DATE NOT NULL
    // Date released is recorded in the database as YYYY-MM-DD so that it is sortable
    @Basic(optional = false)
    @NotNull
    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    // last_updated_date DATE NOT NULL
    // Last updated date is recorded in the database as YYYY-MM-DD so that it is sortable
    @Basic(optional = false)
    @NotNull
    @Column(name = "last_updated_date")
    @Temporal(TemporalType.DATE)
    private Date lastUpdatedDate;


    /*
    ===============================================================
    Class constructors for instantiating a User entity object to
    ===============================================================
     */

    // Used in createAccount method in UserController
    public CryptoCurrency() {
    }

    // Not used but kept for potential future use
    public CryptoCurrency(Integer id) {
        this.id = id;
    }

    // Not used but kept for potential future use
    public CryptoCurrency(Integer id, String name, String symbol, String obtainmentMethod, String websiteLink,
                          Float price, Integer coinID, String description, Date releaseDate, Date lastUpdatedDate) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.obtainmentMethod = obtainmentMethod;
        this.websiteLink = websiteLink;
        this.price = price;
        this.coinID = coinID;
        this.description = description;
        this.releaseDate = releaseDate;
        this.lastUpdatedDate = lastUpdatedDate;
    }

    /*
    ======================================================
    Getter and Setter methods for the attributes (columns)
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getObtainmentMethod() {
        return obtainmentMethod;
    }

    public void setObtainmentMethod(String obtainmentMethod) {
        this.obtainmentMethod = obtainmentMethod;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getCoinID() {
        return coinID;
    }

    public void setCoinID(Integer coinID) {
        this.coinID = coinID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
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
     Checks if the User object identified by 'object' is the same as the User object identified by 'id'
     Parameter object = User object identified by 'object'
     Returns True if the User 'object' and 'id' are the same; otherwise, return False
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        CryptoCurrency other = (CryptoCurrency) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    // Return String representation of database primary key id
    @Override
    public String toString() {
        return id.toString();
    }

}
