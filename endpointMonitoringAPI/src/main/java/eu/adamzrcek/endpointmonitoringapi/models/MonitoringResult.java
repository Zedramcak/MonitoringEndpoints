package eu.adamzrcek.endpointmonitoringapi.models;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class MonitoringResult {
    @Id
    @GeneratedValue
    private int id;

    private Date dateOfCheck;

    private int statusCode;

    private String returnedPayload;

    @ManyToOne(optional = false)
    private MonitoredEndpoint monitoredEndpoint;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateOfCheck() {
        return dateOfCheck;
    }

    public void setDateOfCheck(Date dateOfCheck) {
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
