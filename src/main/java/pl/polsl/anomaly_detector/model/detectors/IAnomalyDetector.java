package pl.polsl.anomaly_detector.model.detectors;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;

public interface IAnomalyDetector {

    boolean isAnomaly(DataRow tuple);
    DataFrame fitAndTransform(DataFrame data);
}
