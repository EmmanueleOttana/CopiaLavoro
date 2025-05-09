package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.dto.AdminRequest;
import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.servicies.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/registration")
    public String registrationWorkers(@RequestBody AdminRequest adminRequest) throws Exception {
        return adminService.registration(adminRequest);
    }
    @PatchMapping("/activeAccount/{id}/{activationCode}")
    public String activeAccount(@PathVariable long id, @PathVariable String activationCode) throws Exception {
        return adminService.activeAccount(id, activationCode);
    }
    @PatchMapping("/acceptance/{id}")
    public boolean acceptanceWorker(@PathVariable long id){
        return adminService.acceptanceWorker(id);
    }

    @GetMapping("/myProfile")
    public Object getMyProfile(HttpServletRequest request){
        return adminService.getMyProfile(request);
    }

    @PatchMapping("/updateEmail")
    public String updateEmail(HttpServletRequest request, @RequestParam String newEmail) throws Exception {
        return adminService.updateEmail(request, newEmail);
    }
    @PatchMapping("/newEmail/{id}/{newEmail}")
    public String acceptNewEmail(@PathVariable long id, @PathVariable String newEmail) throws Exception {
        return adminService.acceptNewEmail(id, newEmail);
    }
    @PatchMapping("/updTelNum")
    public String updateTelephoneNum(HttpServletRequest request, @RequestParam String telephoneNumber) throws Exception {
        return adminService.updateTelephoneNumber(request, telephoneNumber);
    }
    @PatchMapping("/new-tel-num/{id}/{telephoneNumber}")
    public Admin acceptTelephoneNum(@PathVariable long id, @PathVariable String telephoneNumber) throws Exception {
        return adminService.acceptNewTelephoneNumber(id, telephoneNumber);
    }
    @PatchMapping("/updatePassword")
    public String updatePassword(HttpServletRequest request,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword) throws Exception {
        return adminService.updatePassword(request, oldPassword, newPassword);
    }
    @GetMapping("/getAll")
    public List<Admin> getAll(HttpServletRequest request) throws Exception {
        return adminService.getAll(request);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteAdmin(@PathVariable long id, @RequestParam String bossCode, HttpServletRequest request) throws Exception {
        return adminService.deleteAdmin(id, bossCode, request);
    }

    @DeleteMapping("/logout")
    public String logoutAdmin(HttpServletRequest request) throws Exception {
        return adminService.logoutAdmin(request);
    }

}