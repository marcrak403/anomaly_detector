package pl.polsl.anomaly_detector;

import com.github.chen0040.data.frame.BasicDataFrame;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import org.junit.jupiter.api.Test;
import pl.polsl.anomaly_detector.model.detectors.SVMDetector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SVMDetectorTest {

    @Test
    public void testConstructor() {
        SVMDetector detector = new SVMDetector();
        assertEquals(0.0001, detector.getParameters().gamma, 1e-6);
        assertEquals(0.1, detector.getParameters().nu, 1e-6);
    }

    @Test
    public void testFitAndTransform() {
        // Create a sample data frame with known values
        DataFrame data = new BasicDataFrame();

        DataRow row1 = data.newRow();
        row1.setCell("x", 1.0);
        row1.setCell("y", 2.0);
        data.addRow(row1);

        DataRow row2 = data.newRow();
        row2.setCell("x", 3.0);
        row2.setCell("y", 4.0);
        data.addRow(row2);

        SVMDetector detector = new SVMDetector();
        DataFrame transformed = detector.fitAndTransform(data);

        assertEquals(2, transformed.rowCount());
    }

    @Test
    public void testFitAndTransformEmptyData() {
        DataFrame data = new BasicDataFrame();
        SVMDetector detector = new SVMDetector();
        DataFrame transformed = detector.fitAndTransform(data);

        // Verify that the data frame is returned unchanged
        assertEquals(0, transformed.rowCount());
    }

    @Test
    public void testFitAndTransformNullData() {
        SVMDetector detector = new SVMDetector();
        assertThrows(NullPointerException.class, () -> detector.fitAndTransform(null));
    }

}
