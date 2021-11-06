package gr.ntua.ece.softeng18b.model;


import gr.ntua.ece.softeng18b.controller.api.JsonCreator;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sku")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name="model")
    private String name;

    private Double rating;
    private Integer rated;

    @NotNull
    @Size(max = 500)
    private String description;

    @NotNull
    @Size(max = 100)
    private String category;

    @NotNull
    private String tags;

    @NotNull
    private boolean withdrawn;

    @NotNull
    @Column(name="specs")
    private String extraData;

    public Product(String name, Double rating, String description, String category, String tags, boolean withdrawn, String extraData) {
        this.name = name;
        if (rating == null) {
            this.rated = null;
        }
        else {
            this.rated = 1;
        }
        this.rating = rating;
        this.description = description;
        this.category = category;
        this.tags = tags;
        this.withdrawn = withdrawn;
        this.extraData = extraData;
    }

    public Product(){}
    
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

    public Double getRating() {
        return this.rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getRated() {
        return rated;
    }

    public void setRated(Integer rated) {
        this.rated = rated;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String takeTags() {
        return this.tags;
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

    public String takeExtraData() {
        return this.extraData;
    }

    public HashMap<String, String> getExtraData(){
        String[] keyValuePairs = this.extraData.split(",");
        HashMap<String, String> hm = new HashMap<>();
        for(String pair : keyValuePairs){
            String[] keyValuePair = pair.split(":");
            try{
                hm.put(keyValuePair[0].trim(), keyValuePair[1].trim());
            }
            catch(IndexOutOfBoundsException e){
                //hm.put(keyValuePair[0].trim(), " ");
            }
        }
        return hm;
    }

    public JSONObject getJsonExtraData(){
        return JsonCreator.jsonFromString(this.extraData);
    }


    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

}   