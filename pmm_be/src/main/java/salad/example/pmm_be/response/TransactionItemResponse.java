package salad.example.pmm_be.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionItemResponse {
    private Integer transactionId;

    private Integer categoryId;
    private String  categoryName;
    private String  categoryType;

    private Integer paymentMethodId;
    private String  paymentMethodName;

    private Integer walletId;
    private String  walletName;

    private BigDecimal amount;
    private BigDecimal signedAmount;
    private String transactionType;
    private OffsetDateTime occuredAt;

    private String transactionLocation;
    private String note;

    private Double latitude;
    private Double longitude;

    // getters

    public Integer getTransactionId() {
        return transactionId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public Integer getWalletId() {
        return walletId;
    }

    public String getWalletName() {
        return walletName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getSignedAmount() {
        return signedAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public OffsetDateTime getOccuredAt() {
        return occuredAt;
    }

    public String getTransactionLocation() {
        return transactionLocation;
    }

    public String getNote() {
        return note;
    }

    public Double getLatitude() {return latitude;}

    public Double getLongitude() {return longitude;}

    // setters

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public void setWalletId(Integer walletId) {
        this.walletId = walletId;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setSignedAmount(BigDecimal signedAmount) {
        this.signedAmount = signedAmount;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setOccuredAt(OffsetDateTime occuredAt) {
        this.occuredAt = occuredAt;
    }

    public void setTransactionLocation(String transactionLocation) {
        this.transactionLocation = transactionLocation;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setLatitude(Double latitude) {this.latitude = latitude;}

    public void setLongitude(Double longitude) {this.longitude = longitude;}
}