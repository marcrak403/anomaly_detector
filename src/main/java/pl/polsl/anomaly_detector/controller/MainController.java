package pl.polsl.anomaly_detector.controller;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.polsl.anomaly_detector.model.CSVModel;
import pl.polsl.anomaly_detector.model.detectors.IAnomalyDetector;
import pl.polsl.anomaly_detector.service.CSVService;
import pl.polsl.anomaly_detector.service.DetectorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private CSVService csvService;
    @Autowired
    private DetectorService detectorService;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {

        List<String> methods = detectorService.getAvailableMethods();
        session.setAttribute("methods", methods);
        model.addAttribute("methods", methods);
        return "index";
    }

    @PostMapping("/csv")
    public String readCSV(@RequestParam MultipartFile file, Model model, HttpSession session) throws IOException {

        CSVModel csvModel = csvService.readCSV(file);

        // Store session attributes
        session.setAttribute("fullModel", csvModel);

        // Store model attributes
        model.addAttribute("headers", csvModel.getHeaders());
        model.addAttribute("methods", session.getAttribute("methods"));
        model.addAttribute("headersRead", false);

        return "index";
    }

    @PostMapping("/selected")
    public String setSelected(Model model, HttpServletRequest request, HttpSession session) {

        // Retrieve CSVModel object from session
        CSVModel storedModel = (CSVModel) session.getAttribute("fullModel");

        // Extract selected header indexes from request parameters
        String[] selectedIndexes = request.getParameterValues("headers");
        List<Integer> selectedHeaders = new ArrayList<>();
        for (String index : selectedIndexes) {
            int i = Integer.parseInt(index);
            selectedHeaders.add(i);
        }

        List<String> yHeaders = new ArrayList<>();
        for (int i : selectedHeaders)
            yHeaders.add(storedModel.getHeaders()[i]);

        String xHeader = storedModel.getHeaders()[0];

        // Pack data into DataFrames
        List<DataFrame> frames = csvService.getDataFrames(storedModel, selectedHeaders);

        // Store session attributes
        session.setAttribute("dataFrames", frames);
        session.setAttribute("xHeader", xHeader);
        session.setAttribute("yHeaders", yHeaders);

        // Store model attributes
        model.addAttribute("headers", storedModel.getHeaders());
        model.addAttribute("methods", session.getAttribute("methods"));
        model.addAttribute("headersRead", true);

        return "index";
    }

    @PostMapping("/anomaly")
    public String checkAnomaly(Model model, HttpSession session, HttpServletRequest request) {

        CSVModel storedModel = (CSVModel) session.getAttribute("fullModel");
        List<DataFrame> frames = (List<DataFrame>) session.getAttribute("dataFrames");
        String xHeader = (String) session.getAttribute("xHeader");
        List<String> yHeaders = (List<String>) session.getAttribute("yHeaders");

        String method = request.getParameterValues("algorithm")[0];

        List<List> good = new ArrayList<>();
        List<List> bad = new ArrayList<>();
        IAnomalyDetector algorithm = detectorService.getDetector(method);

        for (int i = 0, framesSize = frames.size(); i < framesSize; i++) {
            DataFrame frame = frames.get(i);
            good.add(new ArrayList());
            bad.add(new ArrayList<>());
            algorithm.fitAndTransform(frame);
            for (DataRow row : frame.rows()) {
                if (row.getCategoricalTargetCell("anomaly").equals("1")) {
                    bad.get(i).add(new double[]{row.getCell(xHeader), row.getCell(yHeaders.get(i))});
                } else {
                    good.get(i).add(new double[]{row.getCell(xHeader), row.getCell(yHeaders.get(i))});
                }
            }
        }

        // Store model attributes
        model.addAttribute("methods", session.getAttribute("methods"));
        model.addAttribute("headers", storedModel.getHeaders());
        model.addAttribute("xHeader", xHeader);
        model.addAttribute("yHeaders", yHeaders);
        model.addAttribute("good", good);
        model.addAttribute("bad", bad);

        return "index";
    }
}
