package gr.ntua.ece.softeng18b.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class IndexController {

    @RequestMapping("/")
    public String welcome() {
        return "grid";
    }

    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    @RequestMapping("/search")
    public String maps() {
        return "searchresult";
    }

    @RequestMapping("/camera")
    public String camera() {
        return "try_camera";
    }

    @RequestMapping("/mobile")
    public String mobile() {
        return "mobile";
    }

    @RequestMapping("/wearesorry")
    public String wearesorry() {
        return "wearesorry";
    }

    @RequestMapping("/thankyou")
    public String thankyou() {
        return "thankyou";
    }

    @RequestMapping("/mapsearch")
    public String mapsearch() {
        return "map";
    }

    @RequestMapping("/statistics")
    public String statistics() {
        return "stats";
    }

    @RequestMapping("/api")
    public String api() {
        return "api";
    }

    @RequestMapping("/tryP")
    public String tryP() {
        return "try_products";
    }

    @RequestMapping("/tryM")
    public String tryM() {
        return "try_maps";
    }

    @RequestMapping("/product")
    public String product() {
        return "product";
    }

    @RequestMapping("/signup")
    public String signup() {
        return "signup";
    }

    @RequestMapping("/addproduct")
    public String mobileProduct() {
        return "mobileProduct";
    }

    @RequestMapping("/addstore")
    public String addstore() {
        return "mobileAddStore";
    }
}


