package com.ezticket.web.activity.pojo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table
public class BlockModel {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer blockno;
    @Column
    private String blockName;
    @Column
    private Integer modelno;
    @Column
    private Integer blockType;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name="blockno", referencedColumnName = "blockno")
    private List<SeatsModel> seatsModels;
}
