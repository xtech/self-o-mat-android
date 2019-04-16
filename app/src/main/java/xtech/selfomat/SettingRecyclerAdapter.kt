package xtech.selfomat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import xtech.selfomat.databinding.ItemBoolSettingBinding
import xtech.selfomat.databinding.ItemSettingBinding


class SettingRecyclerAdapter(
    currentSettings: LiveData<List<Setting<out Any>>>,
    lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<SettingRecyclerAdapter.SettingViewHolder>() {

    private enum class ViewType {
        SETTING,
        BOOL_SETTING
    }

    interface SettingClickedListener {
        fun settingClicked(setting: Setting<out Any>)
    }

    var settingClickedListener: SettingClickedListener? = null

    private var latestList: List<Setting<out Any>> = emptyList()

    init {
        currentSettings.observe(lifecycleOwner, Observer {
            latestList = it
            notifyDataSetChanged()
        })
    }

    private fun getViewType(position: Int): ViewType {
        val data = latestList[position]
        return when (data) {
            is BoolSetting -> ViewType.BOOL_SETTING
            else -> ViewType.SETTING
        }
    }

    override fun getItemViewType(position: Int) = getViewType(position).ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ViewType.BOOL_SETTING.ordinal -> BoolSettingViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_bool_setting,
                    parent,
                    false
                )
            ).apply {
                binding.switchChecked.setOnCheckedChangeListener { _,_ ->
                    setting?.run { settingClickedListener?.settingClicked(this) }
                }
            }
            else -> BaseSettingViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_setting,
                    parent,
                    false
                )
            ).apply {
                binding.root.setOnClickListener { _ ->
                    setting?.run { settingClickedListener?.settingClicked(this) }
                }
            }
        }


    override fun getItemCount() = latestList.size

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        latestList[position].let {
            holder.setting = it
            holder.itemView.tag = it
        }
    }

    abstract class SettingViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var setting: Setting<out Any>? = null
        set(value) {
            field = value
            updateBinding()
        }

        abstract protected fun updateBinding()
    }

    class BaseSettingViewHolder(val binding: ItemSettingBinding) : SettingViewHolder(binding.root) {
        override fun updateBinding() {
            setting?.run {
                binding.settingName = name
                binding.settingValue = liveDisplayValue
            }
        }
    }

    class BoolSettingViewHolder(val binding: ItemBoolSettingBinding) : SettingViewHolder(binding.root) {
        override fun updateBinding() {
            setting?.run {
                binding.settingName = name
                binding.settingValue = liveUpdateValue as LiveData<Boolean>
            }
        }
    }


}