package eu.adamzrcek.endpointmonitoringapi.models;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class MonitoredEndpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private String url;

    private Date dateOfCreation;

    private Date dateOfLastCheck;

    @ManyToOne(optional = false)
    private User owner;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Date getDateOfLastCheck() {
        return dateOfLastCheck;
    }

    public void setDateOfLastCheck(Date dateOfLastCheck) {
        this.dateOfLastCheck = dateOfLastCheck;
    }

    @Override
    public String toString() {
        return "MonitoredEndpoint{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", dateOfLastCheck=" + dateOfLastCheck +
                ", owner=" + owner +
                '}';
    }
}
