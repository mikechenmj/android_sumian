package com.sumian.sddoctor.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.delegate.VersionDelegate
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.homepage.HomepageFragment
import com.sumian.sddoctor.me.MeFragment
import com.sumian.sddoctor.patient.fragment.PatientFragment
import com.sumian.sddoctor.widget.nav.BottomNavigationBar
import com.sumian.sddoctor.widget.nav.NavigationItem
import kotlinx.android.synthetic.main.activity_main.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/18 15:00
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class MainActivity : BaseActivity(), BottomNavigationBar.OnSelectedTabChangeListener {

    companion object {
        private const val EXTRAS_POSITION = "com.sumian.sdd.extras.tab.position"

        @JvmStatic
        fun show(position: Int) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, MainActivity::class.java).apply {
                    putExtra(EXTRAS_POSITION, position)
                })
            }
        }
    }

    private val mVersionDelegate: VersionDelegate  by lazy {
        VersionDelegate.init()
    }

    private var position = 0

    override fun initBundle(bundle: Bundle) {
        position = bundle.getInt(EXTRAS_POSITION, 0)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.onMainActivityCreate()
    }

    override fun initWidget() {
        super.initWidget()
        bottom_nav_bar.setOnSelectedTabChangeListener(this)
        bottom_nav_bar.selectItem(position)
        showFragmentByTabPosition(position)
    }

    override fun onStart() {
        super.onStart()
        mVersionDelegate.checkVersion(this)
    }

    override fun onSelectedTabChange(navigationItem: NavigationItem?, position: Int) {
        showFragmentByTabPosition(position)
    }

    private fun showFragmentByTabPosition(position: Int) {
        for (i in 0..2) {
            val iTag = i.toString()
            var iFragment = supportFragmentManager.findFragmentByTag(iTag)
            if (position == i) {
                if (iFragment == null) {
                    iFragment = createFragmentByPosition(position)
                }
                if (iFragment.isAdded) {
                    showFragment(iFragment)
                } else {
                    addFragment(iFragment, iTag)
                }
            } else {
                if (iFragment != null) {
                    hideFragment(iFragment)
                }
            }
        }
    }

    private fun createFragmentByPosition(position: Int): Fragment {
        return when (position) {
            0 -> HomepageFragment()
            1 -> PatientFragment()
            2 -> MeFragment()
            else -> throw RuntimeException("Wrong position")
        }
    }

    private fun hideFragment(f: Fragment) {
        supportFragmentManager.beginTransaction().hide(f).commit()
    }

    private fun showFragment(f: Fragment) {
        supportFragmentManager.beginTransaction().show(f).commit()
    }

    private fun addFragment(f: Fragment, fTag: String) {
        supportFragmentManager.beginTransaction().add(R.id.lay_tab_container, f, fTag).commit()
    }
}