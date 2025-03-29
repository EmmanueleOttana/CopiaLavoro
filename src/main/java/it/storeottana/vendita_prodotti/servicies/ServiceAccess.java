package it.storeottana.vendita_prodotti.servicies;

import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.repositories.RepoAdmin;
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
public class ServiceAccess {

    @Autowired
    private TokenJWT tokenJWT;
    @Autowired
    private RepoAdmin repoAdmin;
    @Autowired
    private EmailService postman;
    @Value("${urlBackend}")
    private String urlBackend;

    public Object login(String email, String password, HttpServletResponse response) {
        Optional <Admin> admin = repoAdmin.findByEmail(email);
        if (admin.isPresent()) {
            Admin adminDB = admin.get();
            if (adminDB.isActive()) {
                if (EncryptionPw.checkPassword(password, adminDB.getPassword())) {
                    adminDB.setToken(tokenJWT.getToken(adminDB.getUsername()));
                    response.addHeader("Token", adminDB.getToken());
                    adminDB.setTimestampToken(LocalDateTime.now());
                    adminDB.setSuspended(false);
                    repoAdmin.saveAndFlush(adminDB);
                    return true;
                } else return "Errore nelle credenziali d'accesso!";
            } else return "Impossibile effettuare l'accesso!";
        }
        return "Errore nell'inserimento della password e/o l'indirizzo";
    }
    public String forgotPW(String email) throws Exception {
        Optional <Admin> admin = repoAdmin.findByEmail(email);
        if (admin.isPresent()){
            Admin adminDB = admin.get();
            adminDB.setSuspended(true);
            this.postman.sendMail(email, "Reset password",
                    "Per inserire una nuova password clicca nel link sottostante:\n" +
                            urlBackend+"/access/resetpw/"+adminDB.getId());

            repoAdmin.saveAndFlush(adminDB);
            return "É stata inviata un email per resettare la password";
        }else {
            return "É stato tentato l'invio dell'email di reset";
        }
    }
    public String resetPw(String newPW, long id) {
        Optional <Admin> admin = repoAdmin.findById(id);
        if (admin.isEmpty()) return "Errore comunicazione";
        Admin adminDB = admin.get();
        if (adminDB.isSuspended()) {
            adminDB.setPassword(EncryptionPw.hashPassword(newPW));
            repoAdmin.saveAndFlush(adminDB);
            return "Password modificata con successo!";
        }else return "Errore richiesta!";
    }

}