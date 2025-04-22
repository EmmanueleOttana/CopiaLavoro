package it.storeottana.vendita_prodotti.controllers;

import it.storeottana.vendita_prodotti.entities.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/add")
    public Feedback create(@RequestParam String orderNumber, @RequestParam long idProduct,
                           @RequestParam String feedbackS, @RequestParam int stars, @RequestParam MultipartFile[] files) throws Exception {
        return feedbackService.addFeedback(orderNumber, idProduct, feedbackS, stars, files);
    }
    @PatchMapping("/addImages")
    public List<String> addImages(@RequestParam long idFeedback, @RequestParam MultipartFile[] files) throws Exception {
        return feedbackService.addImages(idFeedback, files);
    }
    @DeleteMapping("/deleteImages")
    public String deleteImage(@RequestParam long idFeedback, @RequestParam String imageName) throws Exception {
        return feedbackService.deleteImage(idFeedback, imageName);
    }
    @GetMapping("/get")
    public Feedback getFeedback(@RequestParam long idFeedback, @RequestParam String orderNumber) throws Exception {
        return feedbackService.getFeedback(idFeedback, orderNumber);
    }

    @PatchMapping("/edit")
    public Feedback setFeedback(@RequestParam long idFeedback, @RequestParam String orderNumber,
                                @RequestParam String feedbackS, @RequestParam int stars,
                                @RequestParam List<String> orderedFileNames) throws Exception {
        return feedbackService.setFeedback(idFeedback, orderNumber, feedbackS, stars, orderedFileNames);
    }

    @DeleteMapping("/delete")
    public String deleteFeedback(@RequestParam long idFeedback, @RequestParam String orderNumber) throws Exception {
        return feedbackService.deleteFeedback(idFeedback, orderNumber);
    }


}