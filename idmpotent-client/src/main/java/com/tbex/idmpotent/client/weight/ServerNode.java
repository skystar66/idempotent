package com.tbex.idmpotent.client.weight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerNode {


    /**
     * 是否可用 0 不可用 1 可用
     */
    private Integer available;

    /**
     * nodeId
     */
    private String nodeId;


}
