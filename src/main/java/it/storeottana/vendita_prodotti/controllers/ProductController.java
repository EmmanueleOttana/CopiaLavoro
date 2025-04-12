package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.entities.Product;
import it.storeottana.vendita_prodotti.servicies.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/insertion")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public Object create(@RequestParam String name, @RequestParam MultipartFile[] files,
                         @RequestParam String title, @RequestParam String description,
                         @RequestParam double price, HttpServletRequest request) throws Exception {
        return productService.create(name, files, title, description, price, request);
    }
    @GetMapping("/getAll")
    public List<Product> getAllInsertion(){
        return productService.getAllProducts();
    }

    @GetMapping("/getTitle")
    public List<Product> getInsertionByTitle(@PathParam("title") String title) {
        return productService.getProductByTitle(title);
    }
    @GetMapping("/getId")
    public Product getInsertionById(@PathParam("id") long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/update")
    public Object update(@RequestParam long idInsertion, @RequestParam MultipartFile[] files,
                         @RequestParam String title, @RequestParam String description,
                         @RequestParam double price, HttpServletRequest request) throws Exception {

        return productService.updateProduct(idInsertion, files, title, description, price, request);
    }
    @PutMapping("/deleteImage")
    public Object deleteImage(@RequestParam long idInsertion, @RequestParam String[] imagesName,
                              HttpServletRequest request) throws Exception {
        return productService.deleteImage(idInsertion, imagesName, request);
    }
    @DeleteMapping("delete/{id}")
    public Object deleteInsertion(@PathVariable long id, HttpServletRequest request){
        return productService.deleteProduct(id, request);
    }
    @DeleteMapping("deleteAll")
    public Object deleteAllInsertion(HttpServletRequest request){
        return productService.deleteAllProducts(request);
    }

}