package xtech.selfomat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SettingRecyclerAdapter.SettingClickedListener {

    private lateinit var viewmodel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        setContentView(R.layout.activity_main)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = SettingRecyclerAdapter(viewmodel.liveList, this).apply {
            settingClickedListener = this@MainActivity
        }

        // Add some settings
        viewmodel.loadSettings()

        btn_trigger.setOnClickListener {
            viewmodel.triggerCapture()
        }
    }

    override fun settingClicked(setting: Setting) {
        // Show the dialog to update the setting
        val adapter = SettingValueDialogAdapter(setting as ListSetting)
        val dialog = AlertDialog.Builder(this).setAdapter(adapter) { dialog, which ->
            if (which !in 0 until adapter.count) {
                dialog?.dismiss()
                return@setAdapter
            }

            viewmodel.updateSetting(setting.updateURL!!, which)

            dialog?.dismiss()
        }.create()
        dialog?.show()
    }

}
