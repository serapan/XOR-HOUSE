package gr.ntua.ece.softeng18b.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="store_id")
    private Long id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    @Size(max = 100)
    private String address;

    @NotNull
    private String tags;

    @NotNull
    private boolean withdrawn;

    @NotNull
    private Double lng;

    @NotNull
    private Double lat;

    @NotNull
    @Size(max = 100)
    private String area;

    @NotNull
    @Size(max = 100)
    private String postalCode;

    public Store(String name, String address, String tags, boolean withdrawn, Double lng, Double lat, String area, String postalCode) {
        this.name = name;
        this.address = address;
        this.tags = tags;
        this.withdrawn = withdrawn;
        this.lng = lng;
        this.lat = lat;
        this.area = area;
        this.postalCode = postalCode;
    }

    public Store(){

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getTags(){
        List<String> myList = new ArrayList<>(Arrays.asList(this.tags.split(",")));
        return myList;
    }

    public List<String> getTags2(){
        List<String> myList = new ArrayList<>();
        for (String i: this.tags.split(",")) {
            myList.add(i.trim());
        }
        return myList;
    }


    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean getWithdrawn() {
        return this.withdrawn;
    }

    public void setWithdrawn(boolean withdrawn) {
        this.withdrawn = withdrawn;
    }

    public Double getLng() {
        return this.lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}   