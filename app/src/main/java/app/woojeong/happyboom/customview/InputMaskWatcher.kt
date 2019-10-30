package com.dev.hongsw.happyBoom.customview

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 2
 * @license Copyright 2019. H&S All rights reserved.
 **/

class InputMaskWatcher(private val editText: TextInputEditText, private val mask: String) : TextWatcher {

    private var mSelfChange = false

    override fun afterTextChanged(s: Editable?) {
        if (mSelfChange) return
        mSelfChange = true
        format(s)
        mSelfChange = false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    private fun format(text: Editable?) {
        if (text.isNullOrEmpty()) return
        text.apply {
            val editableFilters = filters
            filters = emptyArray()

            val formatted = StringBuilder()
            val list = toMutableList()

            mask.forEach { m ->
                if (list.isNullOrEmpty()) return@forEach
                var c = list[0]
                if (m.isPlaceHolder()) {
                    if (!c.isLetterOrDigit()) {
                        val iterator = list.iterator()
                        while (iterator.hasNext()) {
                            c = iterator.next()
                            if (c.isLetterOrDigit()) break
                            iterator.remove()
                        }
                    }
                    if (list.isNullOrEmpty()) return@forEach
                    formatted.append(c)
                    list.removeAt(0)
                } else {
                    formatted.append(m)
                    if (m == c) {
                        list.removeAt(0)
                    }
                }
            }
            val previousLength = length
            val currentLength = formatted.length
            replace(0, previousLength, formatted, 0, currentLength)

            if (currentLength < previousLength) {
                val currentSelection = findCursorPosition(text, editText.selectionStart)
                editText.setSelection(currentSelection)
            }

            filters = editableFilters
        }
    }

    private fun findCursorPosition(text: Editable?, start: Int): Int {
        if (text.isNullOrEmpty()) return start
        val textLength = text.length
        val maskLength = mask.length
        var position = start
        for (i in start until maskLength) {
            if (mask[i].isPlaceHolder()) {
                break
            }
            position++
        }
        position++
        return if (position < textLength) position else textLength
    }

    private fun Char.isPlaceHolder(): Boolean = this == '#'
}