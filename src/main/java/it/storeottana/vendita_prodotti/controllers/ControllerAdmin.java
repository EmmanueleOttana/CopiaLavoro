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
    private ServiceAdmin workersService;

    @PostMapping("/registration")
    public String registrationWorkers(@RequestBody Admin admin) throws Exception {
        return workersService.registration(admin);
    }
    @GetMapping("/activeAccount/{id}/{activationCode}")
    public String activeAccount(@PathVariable long id, @PathVariable String activationCode){
        return workersService.activeAccount(id, activationCode);
    }
    @GetMapping("/myProfile")
    public Object getMyProfile(HttpServletRequest request){
        return workersService.getMyProfile(request);
    }

    @PatchMapping("/updateEmail")
    public String updateEmail(HttpServletRequest request, @RequestParam String newEmail){
        return workersService.updateEmail(request, newEmail);
    }
    @PatchMapping("/newEmail/{id}/{newEmail}")
    public String acceptNewEmail(@PathVariable long id, @PathVariable String newEmail){
        return workersService.acceptNewEmail(id, newEmail);
    }
    @PatchMapping("/updTelNum")
    public String updateTelephoneNum(HttpServletRequest request, @RequestParam String telephoneNumber) throws Exception {
        return workersService.updateTelephoneNumber(request, telephoneNumber);
    }
    @PatchMapping("/newTelNum/{id}/{newEmail}")
    public Object acceptTelephoneNum(@PathVariable long id, @PathVariable String telephoneNumber){
        return workersService.acceptNewTelephoneNumber(id, telephoneNumber);
    }
    /*
    @PatchMapping("/acceptance/{idWorker}")
    public boolean acceptanceWorker(@PathVariable long idWorker){
        return workersService.acceptanceWorker(idWorker);
    }
     */
}