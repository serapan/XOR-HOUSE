package gr.ntua.ece.softeng18b.controller;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UploadController {

    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "/Users/bakis/Desktop/";

    @GetMapping("/mobile")
    public String index() {
        return "mobile";
    }

    public static JSONObject detectText(String filePath) throws Exception, IOException {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return null;
                }

                // For full list of available annotations, see http://g.co/cloud/vision/docs
                String s = null;
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    s = annotation.getDescription();
                    break;
                }
                JSONObject obj = new JSONObject();
                String[] parts = s.split("\n");
                String[] parts2;
                String key;
                String value;
                for (int i = 1; i < parts.length; i++) {
                    parts2 = parts[i].split(":");
                    key = parts2[0].trim();
                    value = parts2[1].trim();
                    obj.put(key, value);
                }
                return obj;
            }
        }
        return null;
    }

    @PostMapping("/upload") // //new annotation since 4.3
    public ResponseEntity<Object> singleFileUpload(@RequestParam("file") MultipartFile file,
                                           RedirectAttributes redirectAttributes) {

        JSONObject ret = null;
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            ret = detectText(UPLOADED_FOLDER + file.getOriginalFilename());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

}
