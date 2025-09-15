package com.agit.peerflow.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String email;
    private String password;

    // AI 특징값 (수집된 데이터)
    private Double dur;
    private String proto;
    private String service;
    private String state;
    private Integer spkts;
    private Integer dpkts;
    private Integer sbytes;
    private Integer dbytes;
    private Double rate;
    private Integer sttl;
    private Integer dttl;
    private Long sload;
    private Long dload;
    private Integer sloss;
    private Integer dloss;
    private Double sinpkt;
    private Double dinpkt;
    private Double sjit;
    private Double djit;
    private Integer swin;
    private Integer dwin;
    private Double tcprtt;
    private Double synack;
    private Double ackdat;
    private Double smean;
    private Double dmean;
    private Integer trans_depth;
    private Integer response_body_len;
    private Integer ct_srv_src;
    private Integer ct_state_ttl;
    private Integer ct_dst_ltm;
    private Integer ct_src_dport_ltm;
    private Integer ct_dst_sport_ltm;
    private Integer ct_dst_src_ltm;
    private Integer is_ftp_login;
    private Integer ct_ftp_cmd;
    private Integer ct_flw_http_mthd;
    private Integer ct_src_ltm;
    private Integer ct_srv_dst;
    private Integer is_sm_ips_ports;
}
