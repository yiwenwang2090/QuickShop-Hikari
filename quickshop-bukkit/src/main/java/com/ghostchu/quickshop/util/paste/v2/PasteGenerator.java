/*
 *  This file is a part of project QuickShop, the name is PasteGenerator.java
 *  Copyright (C) Ghost_chu and contributors
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.ghostchu.quickshop.util.paste.v2;

import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.util.MsgUtil;
import com.ghostchu.quickshop.util.paste.v2.item.*;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PasteGenerator {
    @Getter
    private final List<PasteItem> pasteItems = new LinkedList<>();
    private final long timestamp = System.currentTimeMillis();
    private final CommandSender sender;

    public PasteGenerator(@Nullable CommandSender sender) {
        this.sender = sender;
        add(new HeaderItem(System.currentTimeMillis(), Map.of()));
        add(new ServerInfoItem());
        add(new SystemInfoItem());
        add(new ChatProcessorInfoItem());
        add(new ShopsInfoItem());
        add(new ReplaceableModulesItem());
        add(new PluginsInfoItem());
        add(new CachePerformanceItem());
        add(new ConfigCollectorItem());
        add(new DebugLogsItem());
    }

    public void add(@NotNull PasteItem pasteItem) {
        pasteItems.add(pasteItem);
    }

    @NotNull
    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append(bakeHeader()).append("\n");
        for (PasteItem pasteItem : pasteItems) {
            builder.append(pasteItem.toHTML()).append("\n");
        }
        builder.append(bakeFooter(getSenderName()));
        return builder.toString();
    }

    @NotNull
    private String getSenderName() {
        if (sender == null) {
            return "Automatic";
        } else {
            return sender.getName();
        }
    }

    @NotNull
    private String bakeHeader() {
        return DOCUMENT_HEADER
                .replace("{title}", "QuickShop-" + QuickShop.getFork() + " // Paste");
    }

    @NotNull
    private String bakeFooter(@NotNull String pasteCreator) {
        return DOCUMENT_FOOTER
                .replace("{product}", "QuickShop-" + QuickShop.getFork() + " v" + QuickShop.getVersion())
                .replace("{time}", formatTime(timestamp))
                .replace("{pastecreator}", pasteCreator);
    }

    @NotNull
    private String formatTime(long time) {
        String timeUnit = LegacyComponentSerializer.legacySection().serialize(QuickShop.getInstance().text().of("timeunit.std-format").forLocale(MsgUtil.getDefaultGameLanguageCode()));
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat(timeUnit);
        } catch (Exception e) {
            format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        }
        return format.format(time);
    }


    private final static String DOCUMENT_HEADER = """
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="utf-8">
                    <title>{title}</title>
                    <link rel="stylesheet" href="https://unpkg.com/sakura.css/css/sakura.css" type="text/css">
                </head>
                <body style = "max-width: 70em !important;">
                <main>
            """;

    private final static String DOCUMENT_FOOTER = """
                </main>
                </body>
                <footer>
                   <p>
                     Generated by {product} at {time}. <br />
                     Paste creator: {pastecreator}.<br />
                     Built with <a href="https://unpkg.com/sakura.css/css/sakura.css">Sakura.css</a>. <br />
                     Pastebin providers: <a href="https://bytebin.lucko.me/">Lucko Bytebin</a>, <a href="https://paste.helpch.at/">HelpChat Pastebin</a> <br />
                     Made with sugar and cat paws <3.
                   </p>
                </footer>
            </html>
            """;
}
