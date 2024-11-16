package com.rentwheelz.rentwheelz.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company_name;  
    private String number_plate;   
    private String model;
    private String type;
    private String fuel;
    private String image_url;      
    private String capacity;    
    private String price_per_day;   
    private String manufacturing_year; 
    private String rating; 


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getcompany_name() {
        return company_name;
    }
    public void setcompany_name(String company_name) {
        this.company_name = company_name;
    }


    public String getnumber_plate() {
        return number_plate;
    }
    public void setnumber_plate(String number_plate) {
        this.number_plate = number_plate;
    }


    public String getmodel() {
        return model;
    }
    public void setmodel(String model) {
        this.model = model;
    }


    public String getimage_url() {
        return image_url;
    }
    public void setimage_url(String image_url) {
        this.image_url = image_url;
    }


    public String getcapacity() {
        return capacity;
    }
    public void setcapacity(String capacity) {
        this.capacity = capacity;
    }


    public String getprice_per_day() {
        return price_per_day;
    }
    public void setprice_per_day(String price_per_day) {
        this.price_per_day = price_per_day;
    }
    

    public String getmanufacturing_year() {
        return manufacturing_year;
    }
    public void setmanufacturing_year(String manufacturing_year) {
        this.manufacturing_year = manufacturing_year;
    }


    public String getrating() {
        return rating;
    }
    public void setrating(String rating) {
        this.rating = rating;
    }


    public String gettype() {
        return type;
    }
    public void settype(String type) {
        this.type = type;
    }


    public String getfuel() {
        return fuel;
    }
    public void setfuel(String fuel) {
        this.fuel = fuel;
    }
}