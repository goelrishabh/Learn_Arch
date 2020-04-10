package com.goelrishabh.arch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val DYNAMIC_FEATURE = "feature1"

    lateinit var splitInstallManager: SplitInstallManager // responsible for downloading the module in foreground
    lateinit var splitInstallRequest: SplitInstallRequest // contains request info for feature from Google Play

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDynamicModules()
    }

    private fun initDynamicModules() {
        splitInstallManager = SplitInstallManagerFactory.create(this)
        splitInstallRequest = SplitInstallRequest
            .newBuilder()
            .addModule(DYNAMIC_FEATURE)
            .build()
    }

    fun buttonClickListener(view: View) {
        when (view.id) {
            buttonOpenNewsModule.id -> openFeature1()
            buttonDeleteNewsModule.id -> deleteFeature1()
            else -> process()
        }
    }

    private fun process() {
        if (!isFeatureDownloaded(DYNAMIC_FEATURE)) {
            downloadFeature()
        } else toggleButtonVisibility(true)
    }

    private fun deleteFeature1() {
        val list = ArrayList<String>()
        list.add(DYNAMIC_FEATURE)
        splitInstallManager.deferredUninstall(list)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Removed Feature", Toast.LENGTH_LONG).show()
                    toggleButtonVisibility(false)
                } else
                    Toast.makeText(this, "deleteFeature1:failed", Toast.LENGTH_LONG).show()
            }
    }

    private fun openFeature1() {
        val intent = Intent().setClassName(
            this,
            "com.goelrishabh.feature1.Feature1Activity"
        )
        startActivity(intent)
    }

    private fun toggleButtonVisibility(isVisisble: Boolean) {
        if (isVisisble) {
            buttonOpenNewsModule.visibility = View.VISIBLE
            buttonDeleteNewsModule.visibility = View.VISIBLE
        } else {
            buttonOpenNewsModule.visibility = View.GONE
            buttonDeleteNewsModule.visibility = View.GONE
        }
    }

    private fun downloadFeature() {
        splitInstallManager.startInstall(splitInstallRequest)
            .addOnCompleteListener {
                if (it.isSuccessful) toggleButtonVisibility(true)
                else Toast.makeText(this, "Task Failed!", Toast.LENGTH_LONG).show()
            }
    }

    private fun isFeatureDownloaded(featureName: String): Boolean {
        return splitInstallManager.installedModules.contains(featureName)
    }

}
