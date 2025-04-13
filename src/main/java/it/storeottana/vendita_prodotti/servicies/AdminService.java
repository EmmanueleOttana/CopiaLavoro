package it.storeottana.vendita_prodotti.servicies;

import it.storeottana.vendita_prodotti.dto.AdminRequest;
import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.repositories.AdminRepo;
import it.storeottana.vendita_prodotti.security.EncryptionPw;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import it.storeottana.vendita_prodotti.utils.EmailService;
import it.storeottana.vendita_prodotti.utils.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private TokenJWT tokenJWT;
    @Autowired
    private AdminRepo adminRepo;
    @Autowired
    private EmailService postman;
    @Autowired
    private SmsService smsService;
    @Value("${authCode}")
    private String authCode;
    @Value("${urlBackend}")
    private String urlBackend;
    @Value("${companyEmail}")
    private String companyEmail;

    public String registration(AdminRequest adminRequest) throws Exception {
        if (!adminRequest.getAuthCode().equals(this.authCode)) return "Richiesta inviata! Attendi che ti venga consentito l'accesso.";

        Admin adminNew = new Admin(adminRequest.getUsername(), adminRequest.getEmail(),
                adminRequest.getTelephoneNumber(), EncryptionPw.hashPassword(adminRequest.getPassword()));
        adminRepo.saveAndFlush(adminNew);

        this.postman.sendMail(adminNew.getEmail(),"Attivazione account",
                "Per inviare la richiesta d'attivazione dell'account cliccare nel link a seguire:\n" +
                        urlBackend+"/admin/activeAccount/"+adminNew.getId()+"/"+adminNew.getActivationCode());

        return "L'utente "+ adminNew.getUsername() + " è stato creato con successo!" +
                "\nConfermare l'indirizzo email per attivarlo";
    }

    public String activeAccount(long id, String activationCode){
        Optional <Admin> admin1 = adminRepo.findById(id);
        if (admin1.isEmpty()) return "Errore comunicazione";

        Admin admin = admin1.get();
        if (admin.getActivationCode().equals(activationCode)){
            admin.setActivationCode(null);
            adminRepo.saveAndFlush(admin);

            this.postman.sendMail(companyEmail,"Richiesta d'attivazione account",
                    "Per attivare l'account di "+admin.getUsername()+" cliccare nel link a seguire:\n" +
                            urlBackend+"/admin/acceptance/"+admin.getId());

            return "Registrazione effettuata con successo!";
        }else {
            return "Impossibile attivare account.";
        }
    }
    public boolean acceptanceWorker(long id){
        Optional<Admin> adminDB = adminRepo.findById(id);
        if (adminDB.isPresent()){
            adminDB.get().setActive(true);
            adminRepo.saveAndFlush(adminDB.get());
            return true;
        }else return false;
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
        Optional <Admin> adminJwt = adminRepo.findById(id);
        if (adminJwt.isEmpty()) return "Errore comunicazione";

        Admin admin = adminJwt.get();
        admin.setEmail(newEmail);
        adminRepo.saveAndFlush(admin);

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
        Optional <Admin> adminJwt = adminRepo.findById(id);
        if (adminJwt.isEmpty()) return "Errore comunicazione";

        Admin admin = adminJwt.get();
        admin.setTelephoneNumber(newTelephoneNumber);
        return adminRepo.saveAndFlush(admin);
    }

    public Optional <Admin> findAdminByRequest(HttpServletRequest request){
        return adminRepo.findByUsername(tokenJWT.getUsername(request.getHeader("BearerToken")));
    }

    public List<Admin> getAll() {
        return adminRepo.findAll();
    }

    public boolean deleteAdmin(long id) {
        adminRepo.deleteById(id);
        return true;
    }

    public String logoutAdmin(HttpServletRequest request) {
        Optional <Admin> adminR = adminRepo.findByUsername(tokenJWT.getUsername(request.getHeader("BearerToken")));
        if (adminR.isEmpty()) return "Utente non trovato!";

        adminR.get().setToken(null);
        adminR.get().setTimestampToken(null);
        adminRepo.saveAndFlush(adminR.get());

        return "Logout effettuato!";
    }

}