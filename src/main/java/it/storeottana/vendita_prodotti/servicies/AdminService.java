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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    @Value("${urlSiteWeb}")
    private String urlSiteWeb;
    @Value("${companyEmail}")
    private String companyEmail;
    @Value("${bossCode}")
    private String bossCode;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanAdmin() {
        adminRepo.deleteByIsActiveFalse();
    }

    public String registration(AdminRequest adminRequest) throws Exception {
        if (!adminRequest.getAuthCode().equals(this.authCode)) return "Richiesta inviata! Attendi che ti venga consentito l'accesso.";
        if (adminRepo.findByUsername(adminRequest.getUsername()).isPresent()) return "Username già esistente!";
        if (adminRepo.findByEmail(adminRequest.getEmail()).isPresent()) return "Email già registrata!";
        if (adminRepo.findByTelephoneNumber(adminRequest.getTelephoneNumber()).isPresent()) return "Numero di telefono già registrato!";

        Admin adminNew = new Admin(adminRequest.getUsername(), adminRequest.getEmail(),
                fixTelephoneNumber(adminRequest.getTelephoneNumber()), EncryptionPw.hashPassword(adminRequest.getPassword()));
        adminRepo.saveAndFlush(adminNew);

        this.postman.sendMail(adminNew.getEmail(),"Attivazione account",
                "Per inviare la richiesta d'attivazione dell'account cliccare nel link a seguire:\n" +
                        urlSiteWeb +"/admin/activeAccount/"+adminNew.getId()+"/"+adminNew.getActivationCode());

        return "L'utente "+ adminNew.getUsername() + " è stato creato con successo!" +
                "\nConfermare l'indirizzo email per attivarlo";
    }

    public String activeAccount(long id, String activationCode) throws Exception {
        Optional <Admin> admin1 = adminRepo.findById(id);
        admin1.orElseThrow(() -> new Exception("Non autorizzato!"));

        Admin admin = admin1.get();
        if (admin.getActivationCode().equals(activationCode)){
            admin.setActivationCode(null);
            adminRepo.saveAndFlush(admin);

            this.postman.sendMail(companyEmail,"Richiesta d'attivazione account",
                    "Per attivare l'account di "+admin.getUsername()+" cliccare nel link a seguire:\n" +
                            urlSiteWeb +"/admin/acceptance/"+admin.getId());

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

    public String updateEmail(HttpServletRequest request, String newEmail) throws Exception {
        Optional <Admin> adminJwt = findAdminByRequest(request);
        adminJwt.orElseThrow(() -> new Exception("Non autorizzato!"));

        Admin admin = adminJwt.get();
        if (!newEmail.isEmpty()){
            this.smsService.sendSms(admin.getTelephoneNumber(),
                "Per modificare l'email cliccare nel link a seguire:\n" +
                        urlSiteWeb+"/admin/new-email/"+admin.getId()+'/'+newEmail);

            return "É stato inviato un sms di conferma, cliccare sul link fornito per sms " +
                    "per confermare il nuovo indirizzo email";
        }else return "Impossibile inserire questa email!";
    }

    public String acceptNewEmail(long id, String newEmail) throws Exception {
        Optional <Admin> adminJwt = adminRepo.findById(id);
        adminJwt.orElseThrow(() -> new Exception("Non autorizzato!"));

        adminJwt.get().setEmail(newEmail);
        adminRepo.saveAndFlush(adminJwt.get());

        return "Email modificata con successo!";
    }

    public String updateTelephoneNumber(HttpServletRequest request, String telephoneNumber) throws Exception {
        Optional <Admin> adminJwt = findAdminByRequest(request);
        adminJwt.orElseThrow(() -> new Exception("Non autorizzato!"));

        Admin admin = adminJwt.get();
        if (!telephoneNumber.isEmpty()){
            this.postman.sendMail(admin.getEmail(),"Cambio numero di telefono",
                "Per modificare il numero di telefono cliccare nel link a sottostante:\n" +
                        urlSiteWeb+"/admin/new-tel-num/"+admin.getId()+"/"
                        +telephoneNumber.substring(telephoneNumber.length()-10));

            return "É stata inviata un email di conferma, cliccare sul link fornito per email " +
                    "per confermare il nuovo numero di telefono";
        }else return "Impossibile inserire questa email!";
    }

    public Admin acceptNewTelephoneNumber(long id, String newTelephoneNumber) throws Exception {
        Optional <Admin> adminJwt = adminRepo.findById(id);
        adminJwt.orElseThrow(() -> new Exception("Non autorizzato!"));

        Admin admin = adminJwt.get();
        admin.setTelephoneNumber(fixTelephoneNumber(newTelephoneNumber));
        return adminRepo.saveAndFlush(admin);
    }
    public String updatePassword(HttpServletRequest request, String oldPwd, String newPwd) throws Exception {
        Optional<Admin> adminOpt = findAdminByRequest(request);
        Admin admin = adminOpt.orElseThrow(() -> new Exception("Non autorizzato!"));

        if (!EncryptionPw.checkPassword(admin.getPassword(), oldPwd)) {
            throw new Exception("Vecchia password errata!");
        }
        admin.setPassword(EncryptionPw.hashPassword(newPwd));
        adminRepo.saveAndFlush(admin);

        return "Password aggiornata con successo!";
    }
    public Optional <Admin> findAdminByRequest(HttpServletRequest request){
        return adminRepo.findByUsername(tokenJWT.extractUsername(request.getHeader("BearerToken")));
    }

    public List<Admin> getAll(HttpServletRequest request) throws Exception {
        Optional <Admin> adminJwt = findAdminByRequest(request);
        adminJwt.orElseThrow(() -> new Exception("Non autorizzato!"));

        return adminRepo.findAll();
    }

    public boolean deleteAdmin(long id, String bossCode, HttpServletRequest request) throws Exception {
        Optional <Admin> adminR = findAdminByRequest(request);
        adminR.orElseThrow(() -> new Exception("Non autorizzato!"));
        if (!this.bossCode.equals(bossCode)) throw new Exception("Non autorizzato!");

        adminRepo.deleteById(id);
        return true;
    }

    public String logoutAdmin(HttpServletRequest request) throws Exception {
        Optional <Admin> adminR = findAdminByRequest(request);
        adminR.orElseThrow(() -> new Exception("Non autorizzato!"));

        adminR.get().setToken(null);
        adminR.get().setTimestampToken(null);
        adminRepo.saveAndFlush(adminR.get());

        return "Logout effettuato!";
    }
    public String fixTelephoneNumber(String telephoneNumber){
        String[] s = telephoneNumber.split(" ");
        StringBuilder sb = new StringBuilder();

        Arrays.stream(s).forEach(x -> sb.append(x.trim()));
        if (sb.toString().toCharArray()[0] != '+'){
            try {
                Long.parseLong(sb.toString());
                if (sb.toString().length() != 10) throw new NumberFormatException("Il formato del numero inserito non è valido!");
            }catch (NumberFormatException e){
                throw new NumberFormatException("Il formato del numero inserito non è valido!");
            }
            return "+39"+sb;
        }else{
            String num = sb.substring(1);
            try {
                Long.parseLong(num);
                if (sb.toString().length() > 10) throw new NumberFormatException("Il formato del numero inserito non è valido!");
            }catch (NumberFormatException e){
                throw new NumberFormatException("Il formato del numero inserito non è valido!");
            }
            return sb.toString();
        }

    }




}