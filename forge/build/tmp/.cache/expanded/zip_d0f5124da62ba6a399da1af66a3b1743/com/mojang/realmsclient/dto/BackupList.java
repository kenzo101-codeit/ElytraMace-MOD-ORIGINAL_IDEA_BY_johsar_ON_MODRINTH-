package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.List;
import net.minecraft.util.LenientJsonParser;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BackupList extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public List<Backup> backups;

    public static BackupList parse(String p_87410_) {
        BackupList backuplist = new BackupList();
        backuplist.backups = Lists.newArrayList();

        try {
            JsonElement jsonelement = LenientJsonParser.parse(p_87410_).getAsJsonObject().get("backups");
            if (jsonelement.isJsonArray()) {
                for (JsonElement jsonelement1 : jsonelement.getAsJsonArray()) {
                    backuplist.backups.add(Backup.parse(jsonelement1));
                }
            }
        } catch (Exception exception) {
            LOGGER.error("Could not parse BackupList: {}", exception.getMessage());
        }

        return backuplist;
    }
}