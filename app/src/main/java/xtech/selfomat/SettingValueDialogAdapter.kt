package xtech.selfomat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class SettingValueDialogAdapter(private val setting: Setting) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        parent ?: return null

        val view = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_setting_value, parent, false)

        view.findViewById<TextView>(R.id.tv_value).text = setting.possibleValues[position].toString()

        return view
    }

    override fun getItem(position: Int) = setting.possibleValues[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = setting.possibleValues.size

}