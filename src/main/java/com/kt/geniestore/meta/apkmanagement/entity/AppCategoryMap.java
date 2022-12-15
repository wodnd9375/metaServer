package com.kt.geniestore.meta.apkmanagement.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name="APPCATEGORYMAP_SEQ_GENERATOR", sequenceName="APPCATEGOTYMAP_SEQ")
public class AppCategoryMap {
    @Id
    @GeneratedValue
    @Column(name="appcategory_map_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="app_id")
    private App app;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="category_id")
    private Category category;

    @Builder
    public AppCategoryMap(App app, Category category) {
        this.app = app;
        this.category = category;
    }
}
