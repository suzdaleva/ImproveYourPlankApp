package com.example.improveyourplank_app.timepicker

import android.content.Context
import android.view.View
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat


internal class ClickActionDelegate(context: Context, resId: Int) :
    AccessibilityDelegateCompat() {
    private val clickAction: AccessibilityActionCompat = AccessibilityActionCompat(
        AccessibilityNodeInfoCompat.ACTION_CLICK, context.getString(resId)
    )

    override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfo(host, info)
        info.addAction(clickAction)
    }

}