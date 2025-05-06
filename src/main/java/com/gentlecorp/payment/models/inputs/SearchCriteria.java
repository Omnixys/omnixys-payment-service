package com.gentlecorp.payment.models.inputs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.util.CollectionUtils.toMultiValueMap;

public record SearchCriteria(String username, UUID sender, UUID receiver) {
    public Map<String, List<Object>> toMap() {
        final Map<String, List<Object>> map = new HashMap<>();
        if (username != null) {
            map.put("username", List.of(username));
        }
        if (sender != null) {
            map.put("sender", List.of(sender));
        }
        if (receiver != null) {
            map.put("receiver", List.of(receiver));
        }
        return toMultiValueMap(map);
    }
}
