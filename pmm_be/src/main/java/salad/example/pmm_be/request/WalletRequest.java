package salad.example.pmm_be.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class WalletRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "type is required")
    private String type;

    @NotNull(message = "balance is required")
    @DecimalMin(value = "0.00", message = "balance must be >= 0")
    private BigDecimal balance;

    @Pattern(regexp = "^#?[0-9A-Fa-f]{6}$", message = "color must be hex like #1122FF")
    private String color;

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
