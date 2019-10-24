package com.sumian.common.base

import android.os.Bundle
import androidx.fragment.app.Fragment

interface FragmentContainer {
    fun switchToFragment(index: Int, data: Bundle?, animated: Boolean = true) {}
    fun switchToFragment(cla: Class<Fragment>, data: Bundle?, animated: Boolean = true) {}
    fun switchNextFragment(data: Bundle?) {}
}