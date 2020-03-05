package org.xjcraft.hackerchecker.config;

import lombok.Data;
import org.xjcraft.annotation.Folder;
import org.xjcraft.annotation.Instance;
import org.xjcraft.annotation.RConfig;
import org.xjcraft.hackerchecker.bean.MinerLog;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RConfig(configName = "logs.yml")
@Data
public class LogConfig {
    @Instance
    public static LogConfig config = new LogConfig();
    Map<String, MinerLog> logs = new LinkedHashMap<>();



}