package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.dto.ShippingData;
import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.OrderPriority;
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
                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        return cartService.addCart(idProduct, quantity, request, response);
    }
    @PatchMapping("/addShippingData")
    public boolean addShippingData(HttpServletRequest request, @RequestBody ShippingData shippingData) throws Exception {
        return cartService.addShippingData(request, shippingData);
    }
    @GetMapping("/get")
    public Cart getCart(HttpServletRequest request, HttpServletResponse response) {
        return cartService.getCart(request, response);
    }

    @DeleteMapping("/empty")
    public String emptyCart(HttpServletRequest request){
        return cartService.emptyCart(request);
    }

    @DeleteMapping("/delete/{idProduct}")
    public String deleteProduct(HttpServletRequest request, @PathVariable long idProduct) throws Exception {
        return cartService.deleteProduct(request, idProduct);
    }
    @PatchMapping("/delivery")
    public Object changeDeliveryMethod(HttpServletRequest request, @RequestParam OrderPriority orderPriority){
        return cartService.changeDeliveryMethod(request, orderPriority);
    }
    @PatchMapping("/updateQuantity")
    public Object changeQuantities(@RequestParam long idProduct, @RequestParam int quantity,
                                       HttpServletRequest request){
        return cartService.changeQuantities(idProduct, quantity, request);
    }


}