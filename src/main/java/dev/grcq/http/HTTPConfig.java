package dev.grcq.http;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HTTPConfig {

    private final int port;

}
