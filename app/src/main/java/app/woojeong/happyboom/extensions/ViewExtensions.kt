package com.dev.hongsw.happyBoom.extensions

import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * @author Hong Seung Woo <qksn1541@gmail.com>
 * @since 19. 5. 29
 * @license Copyright 2019. H&S All rights reserved.
 **/

internal infix fun BottomNavigationView.onClick(function: (MenuItem) -> Boolean) {
    setOnNavigationItemSelectedListener(function)
}

internal infix fun View.onClick(function: (View: View) -> Unit) {
    setOnClickListener(function)
}
