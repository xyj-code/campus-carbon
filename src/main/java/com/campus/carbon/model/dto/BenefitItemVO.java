package com.campus.carbon.model.dto;

public class BenefitItemVO {
    private Long id;
    private String name;
    private String subtitle;
    private Integer point;
    private Integer stock;
    private String category;
    private String categoryLabel;
    private String icon;
    private String image;
    private String sponsor;
    private String deliveryMode;
    private String description;
    private String exchangeHint;
    private Boolean available;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public Integer getPoint() { return point; }
    public void setPoint(Integer point) { this.point = point; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCategoryLabel() { return categoryLabel; }
    public void setCategoryLabel(String categoryLabel) { this.categoryLabel = categoryLabel; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getSponsor() { return sponsor; }
    public void setSponsor(String sponsor) { this.sponsor = sponsor; }

    public String getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(String deliveryMode) { this.deliveryMode = deliveryMode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExchangeHint() { return exchangeHint; }
    public void setExchangeHint(String exchangeHint) { this.exchangeHint = exchangeHint; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}
