package com.artistack.oauth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Optional;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplePublicKeyDto {

    ArrayList<Key> keys;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Key {

        String kty;
        String kid;
        String use;
        String alg;
        String n;
        String e;
    }

    public Optional<Key> getMatchedKeyBy(String kid, String alg) {
        return this.keys.stream()
            .filter(key -> key.kid.equals(kid) && key.alg.equals(alg))
            .findFirst();
    }
}