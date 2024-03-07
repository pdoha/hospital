package com.hospital.reservation.entities;

import com.hospital.admin.center.entities.CenterInfo;
import com.hospital.commons.entities.Base;
import com.hospital.member.entities.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class RequestReservation extends Base {
    @Id
    @GeneratedValue
    private  Long bookCode; //예약 코드

    @Enumerated(EnumType.STRING)
    @Column(length=15, nullable = false)
/*    private ReservationStatus status = ReservationStatus.APPLY; //예약 상태*/

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="member_userNo")
    private Member member; //회원쪽정보 가져올 것임

    @Column(length=15, nullable = false)
    private String donorTel; //헌혈자 전화번호

 /*   @Enumerated(EnumType.STRING)
    @Column(length=25, nullable = false)
    private DonationType bookType;  //헌혈 종류*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cCode")
    private CenterInfo center; //예약할 센터

    private LocalDateTime bookDateTime; //예약시간

    private int capacity;
}
