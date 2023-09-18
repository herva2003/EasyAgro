package com.puc.easyagro.ui.perfil.tarefas

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.util.AttributeSet
import android.widget.TimePicker

class TimePickerCustom : TimePicker {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    override fun setHour(hour: Int) {
        super.setHour(hour)
    }

    override fun setMinute(minute: Int) {
        super.setMinute(minute)
    }

    override fun getHour(): Int {
        return super.getHour()
    }

    override fun getMinute(): Int {
        return super.getMinute()
    }
}