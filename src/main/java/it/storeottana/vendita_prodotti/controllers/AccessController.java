package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.servicies.AccessService;
import it.storeottana.vendita_prodotti.dto.Access;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/access")
public class AccessController {

    @Autowired
    private AccessService accessService;

    @PostMapping("/login")
    public Object loginAdmin(@RequestBody Access access, HttpServletResponse response) {
        return accessService.login(access.getEmail(), access.getPassword(), response);
    }
    @PatchMapping("/forgotpw")
    public String forgotPW(@RequestParam String email) throws Exception {
        return accessService.forgotPW(email);
    }
    @PatchMapping("/resetpw/{id}")
    public String resetPw(@RequestParam String newPW, @PathVariable long id) throws Exception {
        return accessService.resetPw(newPW, id);
    }

}