package pl.polsl.anomaly_detector;

import com.github.chen0040.data.frame.DataFrame;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import pl.polsl.anomaly_detector.model.CSVModel;
import pl.polsl.anomaly_detector.service.CSVService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CSVServiceTest {

    @Test
    public void testReadCSVValidFile() throws IOException {
        // Create a mock MultipartFile object with predetermined headers and records
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("header1;header2\nvalue1;value2\nvalue3;value4".getBytes()));

        // Call the readCSV() method with the mock file as an argument
        CSVService csvService = new CSVService();
        CSVModel csvModel = csvService.readCSV(mockFile);

        // Verify that the returned CSVModel object has the correct headers and records
        assertArrayEquals(new String[]{"header1", "header2"}, csvModel.getHeaders());
        assertEquals(2, csvModel.getRecords().size());
        assertArrayEquals(new String[]{"value1", "value2"}, csvModel.getRecords().get(0));
        assertArrayEquals(new String[]{"value3", "value4"}, csvModel.getRecords().get(1));
    }

    @Test
    public void testReadCSVEmptyFile() throws IOException {
        // Create a mock MultipartFile object that represents an empty CSV file
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        // Call the readCSV() method with the mock file as an argument
        CSVService csvService = new CSVService();
        CSVModel csvModel = csvService.readCSV(mockFile);

        // Verify that the returned CSVModel object has no headers or records
        assertArrayEquals(null, csvModel.getHeaders());
        assertTrue(csvModel.getRecords().isEmpty());
    }

    @Test
    public void testReadCSVInvalidFile() {
        // Create a mock MultipartFile object that is not a CSV file
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("text/plain");

        // Call the readCSV() method with the mock file as an argument
        CSVService csvService = new CSVService();
        assertThrows(RuntimeException.class, () -> {
            csvService.readCSV(mockFile);
        });
    }

    @Test
    public void testGetDataFramesValidInput() {
        // Create a mock CSVModel object with predetermined headers and records
        String[] headers = new String[]{"header1", "header2", "header3"};
        List<String[]> records = new ArrayList<>();
        records.add(new String[]{"1", "2", "3"});
        records.add(new String[]{"4", "5", "6"});
        CSVModel mockModel = new CSVModel(headers, records);

        // Call the getDataFrames() method with the mock model and a list of selected headers as arguments
        CSVService csvService = new CSVService();
        List<DataFrame> dataFrames = csvService.getDataFrames(mockModel, Arrays.asList(1, 2));


        // Verify that the returned list of DataFrame objects contains the correct data
        assertEquals(2, dataFrames.size());
        assertEquals("header1", dataFrames.get(0).getInputColumns().get(0).getColumnName());
        assertEquals("header2", dataFrames.get(0).getInputColumns().get(1).getColumnName());
        assertArrayEquals(new double[]{1.0, 2.0}, dataFrames.get(0).row(0).toArray());
        assertArrayEquals(new double[]{4.0, 5.0}, dataFrames.get(0).row(1).toArray());
        assertEquals("header1", dataFrames.get(1).getInputColumns().get(0).getColumnName());
        assertEquals("header3", dataFrames.get(1).getInputColumns().get(1).getColumnName());
        assertArrayEquals(new double[]{1.0, 3.0}, dataFrames.get(1).row(0).toArray());
        assertArrayEquals(new double[]{4.0, 6.0}, dataFrames.get(1).row(1).toArray());
    }

    @Test
    public void testGetDataFramesInvalidHeaderIndex() {
        // Create a mock CSVModel object with predetermined headers and records
        String[] headers = new String[]{"header1", "header2", "header3"};
        List<String[]> records = new ArrayList<>();
        records.add(new String[]{"1", "2", "3"});
        records.add(new String[]{"4", "5", "6"});
        CSVModel mockModel = new CSVModel(headers, records);

        // Call the getDataFrames() method with the mock model and a list of selected headers that includes an invalid index as arguments
        CSVService csvService = new CSVService();
        assertThrows(IndexOutOfBoundsException.class, () -> csvService.getDataFrames(mockModel, Arrays.asList(0, 3)));
    }

    @Test
    public void testGetDataFramesEmptySelectedHeaders() {
        // Create a mock CSVModel object with predetermined headers and records
        String[] headers = new String[]{"header1", "header2", "header3"};
        List<String[]> records = new ArrayList<>();
        records.add(new String[]{"1", "2", "3"});
        records.add(new String[]{"4", "5", "6"});
        CSVModel mockModel = new CSVModel(headers, records);

        // Call the getDataFrames() method with the mock model and an empty list of selected headers as arguments
        CSVService csvService = new CSVService();
        List<DataFrame> dataFrames = csvService.getDataFrames(mockModel, new ArrayList<>());

        // Verify that the returned list of DataFrame objects is empty
        assertTrue(dataFrames.isEmpty());
    }
}
