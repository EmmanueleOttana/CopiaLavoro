package it.storeottana.vendita_prodotti.servicies;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.entities.Cart;
import it.storeottana.vendita_prodotti.entities.Product;
import it.storeottana.vendita_prodotti.repositories.CartRepo;
import it.storeottana.vendita_prodotti.repositories.ProductRepo;
import it.storeottana.vendita_prodotti.utils.FileStorageService;
import it.storeottana.vendita_prodotti.utils.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CartService cartService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private Cloudinary cloudinary;

    public List<String> upload(MultipartFile[] files) throws Exception {
        List<String> fileNames = new ArrayList<>();
        for ( MultipartFile file : files ) {
            String singleUpload = fileStorageService.estraiNomeFile(fileStorageService.uploadToCloudinary(file));
            fileNames.add(singleUpload);
        }
        return fileNames;
    }

    public byte[] download(String fileName, HttpServletResponse response) throws Exception {
        String extension = FilenameUtils.getExtension(fileName);
        switch (extension) {
            case "jpg", "jpeg" -> response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            case "gif" -> response.setContentType(MediaType.IMAGE_GIF_VALUE);
            case "png" -> response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader("Content-Disposition","attachment; fileName=\""+fileName+"\"");
        return fileStorageService.download(fileName);
    }
    public Object create(String name, MultipartFile[] files, String title, String description, double price,
                            HttpServletRequest request) throws Exception {

        Optional<Admin> adminRequest = adminService.findAdminByRequest(request);
        if (adminRequest.isEmpty() || !adminRequest.get().isActive() || adminRequest.get().isSuspended()) return "Errore!";

        Product product = new Product(name, upload(files),title,description,price);
        return productRepo.saveAndFlush(product);
    }

    public List<Product> getAllProducts(){
        return productRepo.findAll();
    }

    public List<Product> getProductByTitle(String title){
        return productRepo.findByTitleContaining(title);
    }

    public Product getProductById(long id){
        Optional <Product> product = productRepo.findById(id);
        return product.orElse(null);
    }

    public Object updateProduct(long idProduct, String name, List<String> orderedFileNames,
                                String title, String description, double price,
                                HttpServletRequest request) throws Exception {
        adminService.findAdminByRequest(request).orElseThrow(() -> new Exception("Non autorizzato!"));

        Product productDB = productRepo.findById(idProduct)
                .orElseThrow(() -> new Exception("Prodotto non trovato"));
        if (!name.isEmpty()) productDB.setName(name);
        if (!title.isEmpty()) productDB.setTitle(title);
        if (!description.isEmpty()) productDB.setDescription(description);
        if (!String.valueOf(price).isEmpty()) productDB.setPrice(price);

        if (orderedFileNames != null && !orderedFileNames.isEmpty()) {
            productDB.setFileNames(new ArrayList<>(orderedFileNames));
        }

        productRepo.saveAndFlush(productDB);
        return productDB;
    }
    public List<String> addImages(MultipartFile[] files, long idProduct, HttpServletRequest request) throws Exception {
        adminService.findAdminByRequest(request)
                .orElseThrow(() -> new Exception("Non autorizzato!"));
        Product productDB = productRepo.findById(idProduct)
                .orElseThrow(() -> new Exception("Prodotto non trovato"));

        List<String> newFileNames = upload(files);

        productDB.getFileNames().addAll(newFileNames);
        return productDB.getFileNames();
    }

    public boolean deleteProduct(long idProduct, HttpServletRequest request) throws Exception {
        adminService.findAdminByRequest(request).orElseThrow(() -> new Exception("Non autorizzato!"));
        Product product = productRepo.findById(idProduct).orElseThrow(() -> new Exception("Prodotto non trovato!"));

        List<String> names = product.getFileNames();
        for (int i = 0; i < names.size(); i++) {
            try {
                String cleanName = names.get(i).substring(0, names.get(i).lastIndexOf('.'));
                cloudinary.api().deleteResources(Arrays.asList("storeottana/"+cleanName),
                        ObjectUtils.asMap("type", "upload", "resource_type", "image"));
            } catch (IOException exception) {
                exception.getMessage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        cartService.deleteProducedByCarts(idProduct);
        productRepo.deleteById(idProduct);
        return true;
    }
    public boolean deleteAllProducts(HttpServletRequest request) throws Exception {
        adminService.findAdminByRequest(request).orElseThrow(() -> new Exception("Non autorizzato!"));

        List<Product> products = productRepo.findAll();
        for (Product product : products) {
            for (String name : product.getFileNames()) {
                try {
                    String cleanName = name.substring(0, name.lastIndexOf('.'));
                    cloudinary.api().deleteResources(Arrays.asList("storeottana/" + cleanName),
                            ObjectUtils.asMap("type", "upload", "resource_type", "image"));
                } catch (IOException exception) {
                    exception.getMessage();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        cartService.deleteAll();
        productRepo.deleteAll();
        return true;
    }

    public String deleteImage(long idInsertion, String imagesName, HttpServletRequest request) throws Exception {
        Optional<Product> productR = productRepo.findById(idInsertion);
        Optional<Admin> adminR = adminService.findAdminByRequest(request);

        productR.orElseThrow(() -> new Exception("Prodotto non trovato!"));
        adminR.orElseThrow(() -> new Exception("Non autorizzato!"));
            try {
                String cleanName = imagesName.substring(0, imagesName.lastIndexOf('.'));
                cloudinary.api().deleteResources(Arrays.asList("storeottana/"+cleanName),
                        ObjectUtils.asMap("type", "upload", "resource_type", "image"));
            } catch (IOException exception) {
                exception.getMessage();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            productR.get().getFileNames().remove(imagesName);
            productRepo.saveAndFlush(productR.get());
        return "Immagini modificate";
    }


}