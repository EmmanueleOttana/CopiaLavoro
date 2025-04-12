package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.entities.StateOfOrder;
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
    @PatchMapping("/orderState/{orderNumber}")
    public Object changeOrderState(@PathVariable String orderNumber, @RequestParam StateOfOrder stateOfOrder,
                                   HttpServletRequest request){
        return orderService.changeOrderState(orderNumber, stateOfOrder, request);
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
