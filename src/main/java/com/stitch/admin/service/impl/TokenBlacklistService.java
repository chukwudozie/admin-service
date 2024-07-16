package com.stitch.admin.service.impl;

import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@ToString
@Getter
public class TokenBlacklistService {

    private Set<String> blacklist = new HashSet<>();

    public void blacklistToken(String token) {
        blacklist.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
