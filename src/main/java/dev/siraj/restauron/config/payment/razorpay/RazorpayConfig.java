package dev.siraj.restauron.config.payment.razorpay;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    // Configuration class for Razorpay payment gateway integration

@Getter
@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    /** Create and configure a RazorpayClient bean.
     *
     * @return RazorpayClient instance
     * @throws RazorpayException if there is an error creating the client
     */
    @Bean
    public RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(keyId, keySecret);
    }

}
