package pl.polsl.anomaly_detector;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.polsl.anomaly_detector.model.CSVModel;
import pl.polsl.anomaly_detector.service.CSVService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CSVService csvService;

    @Test
    public void testReadCSV() throws Exception {
        String csvContent = "timestamp;value1;value2\n" +
                "1598340000;1.1;2.2\n" +
                "1598340001;3.3;4.4\n";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/csv")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());

        CSVModel csvModel = csvService.readCSV(file);
        assertEquals("timestamp", csvModel.getHeaders()[0]);
        assertEquals("value1", csvModel.getHeaders()[1]);
        assertEquals("value2", csvModel.getHeaders()[2]);
        assertEquals("1598340000", csvModel.getRecords().get(0)[0]);
        assertEquals("3.3", csvModel.getRecords().get(1)[1]);
    }
}

