package legacyorder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext
public class OrderProcessingServiceTest {
    @Autowired
    private Processor processor;

    @Autowired
    private MessageCollector messageCollector;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddLegacyBarcodeToMessage() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> sampleOrder = new HashMap<>();
        sampleOrder.put("sampleKey", "sampleValue");
        String sampleOrderString = objectMapper.writeValueAsString(sampleOrder);

        Message<String> message = new GenericMessage<>(sampleOrderString);
        processor.input().send(message);

        Message<String> receivedMessage =
            (Message<String>)messageCollector.forChannel(processor.output()).poll(100, TimeUnit.MILLISECONDS);

        Map<String, Object> receivedOrder = objectMapper.readValue(receivedMessage.getPayload(), Map.class);
        assertThat(receivedOrder).containsAllEntriesOf(sampleOrder);
        assertThat(receivedOrder).containsKey("legacyInfo");
        assertThat((Map)receivedOrder.get("legacyInfo")).contains(entry("barcode", "8TAC123456789A040"));
    }
}
