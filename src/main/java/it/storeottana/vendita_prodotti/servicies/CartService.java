package it.storeottana.vendita_prodotti.servicies;

import it.storeottana.vendita_prodotti.dto.ShippingData;
import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.OrderPriority;
import it.storeottana.vendita_prodotti.dto.ProductAndquantity;
import it.storeottana.vendita_prodotti.entities.Product;
import it.storeottana.vendita_prodotti.repositories.CartRepo;
import it.storeottana.vendita_prodotti.repositories.ProductRepo;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDateTime;

import java.util.*;

@Service
public class CartService {
    @Autowired
    private TokenJWT tokenJWT;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private ProductRepo productRepo;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredCarts() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        cartRepo.deleteExpiredCarts(oneMonthAgo);
    }

    public String addCart(long idProduct, int quantity, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Optional<Product> productR = productRepo.findById(idProduct);
        productR.orElseThrow(() -> new Exception("Prodotto non trovato"));

        String token = tokenJWT.getTokenFromCookie(request);
        String username = token != null ? tokenJWT.extractUsername(token) : tokenJWT.guestUsername();

        Cart cart = cartRepo.findByUsername(username).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUsername(username);
            newCart.setToken(tokenJWT.createToken(username));
            return newCart;
        });

        if (!isFoundProduct(cart, productR.get())) {
            addProducts(cart, productR.get(), quantity);
        }

        tokenJWT.addTokenToCookie(response, cart.getToken());

        return "Prodotto aggiunto!";
    }
    public boolean addShippingData(HttpServletRequest request, ShippingData shippingData) throws Exception {
        String token = tokenJWT.getTokenFromCookie(request);
        String username = token != null ? tokenJWT.extractUsername(token) : tokenJWT.guestUsername();

        Optional <Cart> cartR = cartRepo.findByUsername(username);
        if (cartR.isEmpty() || cartR.get().getProductAndquantity().isEmpty()) throw new Exception("Carrello o prodotto non trovato!");

        cartR.get().setShippingData(shippingData);
        cartRepo.saveAndFlush(cartR.get());
        return true;
    }
    public void addProducts(Cart cart, Product product, int quantity) {
        ProductAndquantity productAndquantity = new ProductAndquantity(product, quantity);
        cart.addProductsInCart(productAndquantity);
        updateCart(cart);
        cartRepo.saveAndFlush(cart);
    }
    public boolean isFoundProduct(Cart cart, Product product){
        boolean isFound = false;
        for ( ProductAndquantity pic : cart.getProductAndquantity() ){
            if (pic.getProduct() == product){
                pic.setQuantity(pic.getQuantity()+1);

                updateCart(cart);
                cartRepo.saveAndFlush(cart);
                isFound = true;
            }
        }
        return isFound;
    }
    public void updateCart(Cart cart){
        cart.setTotalQuantities(cart.getProductAndquantity().stream().mapToInt(ProductAndquantity::getQuantity).sum());

        double totalCost = 0;
        for (ProductAndquantity pr : cart.getProductAndquantity()){
            totalCost += pr.getProduct().getPrice() * pr.getQuantity();
        }
        cart.setTotalCost(totalCost + cart.getOrderPriority().calculateShippingCost(totalCost));
    }
    public Object getCart(HttpServletRequest request) throws Exception {
        String token = tokenJWT.getTokenFromCookie(request);
        if (token == null) return "Token non trovato!";

        Optional <Cart> cartR = cartRepo.findByUsername(tokenJWT.extractUsername(token));
        cartR.orElseThrow(() -> new Exception("Carrerllo non trovato!"));

        return cartR.get();
    }
    public String emptyCart(HttpServletRequest request) {
        String token = tokenJWT.getTokenFromCookie(request);
        if (token == null) return "Errore di comunicazione!";

        Optional <Cart> cartR = cartRepo.findByUsername(tokenJWT.extractUsername(token));
        cartR.ifPresent(value -> cartRepo.deleteById(value.getId()));

        return "Carrello svuotato!";
    }
    public String deleteProduct(HttpServletRequest request, long idProduct) throws Exception {
        String token = tokenJWT.getTokenFromCookie(request);
        Optional <Product> productR = productRepo.findById(idProduct);
        if (token == null || productR.isEmpty()) throw new Exception("Token o prodotto non trovato!");

        Optional <Cart> cartR = cartRepo.findByUsername(tokenJWT.extractUsername(token));
        if (cartR.isPresent()) {
            boolean removed = cartR.get().getProductAndquantity().removeIf(
                    item -> item.getProduct().getId() == idProduct);
            if (removed) {
                updateCart(cartR.get());
                cartRepo.saveAndFlush(cartR.get());
                return "Rimosso!";
            } else {
                return "Prodotto non presente nel carrello!";
            }
        }
        return "Carrello non trovato!";
    }
    public Object changeDeliveryMethod(HttpServletRequest request, OrderPriority orderPriority){
        String token = tokenJWT.getTokenFromCookie(request);
        Optional <Cart> cartR = cartRepo.findByUsername(tokenJWT.extractUsername(token));
        if (cartR.isPresent()) {
            cartR.get().setOrderPriority(orderPriority);
            updateCart(cartR.get());
            return cartRepo.saveAndFlush(cartR.get());
        }
        return "Errore comunicazione!";
    }
    public Object changeQuantities(long idProduct, int quantity, HttpServletRequest request) {
        String token =  tokenJWT.getTokenFromCookie(request);
        Optional <Cart> cartR = cartRepo.findByUsername(tokenJWT.extractUsername(token));
        Optional <Product> productR = productRepo.findById(idProduct);

        if (cartR.isPresent() && productR.isPresent()) {
            cartR.get().getProductAndquantity().stream()
                    .filter(pc -> pc.getProduct().equals(productR.get()))
                    .forEach(pc -> pc.setQuantity(quantity));
            updateCart(cartR.get());
            return cartRepo.saveAndFlush(cartR.get());
        }
        return "Errore comunicazione!";
    }

}
