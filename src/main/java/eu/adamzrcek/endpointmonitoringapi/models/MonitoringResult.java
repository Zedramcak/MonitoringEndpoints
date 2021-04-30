package eu.adamzrcek.endpointmonitoringapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class MonitoringResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(columnDefinition = "DateTime")
    private Timestamp dateOfCheck;

    private int statusCode;

    @Column(columnDefinition = "TEXT")
    private String returnedPayload;

    @JsonIgnore
    @ManyToOne(optional = false)
    private MonitoredEndpoint monitoredEndpoint;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDateOfCheck() {
        return dateOfCheck;
    }

    public void setDateOfCheck(Timestamp dateOfCheck) {
        this.dateOfCheck = dateOfCheck;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReturnedPayload() {
        return returnedPayload;
    }

    public void setReturnedPayload(String returnedPayload) {
        this.returnedPayload = returnedPayload;
    }

    public MonitoredEndpoint getMonitoredEndpoint() {
        return monitoredEndpoint;
    }

    public void setMonitoredEndpoint(MonitoredEndpoint monitoredEndpoint) {
        this.monitoredEndpoint = monitoredEndpoint;
    }

    @Override
    public String toString() {
        return "MonitoringResult{" +
                "id=" + id +
                ", dateOfCheck=" + dateOfCheck +
                ", statusCode=" + statusCode +
                ", returnedPayload='" + returnedPayload + '\'' +
                ", monitoredEndpoint=" + monitoredEndpoint +
                '}';
    }
}
