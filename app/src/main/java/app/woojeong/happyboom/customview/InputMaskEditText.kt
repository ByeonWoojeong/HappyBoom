package app.woojeong.happyboom.customview

import android.content.Context
import android.util.AttributeSet
import app.woojeong.happyboom.R
import com.dev.hongsw.happyBoom.customview.InputMaskWatcher
import com.google.android.material.textfield.TextInputEditText


/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 6. 2
 * @license Copyright 2019. H&S All rights reserved.
 **/
class InputMaskEditText : TextInputEditText {

    private var maskTextWatcher: InputMaskWatcher? = null

    private var mask: String? = null
        set(value) {
            field = value
            if (value.isNullOrEmpty()) {
                removeTextChangedListener(maskTextWatcher)
            } else {
                maskTextWatcher = mask?.let { InputMaskWatcher(this, it) }
                addTextChangedListener(maskTextWatcher)
            }
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.InputMaskEditText)
            with(a) {
                mask = getString(R.styleable.InputMaskEditText_mask)
                recycle()
            }
        }
    }
}