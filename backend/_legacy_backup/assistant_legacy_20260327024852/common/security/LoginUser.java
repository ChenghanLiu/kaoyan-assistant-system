package com.kaoyan.assistant.common.security;

import com.kaoyan.assistant.domain.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class LoginUser implements UserDetails {

    private final SysUser user;

    public LoginUser(SysUser user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getRealName() {
        return user.getRealName();
    }

    public SysUser getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode().name()))
                .toList();
    }

    public List<String> getRoleCodes() {
        return user.getRoles().stream().map(role -> role.getRoleCode().name()).toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getEnabled());
    }
}
