package salad.example.pmm_be.request;

import jakarta.validation.constraints.*;

public class CategoryRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "type is required")
    @Pattern(regexp = "^(Income|Expense)$", message = "type must be Income or Expense")
    private String type;

    @NotBlank(message = "color is required")
    @Pattern(regexp = "^#?[0-9A-Fa-f]{6}$", message = "color must be hex like #3366FF")
    private String color;

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
