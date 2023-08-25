package pl.polsl.anomaly_detector.model.detectors;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.svmext.oneclass.OneClassSVM;

public class SVMDetector extends OneClassSVM implements IAnomalyDetector{

    public SVMDetector() {
        super();
        this.set_gamma(0.0001);
        this.set_nu(0.1);
        this.thresholdSupplier = () -> 0.0;
    }

    public DataFrame fitAndTransform(DataFrame data) {
        this.fit(data);
        data = data.makeCopy();

        for(int i = 0; i < data.rowCount(); ++i) {
            DataRow row = data.row(i);
            boolean anomaly = this.isAnomaly(row);
            row.setCategoricalTargetCell("anomaly", anomaly ? "1" : "0");
        }

        return data;
    }
}
