package pl.polsl.anomaly_detector.service;

import org.springframework.stereotype.Service;
import pl.polsl.anomaly_detector.model.detectors.IAnomalyDetector;
import pl.polsl.anomaly_detector.model.detectors.IsolationForestDetector;
import pl.polsl.anomaly_detector.model.detectors.LOFDetector;
import pl.polsl.anomaly_detector.model.detectors.SVMDetector;

import java.util.ArrayList;
import java.util.List;

@Service
public class DetectorService {

    private final String IForest = "Isolation Forest";
    private final String LOF = "Local Outlier Factor";
    private final String SVM = "Support Vector Machines";

    public List<String> getAvailableMethods() {
        List<String> methods = new ArrayList<>();
        methods.add(IForest);
        methods.add(LOF);
        methods.add(SVM);
        return methods;
    }

    public IAnomalyDetector getDetector(String method) {

        switch (method) {
            case IForest:
                return new IsolationForestDetector();
            case LOF:
                return new LOFDetector();
            case SVM:
                return new SVMDetector();
        }
        return null;
    }
}
