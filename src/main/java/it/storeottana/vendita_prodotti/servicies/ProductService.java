package it.storeottana.vendita_prodotti.servicies;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.entities.Product;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ProductRepo productRepo;
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

    public Object updateProduct(long idProduct, MultipartFile[] files, String title, String description, double price,
                                HttpServletRequest request) throws Exception {
        if (adminService.findAdminByRequest(request).isEmpty()) return "Errore!";

        Product productDB = productRepo.findById(idProduct).get();
        if (!files[0].getOriginalFilename().isEmpty()) productDB.setFileNames(upload(files));
        if (!title.isEmpty()) productDB.setTitle(title);
        if (!description.isEmpty()) productDB.setDescription(description);
        if (!String.valueOf(price).isEmpty()) productDB.setPrice(price);

        productRepo.saveAndFlush(productDB);
        return productDB;
    }

    public Object deleteProduct(long idProduct, HttpServletRequest request){
        if (adminService.findAdminByRequest(request).isEmpty()) return "Errore!";

        productRepo.deleteById(idProduct);
        return true;
    }
    public Object deleteAllProducts(HttpServletRequest request){
        if (adminService.findAdminByRequest(request).isEmpty()) return "Errore!";

        productRepo.deleteAll();
        return true;
    }

    public Object deleteImage(long idInsertion, String imageName, HttpServletRequest request) {
        Optional<Product> productR = productRepo.findById(idInsertion);
        Optional<Admin> adminR = adminService.findAdminByRequest(request);

        if (productR.isEmpty() || adminR.isEmpty()) return "Errore!";
        try {
            cloudinary.api().deleteResources(Arrays.asList("storeottana/"+imageName),
                    ObjectUtils.asMap("type", "upload", "resource_type", "image"));
        } catch (IOException exception) {
            exception.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "Immagini modificate";
    }
}