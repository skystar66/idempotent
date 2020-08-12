package com.tbex.idmpotent.client.weight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerAdrWeight {


    /**
     * ip
     */
    private String ip;

    /**
     * nodeId
     */
    private String nodeId;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 权重值
     */
    private Integer weight;


}
