package it.storeottana.vendita_prodotti.servicies;

import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.repositories.AdminRepo;
import it.storeottana.vendita_prodotti.security.EncryptionPw;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import it.storeottana.vendita_prodotti.utils.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccessService {

    @Autowired
    private TokenJWT tokenJWT;
    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private EmailService postman;
    @Value("${urlSiteWeb}")
    private String urlSiteWeb;

    public Object login(String email, String password, HttpServletResponse response) {
        Optional <Admin> admin = adminRepo.findByEmail(email);
        if (admin.isPresent()) {
            Admin adminDB = admin.get();
            if (adminDB.isActive()) {
                if (EncryptionPw.checkPassword(password, adminDB.getPassword())) {
                    adminDB.setToken(tokenJWT.createToken(adminDB.getUsername()));
                    response.addHeader("BearerToken", adminDB.getToken());
                    adminDB.setTimestampToken(LocalDateTime.now());
                    adminDB.setSuspended(false);
                    adminRepo.saveAndFlush(adminDB);
                    return true;
                } else return "Errore nelle credenziali d'accesso!";
            } else return "Impossibile effettuare l'accesso!";
        }
        return "Errore nell'inserimento della password e/o l'indirizzo";
    }
    public String forgotPW(String email) throws Exception {
        Optional <Admin> admin = adminRepo.findByEmail(email);
        if (admin.isPresent()){
            Admin adminDB = admin.get();
            adminDB.setSuspended(true);
            this.postman.sendMail(email, "Reset password",
                    "Per inserire una nuova password clicca nel link sottostante:\n" +
                            urlSiteWeb +"/access/resetpw/"+adminDB.getId());

            adminRepo.saveAndFlush(adminDB);
            return "É stata inviata un email per resettare la password";
        }else {
            return "É stato tentato l'invio dell'email di reset";
        }
    }
    public String resetPw(String newPW, long id) {
        Optional <Admin> admin = adminRepo.findById(id);
        if (admin.isEmpty()) return "Errore comunicazione";
        Admin adminDB = admin.get();
        if (adminDB.isSuspended()) {
            adminDB.setPassword(EncryptionPw.hashPassword(newPW));
            adminRepo.saveAndFlush(adminDB);
            return "Password modificata con successo!";
        }else return "Errore richiesta!";
    }

}