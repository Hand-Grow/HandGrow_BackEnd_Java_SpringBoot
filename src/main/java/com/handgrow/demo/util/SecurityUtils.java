package com.handgrow.demo.util;

import com.handgrow.demo.entity.Account;
import com.handgrow.demo.exception.AppException;
import com.handgrow.demo.exception.ErrorCode;
import java.util.UUID;
import org.springframework.security.core.Authentication;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UUID extractAccountId(Authentication authentication) {
        if (authentication == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (authentication.getPrincipal() instanceof Account account) {
            return account.getId();
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
