package com.dev.hongsw.happyBoom.subtitle

import app.woojeong.happyboom.subtitle.DefaultStyle

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 2
 * @license Copyright 2019. H&S All rights reserved.
 **/
// Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
// Dialogue: 0,[H:MM:SS.ss],[H:MM:SS.ss],Default,,0000,0000,0000,,자막1
data class Subtitle(var defaultStyle: DefaultStyle, var ViewWidth: Int, var ViewHeight: Int, var Layer: String = "0", var Start: String = "", var End: String = "",
                    var Style: String = "Default", var Name: String = "", var MarginL: String = "0000",
                    var MarginR: String = "0000", var MarginV: String = "0000", var Effect: String = "",
                    var Text: String = "",
                    var customStyle: CustomStyle = CustomStyle(PosX = (ViewWidth/2).toString(), PosY = (ViewHeight/2).toString()),
                    var setting: Boolean = false, var available: Boolean = true) {

    override fun toString(): String {
        System.out.println("Dialogue: $Layer,$Start,$End,$Style,$Name,$MarginL,$MarginR,$MarginV,$Effect,${if (!setting) Text else customStyle.toString() + Text}\n")
        return "Dialogue: $Layer,$Start,$End,$Style,$Name,$MarginL,$MarginR,$MarginV,$Effect,${if (!setting) Text.replace("\n", "\\N") else customStyle.toString() + Text}\n"
    }
}