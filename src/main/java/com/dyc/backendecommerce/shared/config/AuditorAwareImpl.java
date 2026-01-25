package com.dyc.backendecommerce.shared.config;

import com.dyc.backendecommerce.shared.entity.CustomUserDetails;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        return Optional.ofNullable(userId);
    }
}
