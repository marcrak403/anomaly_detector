package pl.polsl.anomaly_detector.service;

import com.github.chen0040.data.frame.BasicDataFrame;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.polsl.anomaly_detector.model.CSVModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CSVService {

    public CSVModel readCSV(MultipartFile file) throws IOException {
        List<String[]> records;
        try (CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build()) {
            String[] headers = csvReader.readNext();
            records = csvReader.readAll();
            return new CSVModel(headers, records);
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public List<DataFrame> getDataFrames(CSVModel model, List<Integer> selectedHeaders) {
        List<DataFrame> frames = new ArrayList<>();
        int idx = 0;
        for (Integer index : selectedHeaders) {
            frames.add(new BasicDataFrame());
            List<String[]> records = model.getRecords();
            String[] headers = model.getHeaders();
            for (int i = 0; i < records.size(); i++) {
                DataRow row = frames.get(idx).newRow();
                row.setCell(headers[0], Double.parseDouble(records.get(i)[0]));
                row.setCell(headers[index], Double.valueOf(records.get(i)[index]));
                frames.get(idx).addRow(row);
            }
            frames.get(idx).lock();
            idx++;
        }
        return frames;
    }
}

