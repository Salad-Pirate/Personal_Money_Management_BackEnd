package salad.example.pmm_be.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionRequest {
    @NotNull(message = "categoryId is required")
    private Integer categoryId;

    @NotNull(message = "paymentMethodId is required")
    private Integer paymentMethodId;

    @NotNull(message = "walletId is required")
    private Integer walletId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be > 0")
    private BigDecimal amount;

    @NotBlank(message = "type is required")
    @Pattern(regexp = "^(Income|Expense)$", message = "type must be Income or Expense")
    private String type;

    @NotNull(message = "occuredAt is required")
    private OffsetDateTime occuredAt;

    private String transactionLocation;
    private String note;

    // getters/setters
    public Integer getCategoryId() {return categoryId;}
    public void setCategoryId(Integer categoryId) {this.categoryId = categoryId;}

    public Integer getPaymentMethodId() {return paymentMethodId;}
    public void setPaymentMethodId(Integer paymentMethodId) {this.paymentMethodId = paymentMethodId;}


    public Integer getWalletId() {return walletId;}
    public void setWalletId(Integer walletId) {this.walletId = walletId;}

    public BigDecimal getAmount() {return amount;}
    public void setAmount(BigDecimal amount) {this.amount = amount;}


    public String getType() {return type;}
    public void setType(String type) {this.type = type;}


    public OffsetDateTime getOccuredAt() {return occuredAt;}
    public void setOccuredAt(OffsetDateTime occuredAt) {this.occuredAt = occuredAt;}



    public String getTransactionLocation() {return transactionLocation;}
    public void setTransactionLocation(String transactionLocation) {this.transactionLocation = transactionLocation;}


    public String getNote() {return note;}
    public void setNote(String note) {this.note = note;}

}