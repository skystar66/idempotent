package com.tbex.idmpotent.server.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Pair implements Serializable {

  private IdpKey idpKey;
  private Integer count;

}
