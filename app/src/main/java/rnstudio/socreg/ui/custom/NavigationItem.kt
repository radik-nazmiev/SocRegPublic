package rnstudio.socreg.ui.custom

import rnstudio.socreg.R

sealed class NavigationItem(var route: String?, var icon: Int?, var title: Int?) {
    object Main : NavigationItem("main", R.drawable.ic_home, R.string.main)
    object SMS : NavigationItem("sms", R.drawable.ic_sms, R.string.sms)
    object Proxy : NavigationItem("proxy", R.drawable.ic_wan, R.string.proxy)
    object Logs : NavigationItem("logs", R.drawable.ic_note, R.string.logs)
    object Empty: NavigationItem("empty",null,null)
}
