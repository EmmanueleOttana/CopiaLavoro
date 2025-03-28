package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.servicies.ServiceAdmin;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class ControllerAdmin {
    @Autowired
    private ServiceAdmin serviceAdmin;

    @PostMapping("/registration")
    public String registrationWorkers(@RequestBody Admin admin) throws Exception {
        return serviceAdmin.registration(admin);
    }
    @GetMapping("/activeAccount/{id}/{activationCode}")
    public String activeAccount(@PathVariable long id, @PathVariable String activationCode){
        return serviceAdmin.activeAccount(id, activationCode);
    }
    @GetMapping("/myProfile")
    public Object getMyProfile(HttpServletRequest request){
        return serviceAdmin.getMyProfile(request);
    }
    @PatchMapping("/updateEmail")
    public String updateEmail(HttpServletRequest request, @RequestParam String newEmail){
        return serviceAdmin.updateEmail(request, newEmail);
    }
    @PatchMapping("/newEmail/{id}/{newEmail}")
    public String acceptNewEmail(@PathVariable long id, @PathVariable String newEmail){
        return serviceAdmin.acceptNewEmail(id, newEmail);
    }
    @PatchMapping("/updTelNum")
    public String updateTelephoneNum(HttpServletRequest request, @RequestParam String telephoneNumber) throws Exception {
        return serviceAdmin.updateTelephoneNumber(request, telephoneNumber);
    }
    @PatchMapping("/newTelNum/{id}/{newEmail}")
    public Object acceptTelephoneNum(@PathVariable long id, @PathVariable String telephoneNumber){
        return serviceAdmin.acceptNewTelephoneNumber(id, telephoneNumber);
    }
    /*
    @PatchMapping("/acceptance/{idWorker}")
    public boolean acceptanceWorker(@PathVariable long idWorker){
        return workersService.acceptanceWorker(idWorker);
    }
     */
}