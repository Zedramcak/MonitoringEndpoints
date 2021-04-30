package eu.adamzrcek.endpointmonitoringapi.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class MonitoredEndpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NonNull
    private String name;

    private String url;

    @Column(columnDefinition = "DateTime")
    private Timestamp dateOfCreation;

    @Column(columnDefinition = "DateTime")
    private Timestamp dateOfLastCheck;

    private int monitoredInterval;

    @JsonIgnore
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

    public Timestamp getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Timestamp dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Timestamp getDateOfLastCheck() {
        return dateOfLastCheck;
    }

    public void setDateOfLastCheck(Timestamp dateOfLastCheck) {
        this.dateOfLastCheck = dateOfLastCheck;
    }

    public int getMonitoredInterval() {
        return monitoredInterval;
    }

    public void setMonitoredInterval(int monitoredInterval) {
        this.monitoredInterval = monitoredInterval;
    }

    @Override
    public String toString() {
        return "MonitoredEndpoint{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", dateOfLastCheck=" + dateOfLastCheck +
                ", monitoredInterval=" + monitoredInterval +
                '}';
    }
}
