package org.xjcraft.hackerchecker.config;

import lombok.Data;
import org.xjcraft.annotation.Instance;
import org.xjcraft.annotation.RConfig;
import org.xjcraft.hackerchecker.bean.MinerLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RConfig(configName = "warning.yml")
@Data
public class WarningConfig {
    @Instance
    public static WarningConfig config = new WarningConfig();
    Map<String,Float> suspects = new LinkedHashMap<>();



}