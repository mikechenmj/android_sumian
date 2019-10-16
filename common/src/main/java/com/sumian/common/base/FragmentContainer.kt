package com.sumian.common.base

import android.os.Bundle
import androidx.fragment.app.Fragment

interface FragmentContainer {
    fun switchToFragment(index: Int, data: Bundle?){}
    fun switchToFragment(cla: Class<Fragment>, data: Bundle?){}
    fun switchNextFragment(data: Bundle?){}
}