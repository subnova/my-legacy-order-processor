package legacyorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@EnableBinding(Processor.class)
public class OrderProcessingService {
    private final Logger logger = LoggerFactory.getLogger(OrderProcessingService.class);

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public Map<String, Object> handle(Map<String, Object> order) {
        logger.info("Processing order {}", order);

        Map<String, Object> legacyInfo = new HashMap<>();
        legacyInfo.put("barcode", "8TAC123456789A040");

        order.put("legacyInfo", legacyInfo);

        return order;
    }
}
