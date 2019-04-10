package xtech.selfomat

open class Setting(val name: String, val value: Any, val updateURL: String?)
class ListSetting(name: String, val currentIndex: Int, val possibleValues: List<String>, update: String?) : Setting(name, possibleValues[currentIndex], update)