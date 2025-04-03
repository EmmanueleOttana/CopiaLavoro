package it.storeottana.vendita_prodotti.servicies;

import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.DeliveryMethods;
import it.storeottana.vendita_prodotti.entities.ProductInCart;
import it.storeottana.vendita_prodotti.entities.Product;
import it.storeottana.vendita_prodotti.repositories.ProductInCartRepo;
import it.storeottana.vendita_prodotti.repositories.CartRepo;
import it.storeottana.vendita_prodotti.repositories.ProductRepo;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
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
    @Autowired
    private ProductInCartRepo productInCartRepo;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredCarts() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        cartRepo.deleteExpiredCarts(oneWeekAgo);
    }

    public String addCart(long idProduct, int quantity, HttpServletRequest request, HttpServletResponse response) {
        Optional<Product> productR = productRepo.findById(idProduct);
        if (productR.isEmpty()) return "Errore di comunicazione!";

        String token = getTokenFromCookie(request);
        String username = token != null ? tokenJWT.getUsername(token) : tokenJWT.guestUsername();

        Cart cart = cartRepo.findByUsername(username).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUsername(username);
            newCart.setToken(tokenJWT.getToken(username));
            return newCart;
        });

        if (!isFoundProduct(cart, productR.get())) {
            addProducts(cart, productR.get(), quantity);
        }

        // Assicura che il token sia sempre aggiornato nel cookie
        addTokenToCookie(response, cart.getToken());

        return "Prodotto aggiunto!";
    }

    public void addProducts(Cart cart, Product product, int quantity) {
        ProductInCart productInCart = new ProductInCart(cart, product, quantity);
        cart.addProductsInCart(productInCart);
        updateCart(cart);
        cartRepo.saveAndFlush(cart);
    }
    public boolean isFoundProduct(Cart cart, Product product){
        boolean isFound = false;
        for ( ProductInCart p : cart.getProductsInCart() ){
            if (p.getProduct() == product){
                p.setQuantity(p.getQuantity()+1);

                updateCart(cart);
                productInCartRepo.saveAndFlush(p);
                isFound = true;
            }
        }
        return isFound;
    }
    public void updateCart(Cart cart){
        cart.setTotalQuantities(cart.getProductsInCart().stream().mapToInt(ProductInCart::getQuantity).sum());

        double totalCost = 0;
        for (ProductInCart pr : cart.getProductsInCart()){
            totalCost += pr.getProduct().getPrice() * pr.getQuantity();
        }
        cart.setTotalCost(totalCost + cart.getDeliveryMethods().calculateShippingCost(totalCost));
    }
    public Object getCart(HttpServletRequest request){
        String token = getTokenFromCookie(request);
        if (token == null) return "Carrello non trovato!";

        Optional <Cart> cartR = cartRepo.findByUsername(tokenJWT.getUsername(token));
        if (cartR.isEmpty()) return "Carrello vuoto!";

        return cartR.get();
    }
    public String emptyCart(HttpServletRequest request) {
        String token = getTokenFromCookie(request);
        if (token == null) return "Errore di comunicazione!";

        Optional <Cart> cartR = cartRepo.findByUsername(tokenJWT.getUsername(token));
        cartR.ifPresent(value -> cartRepo.deleteById(value.getId()));

        return "Carrello svuotato!";
    }
    public String deleteProduct(HttpServletRequest request, long idProduct) {
        String token = getTokenFromCookie(request);
        Optional <Product> productR = productRepo.findById(idProduct);
        if (token == null || productR.isEmpty()) return "Errore di comunicazione!";

        Optional <Cart> cartR = cartRepo.findByUsername(tokenJWT.getUsername(token));
        if (cartR.isPresent()) {
            productInCartRepo.deleteByCartAndProduct(cartR.get(), productR.get());
            updateCart(cartR.get());
            cartRepo.saveAndFlush(cartR.get());

            return "Rimosso!";
        }
        return "Carrello non trovato!";
    }
    public Object changeDeliveryMethod(HttpServletRequest request, DeliveryMethods deliveryMethods){
        String token = getTokenFromCookie(request);
        Optional <Cart> cartR = cartRepo.findByUsername(tokenJWT.getUsername(token));
        if (cartR.isPresent()) {
            cartR.get().setDeliveryMethods(deliveryMethods);
            updateCart(cartR.get());
            return cartRepo.saveAndFlush(cartR.get());
        }
        return "Errore comunicazione!";
    }
    public void addTokenToCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("guest777token", token);
        //cookie.setHttpOnly(true);  // Impedisce l'accesso da JavaScript per sicurezza
        cookie.setPath("/");       // Il cookie è accessibile da tutto il sito
        cookie.setMaxAge(60 * 60 * 24 * 6); // Valido per 6 giorni
        //cookie.setSecure(true);// Il cookie sarà inviato solo su connessioni HTTPS
        cookie.setDomain(".storeottana.it");
        response.addCookie(cookie); // Aggiunge il cookie alla risposta
    }
    public String getTokenFromCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String token = null;
        //Controlla l'esistenza del carrello
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("guest777token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        return token;
    }

}
