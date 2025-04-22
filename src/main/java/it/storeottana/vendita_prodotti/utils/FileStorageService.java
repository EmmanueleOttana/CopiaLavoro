package it.storeottana.vendita_prodotti.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.storeottana.vendita_prodotti.configurations.CloudinaryConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public String loadingImages(MultipartFile file) throws Exception {
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

    public List<String> loadingImages(MultipartFile[] files) throws Exception {
        List<String> fileNames = new ArrayList<>();
        for ( MultipartFile file : files ) {
            String singleUpload = estraiNomeFile(uploadToCloudinary(file));
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
        return download(fileName);
    }
}