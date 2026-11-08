package com.example.healthmate.health.presentation.ui.navigation

sealed class Screen(
    val route: String) {

    object Dashboard: Screen("dashboard")
    object RecordList: Screen("record_list")
    object AddEditRecord: Screen("add_edit_record")
    object SearchByDate: Screen("search_by_date")

}