package salad.example.pmm_be.response;

import java.time.OffsetDateTime;

public class PaymentMethodResponse {
    private Integer paymentMethodId;
    private String name;
    private String color;            // #RRGGBB
    private OffsetDateTime createdAt;

    public Integer getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(Integer paymentMethodId) { this.paymentMethodId = paymentMethodId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
