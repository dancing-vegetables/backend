package com.dv.dancingvegetables.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Lank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="lank_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name ="lank_point",nullable = false)
    private Long lankPoint;

    public void update(Long lankPoint){
        this.lankPoint=lankPoint;
    }
}
