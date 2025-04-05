package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.servicies.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/get/{orderNumber}")
    public Object getOrder(@PathVariable String orderNumber){
        return orderService.getOrder(orderNumber);
    }
    @GetMapping("/getAll")
    public Object getAll(HttpServletRequest request){
        return orderService.getAll(request);
    }
    @DeleteMapping("/annul/{orderNumber}")
    public String requestCancellation(@PathVariable String orderNumber){
        return orderService.requestCancellation(orderNumber);
    }
    @DeleteMapping("/cancel/{orderNumber}")
    public Object orderCancellation(@PathVariable String orderNumber, HttpServletRequest request){
        return orderService.orderCancellation(orderNumber, request);
    }

}
