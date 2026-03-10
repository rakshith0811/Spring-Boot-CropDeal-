package com.cropdeal.farmer.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "orders")
public class Orders {

    @Id
    private String orderID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dealerID")
    private Dealer dealer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "farmerID")
    private Farmer farmer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cropID")
    private Crop crop;
    private int quantity;
    private String orderStatus;

    @Transient
    public String getDealerName() {
        return dealer != null && dealer.getUser() != null ? dealer.getUser().getUsername() : null;
    }

    @Transient
    public String getFarmerName() {
        return farmer != null && farmer.getUser() != null ? farmer.getUser().getUsername() : null;
    }

    @Transient
    public String getCropName() {
        return crop != null ? crop.getCropName() : null;
    }

    @Transient
    public String getDealerMobile() {
        return dealer != null && dealer.getUser() != null ? dealer.getUser().getMobileNumber() : null;
    }

    @Transient
    public String getFarmerMobile() {
        return farmer != null && farmer.getUser() != null ? farmer.getUser().getMobileNumber() : null;
    }
}
