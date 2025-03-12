package it.storeottana.vendita_prodotti.utils;

import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @Test
    void estraiNomeFile() {
        String urlFile = "https://res.cloudinary.com/dzaopwmcj/image/upload/v1741352071/vknz2icxqq7qqf2mjuve.png";
        System.out.println(urlFile.substring(urlFile.lastIndexOf("/")+1, urlFile.length()));
    }
}