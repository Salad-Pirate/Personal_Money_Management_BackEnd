package salad.example.pmm_be.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class WalletResponse {
    private Integer walletId;
    private String name;
    private BigDecimal balance;
    private String type;
    private String color;
    private OffsetDateTime createdAt;

    // getters/setters
    public Integer getWalletId() { return walletId; }
    public void setWalletId(Integer walletId) { this.walletId = walletId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
