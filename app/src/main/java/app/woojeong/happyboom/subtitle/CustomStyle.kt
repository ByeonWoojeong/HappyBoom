package com.dev.hongsw.happyBoom.subtitle

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 3
 * @license Copyright 2019. H&S All rights reserved.
 **/

data class CustomStyle(    var PrimaryColor: String = "&H00FFFFFF",
                           var OutlineColor: String = "&H00000000",
                           var PosX: String,
                           var PosY: String,
                           var FontSize: String = "50") {

    override fun toString(): String {
        return "{\\1c&H${PrimaryColor.substring(4,10)}& \\1a&H${PrimaryColor.substring(2,4)}& \\3c&H${OutlineColor.substring(4,10)}&" +
                " \\3a&H${OutlineColor.substring(2, 4)}& \\pos($PosX, $PosY) \\fs$FontSize}"
    }

}
