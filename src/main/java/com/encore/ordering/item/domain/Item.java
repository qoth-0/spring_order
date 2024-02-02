package com.encore.ordering.item.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private String imagePath;
    @Builder.Default // 빌더 사용 시 디폴트 값 지정 가능
    private String delYn = "N";
//    oneToMany-itemorder는 item기준으로 조회할 일이 없기 때문에 안써도된다 - 필요할 때만 사용하면 됨

    @CreationTimestamp
    private LocalDateTime createdTime;
    @UpdateTimestamp
    private LocalDateTime updatedTime;

    public void deleteItem() {
        this.delYn = "Y";
    }

    public void updateItem(String name, String category, int price, int stockQuantity, String imagePath) {
        this.name = name;
        this.category = category;
        this. price = price;
        this. stockQuantity = stockQuantity;
        this.imagePath = imagePath;
    }
    public void updateStockQuantity(int newQuantity) {
        this.stockQuantity = newQuantity;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
