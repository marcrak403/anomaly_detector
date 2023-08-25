package pl.polsl.anomaly_detector.model;

import java.util.List;

public class CSVModel {
    private String[] headers;
    private List<String[]> records;

    public CSVModel(String[] headers, List<String[]> records) {
        this.headers = headers;
        this.records = records;
    }

    public String[] getHeaders() {
        return headers;
    }

    public List<String[]> getRecords() {
        return records;
    }
}
