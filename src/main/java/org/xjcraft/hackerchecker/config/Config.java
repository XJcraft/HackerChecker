package org.xjcraft.hackerchecker.config;

import lombok.Data;
import org.xjcraft.annotation.Comment;
import org.xjcraft.annotation.Instance;
import org.xjcraft.annotation.RConfig;

@RConfig
@Data
public class Config {
    @Instance
    public static Config config = new Config();
    @Comment("低于此高度才会记录")
    Integer height = 16;
    @Comment("封禁线")
    Double banRate = 0.02;
    @Comment("嫌疑线")
    Double suspectRate = 0.01;



}