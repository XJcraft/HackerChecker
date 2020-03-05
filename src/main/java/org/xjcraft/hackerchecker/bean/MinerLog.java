package org.xjcraft.hackerchecker.bean;

import lombok.Data;
import org.xjcraft.api.SimpleConfigurationSerializable;

@Data
public class MinerLog implements SimpleConfigurationSerializable {
    Integer total = 0;
    Integer diamonds = 0;
}
