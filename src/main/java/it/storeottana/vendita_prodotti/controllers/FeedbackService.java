package it.storeottana.vendita_prodotti.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.storeottana.vendita_prodotti.entities.Admin;
import it.storeottana.vendita_prodotti.entities.Feedback;
import it.storeottana.vendita_prodotti.entities.Order;
import it.storeottana.vendita_prodotti.entities.Product;
import it.storeottana.vendita_prodotti.repositories.FeedbackRepo;
import it.storeottana.vendita_prodotti.repositories.OrderRepo;
import it.storeottana.vendita_prodotti.repositories.ProductRepo;
import it.storeottana.vendita_prodotti.security.TokenJWT;
import it.storeottana.vendita_prodotti.utils.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    @Autowired
    private TokenJWT tokenJWT;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private FeedbackRepo feedbackRepo;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private Cloudinary cloudinary;

    public Feedback addFeedback(String orderNumber, long idProduct, String feedbackS, int stars, MultipartFile[] file) throws Exception {
        Optional<Order> orderR = orderRepo.findByOrderNumber(orderNumber);
        if (orderR.isEmpty()) throw new Exception("Ordine non trovato!");

        Optional <Product> productR = productRepo.findById(idProduct);
        if (productR.isEmpty()) throw new Exception("Il prodotto non è più presente!");

        List<String> namesFile = fileStorageService.loadingImages(file);
        Feedback feedback = new Feedback(feedbackS, stars, productR.get());
        feedback.setImages(namesFile);
        return feedbackRepo.saveAndFlush(feedback);
    }

    public Feedback getFeedback(long idFeedback, String orderNumber) throws Exception {
        orderRepo.findByOrderNumber(orderNumber).orElseThrow(()-> new Exception("Ordine non trovato!"));
        return feedbackRepo.findById(idFeedback).orElseThrow(() -> new Exception("Feedback non trovato!"));
    }

    public Feedback setFeedback(long idFeedback, String orderNumber, String feedbackS,
                                int stars, List <String> orderedFileNames) throws Exception {
        orderRepo.findByOrderNumber(orderNumber).orElseThrow(()-> new Exception("Ordine non trovato!"));
        Feedback feedback = feedbackRepo.findById(idFeedback).orElseThrow(() -> new Exception("Feedback non trovato!"));

        feedback.setDescription(feedbackS);
        feedback.setStars(stars);
        if (orderedFileNames != null && !orderedFileNames.isEmpty()) {
            feedback.setImages(new ArrayList<>(orderedFileNames));
        }
        return feedbackRepo.saveAndFlush(feedback);
    }

    public String deleteFeedback(long idFeedback, String orderNumber) throws Exception {
        orderRepo.findByOrderNumber(orderNumber).orElseThrow(()-> new Exception("Ordine non trovato!"));
        feedbackRepo.findById(idFeedback).orElseThrow(() -> new Exception("Feedback non trovato!"));

        feedbackRepo.deleteById(idFeedback);
        return "eliminato con successo!";
    }

    public List<String> addImages(long idFeedback, MultipartFile[] files) throws Exception {
        Feedback feedback = feedbackRepo.findById(idFeedback).orElseThrow(() -> new Exception("Feedback non trovato"));

        List<String> newFileNames = fileStorageService.loadingImages(files);

        feedback.getImages().addAll(newFileNames);
        return feedback.getImages();
    }

    public String deleteImage(long idFeedback, String imageName) throws Exception {
        Optional<Feedback> feedbackR = feedbackRepo.findById(idFeedback);

        feedbackR.orElseThrow(() -> new Exception("Feedback non trovato!"));
        try {
            String cleanName = imageName.substring(0, imageName.lastIndexOf('.'));
            cloudinary.api().deleteResources(Arrays.asList("storeottana/"+cleanName),
                    ObjectUtils.asMap("type", "upload", "resource_type", "image"));
        } catch (IOException exception) {
            exception.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        feedbackR.get().getImages().remove(imageName);
        feedbackRepo.saveAndFlush(feedbackR.get());
        return "Eliminato correttamente!";
    }
}
