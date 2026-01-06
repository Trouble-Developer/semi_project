package edu.kh.project.info.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * [역할] 지역 코드(시도, 시군구) 정보를 담는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AreaCode {
    private String areaCd;   // 지역 코드 (예: 6110000)
    private String areaNm;   // 지역 명칭 (예: 서울특별시)
    private String parentCd; // 부모 지역 코드 (시도일 경우 NULL, 시군구일 경우 해당 시도 코드)
}