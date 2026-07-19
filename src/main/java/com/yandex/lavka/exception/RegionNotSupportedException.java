package com.yandex.lavka.exception;

import org.springframework.http.HttpStatus;

public class RegionNotSupportedException extends BusinessException {

    public RegionNotSupportedException(Integer region) {
        super(
                "error.region.not-supported",
                ErrorCode.REGION_NOT_SUPPORTED,
                HttpStatus.UNPROCESSABLE_ENTITY,
                region
        );
    }
}
