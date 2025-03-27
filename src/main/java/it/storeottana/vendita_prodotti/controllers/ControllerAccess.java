package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.servicies.ServiceAccess;
import it.storeottana.vendita_prodotti.entities.Access;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/access")
public class ControllerAccess {

    @Autowired
    private ServiceAccess serviceAccess;

    @PostMapping("/login")
    public Object loginAdmin(@RequestBody Access access, HttpServletResponse response) {
        return serviceAccess.login(access.getEmail(), access.getPassword(), response);
    }
    @PatchMapping("/forgotpw")
    public String forgotPW(@RequestParam String email) throws Exception {
        return serviceAccess.forgotPW(email);
    }
    @PatchMapping("/resetpw/{id}")
    public String resetPw(@RequestParam String newPW, @PathVariable long id) {
        return serviceAccess.resetPw(newPW, id);
    }

}