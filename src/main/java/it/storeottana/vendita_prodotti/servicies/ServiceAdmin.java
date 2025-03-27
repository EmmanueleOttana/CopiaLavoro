package it.storeottana.vendita_prodotti.servicies;

import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.repositories.RepoAdmin;
import it.storeottana.vendita_prodotti.security.EncryptionPw;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import it.storeottana.vendita_prodotti.utils.GMailer;
import it.storeottana.vendita_prodotti.utils.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceAdmin {
    @Autowired
    private TokenJWT tokenJWT;
    @Autowired
    private RepoAdmin repoAdmin;
    @Autowired
    private GMailer postman;
    @Autowired
    private SmsService smsService;

    public String registration(Admin admin) throws Exception {

        Admin adminNew = new Admin(admin.getUsername(), admin.getEmail(),
                admin.getTelephoneNumber(), EncryptionPw.hashPassword(admin.getPassword()));
        repoAdmin.saveAndFlush(adminNew);

        this.postman.sendMail(adminNew.getEmail(),"Attivazione account",
                "Per attivare l'account cliccare nel link a seguire:\n" +
                "http://localhost:8080/admin/activeAccount/"+adminNew.getId()+"/"+adminNew.getActivationCode());

        return "L'utente "+ adminNew.getUsername() + " è stato creato con successo!" +
                "\nConfermare l'indirizzo email per attivarlo";
    }

    public String activeAccount(long id, String activationCode){
        Optional <Admin> admin1 = repoAdmin.findById(id);
        if (admin1.isEmpty()) return "Errore comunicazione";

        Admin admin = admin1.get();
        if (admin.getActivationCode().equals(activationCode)){
            admin.setActivationCode(null);

            repoAdmin.saveAndFlush(admin);
            return "Registrazione effettuata con successo!";
        }else {
            return "Impossibile attivare account.";
        }
    }

    public Object getMyProfile(HttpServletRequest request){
        Optional <Admin> adminJwt = findAdminByRequest(request);
        if (adminJwt.isPresent()) {
            return adminJwt.get();
        }else return "Pagina non trovata!";
    }

    public String updateEmail(HttpServletRequest request, String newEmail) {
        Optional <Admin> adminJwt = findAdminByRequest(request);
        if (adminJwt.isEmpty()) return "Errore comunicazione";

        Admin admin = adminJwt.get();
        if (!newEmail.isEmpty()){
            this.smsService.sendSms(admin.getTelephoneNumber(),
                "Per modificare l'email cliccare nel link a seguire:\n" +
                        "http://localhost:8080/human/newEmail/"+admin.getId()+'/'+newEmail);

            return "Le è stato inviato un sms di conferma, cliccare sul link fornito per sms " +
                    "per confermare il nuovo indirizzo email";
        }else return "Impossibile inserire questa email!";
    }

    public String acceptNewEmail(long id, String newEmail) {
        Optional <Admin> adminJwt = repoAdmin.findById(id);
        if (adminJwt.isEmpty()) return "Errore comunicazione";

        Admin admin = adminJwt.get();
        admin.setEmail(newEmail);
        repoAdmin.saveAndFlush(admin);

        return "Email modificata con successo!";
    }

    public String updateTelephoneNumber(HttpServletRequest request, String telephoneNumber) throws Exception {
        Optional <Admin> adminJwt = findAdminByRequest(request);
        if (adminJwt.isEmpty()) return "Errore comunicazione";

        Admin admin = adminJwt.get();
        if (!telephoneNumber.isEmpty()){
            this.postman.sendMail(admin.getEmail(),"Cambio numero di telefono",
                "Per modificare il numero di telefono cliccare nel link a sottostante:\n" +
                        "http://localhost:8080/human/newTelNum/"+admin.getId()+"/"+telephoneNumber);

            return "Le è stato inviato un sms di conferma, cliccare sul link fornito per sms " +
                    "per confermare il nuovo indirizzo email";
        }else return "Impossibile inserire questa email!";
    }

    public Object acceptNewTelephoneNumber(long id, String newTelephoneNumber) {
        Optional <Admin> adminJwt = repoAdmin.findById(id);
        if (adminJwt.isEmpty()) return "Errore comunicazione";

        Admin admin = adminJwt.get();
        admin.setTelephoneNumber(newTelephoneNumber);
        return repoAdmin.saveAndFlush(admin);
    }

    public Optional <Admin> findAdminByRequest(HttpServletRequest request){
        return repoAdmin.findByUsername(tokenJWT.getUsername(request.getHeader("Token")));
    }
}