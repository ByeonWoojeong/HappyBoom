package app.woojeong.happyboom.subtitle

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 4
 * @license Copyright 2019. H&S All rights reserved.
 **/

data class DefaultStyle(var Name: String = "Default", var FontName: String = "KoPubWorldDotum", var FontSize: String = "70",
                        var PrimaryColor: String = "&H00FFFFFF", var SecondaryColor: String = "&H00FFFFFF",
                        var OutlineColor: String = "&H00000000", var BackColor: String = "&00FFFFFFF",
                        var Bold: String = "-1", var Italic: String = "0", var Underline: String = "0",
                        var StrikeOut: String = "0", var ScaleX: String = "100", var ScaleY: String = "100",
                        var Spacing: String = "0", var Angle: String = "0.00", var BorderStyle: String = "1",
                        var Outline: String = "1", var Shadow: String = "1", var Alignment: String = "2",
                        var MarginL: String = "30", var MarginR: String = "30", var MarginV: String = "30",
                        var Encoding: String = "0", val ViewWidth: Int, val ViewHeight: Int) {

    override fun toString(): String {
        return "Style: $Name,$FontName,$FontSize,$PrimaryColor,$SecondaryColor,$OutlineColor,$BackColor" +
                ",$Bold,$Italic,$Underline,$StrikeOut,$ScaleX,$ScaleY,$Spacing,$Angle,$BorderStyle,$Outline" +
                ",$Shadow,$Alignment,$MarginL,$MarginR,$MarginV,$Encoding\n\n"
    }

    operator fun plus(b: DefaultStyle) {
        this::class.memberProperties.forEach {
            if (it is KMutableProperty<*>) {
                it.setter.call(this, b::class.memberProperties.find { other -> other.name == it.name }!!.getter.call(b))
            }
        }
    }

    operator fun get(b: DefaultStyle) {
        this.MarginV = b.MarginV
        this.MarginL = b.MarginL
        this.FontSize = b.FontSize
        this.PrimaryColor = b.PrimaryColor
        this.OutlineColor = b.OutlineColor
    }
}