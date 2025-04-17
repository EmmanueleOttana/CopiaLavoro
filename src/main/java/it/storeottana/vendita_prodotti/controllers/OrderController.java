package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.entities.Order;
import it.storeottana.vendita_prodotti.entities.StateOfOrder;
import it.storeottana.vendita_prodotti.servicies.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/get/{orderNumber}")
    public Order getOrder(@PathVariable String orderNumber) throws Exception {
        return orderService.getOrder(orderNumber);
    }
    @GetMapping("/getAll")
    public List<Order> getAll(HttpServletRequest request) throws Exception {
        return orderService.getAll(request);
    }
    @PatchMapping("/orderState/{orderNumber}")
    public Order changeOrderState(@PathVariable String orderNumber, @RequestParam StateOfOrder stateOfOrder,
                                   HttpServletRequest request) throws Exception {
        return orderService.changeOrderState(orderNumber, stateOfOrder, request);
    }
    @DeleteMapping("/annul/{orderNumber}")
    public String requestCancellation(@PathVariable String orderNumber) throws Exception {
        return orderService.requestCancellation(orderNumber);
    }
    @DeleteMapping("/cancel/{orderNumber}")
    public String orderCancellation(@PathVariable String orderNumber, HttpServletRequest request) throws Exception {
        return orderService.orderCancellation(orderNumber, request);
    }

}
