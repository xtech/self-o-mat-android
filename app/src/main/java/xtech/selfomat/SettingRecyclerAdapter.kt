package xtech.selfomat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import xtech.selfomat.databinding.ItemSettingBinding


class SettingRecyclerAdapter(
    currentSettings: LiveData<List<Setting>>,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<SettingRecyclerAdapter.SettingViewHolder>() {

    interface SettingClickedListener {
        fun settingClicked(setting: Setting)
    }

    var settingClickedListener: SettingClickedListener? = null

    private var latestList: List<Setting> = emptyList()

    init {
        currentSettings.observe(lifecycleOwner, Observer {
            latestList = it
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SettingViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_setting,
            parent,
            false
        )
    ).apply {
        itemView.setOnClickListener { view ->
            (view.tag as? Setting)?.let { setting ->
                settingClickedListener?.settingClicked(setting)
            }
        }
    }

    override fun getItemCount() = latestList.size

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        latestList[position].let {
            holder.setSetting(it)
            holder.itemView.tag = it
        }
    }


    class SettingViewHolder(private val binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setSetting(setting: Setting) {
            binding.settingName = setting.name
            binding.settingValue = setting.value
        }
    }

}