package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.DeliveryMethods;
import it.storeottana.vendita_prodotti.servicies.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add/{idProduct}/{quantity}")
    public String addCart(@PathVariable long idProduct, @PathVariable int quantity,
                          HttpServletRequest request, HttpServletResponse response){
        return cartService.addCart(idProduct, quantity, request, response);
    }
    @GetMapping("/get")
    public Object getCart(HttpServletRequest request){
        return cartService.getCart(request);
    }
    @DeleteMapping("/empty")
    public String emptyCart(HttpServletRequest request){
        return cartService.emptyCart(request);
    }
    @DeleteMapping("/delete/{idProduct}")
    public String deleteProduct(HttpServletRequest request, @PathVariable long idProduct){
        return cartService.deleteProduct(request, idProduct);
    }
    @PatchMapping("/delivery")
    public Object changeDeliveryMethod(HttpServletRequest request, @RequestParam DeliveryMethods deliveryMethods){
        return cartService.changeDeliveryMethod(request, deliveryMethods);
    }
    @PatchMapping("/{idProduct}/{quantity}")
    public Object changeQuantities(@PathVariable long idProduct, @PathVariable int quantity,
                                       HttpServletRequest request){
        return cartService.changeQuantities(idProduct, quantity, request);
    }


}