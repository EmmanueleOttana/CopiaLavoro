package it.storeottana.vendita_prodotti.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import it.storeottana.vendita_prodotti.configurations.CloudinaryConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class FileStorageService {
    @Value("${directoryFile}")
    private String directory;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private CloudinaryConfig cloudinaryConfig;

    public String upload(MultipartFile file) throws Exception {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String newName = UUID.randomUUID().toString();
        String completeName = newName + "." + extension;
        File fileFolder = new File(directory);
        if (!fileFolder.exists()) throw new Exception("Il file non esiste!");
        if (!fileFolder.isDirectory()) throw new Exception("La directory non esiste!");
        File finalDestination = new File(directory + "\\" + completeName);
        if (finalDestination.exists()) throw new Exception("Il file esiste già");
        file.transferTo(finalDestination);
        return completeName;
    }
    public byte[] download(String fileName) throws Exception {
        File fileRepository = new File(directory + "\\" + fileName);
        if(!fileRepository.exists()) throw new Exception("Il file non esiste");
        return IOUtils.toByteArray(new FileInputStream(fileRepository));
    }
    public String uploadToCloudinary(MultipartFile file) throws Exception {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String newName = UUID.randomUUID().toString();
        String nameComplete = newName+ "." + extension;
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "public_id", newName,
                    "folder", "storeOttana/" // Facoltativo: salva i file in una cartella specifica
            ));
            return uploadResult.get("secure_url").toString(); // Restituisce l'URL pubblico del file
        } catch (IOException e) {
            throw new Exception("Errore durante il caricamento su Cloudinary", e);
        }
    }
    public String estraiNomeFile(String urlFile){
        return urlFile.substring(urlFile.lastIndexOf("/")+1);
    }
    public void prova() throws Exception {
        ApiResponse apiResponse = cloudinaryConfig.cloudinary().api().resourceByAssetID("e4c4ae04f65b1287dc5dda381503759c", ObjectUtils.emptyMap());
        System.out.println(apiResponse);
    }
}