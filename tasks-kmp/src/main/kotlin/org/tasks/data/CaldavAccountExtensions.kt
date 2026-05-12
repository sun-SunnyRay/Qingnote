package org.tasks.data

import org.tasks.data.entity.CaldavAccount
import org.tasks.data.entity.CaldavAccount.Companion.PACKAGE_DAVX5
import org.tasks.data.entity.CaldavAccount.Companion.PACKAGE_DAVX5_MANAGED
import org.tasks.data.entity.CaldavAccount.Companion.PACKAGE_DECSYNC
import org.tasks.data.entity.CaldavAccount.Companion.PACKAGE_ETESYNC
import org.tasks.data.entity.CaldavAccount.Companion.isDecSync
import org.tasks.data.entity.CaldavAccount.Companion.isDavx5
import org.tasks.data.entity.CaldavAccount.Companion.isDavx5Managed
import org.tasks.data.entity.CaldavAccount.Companion.isEteSync

data class AccountIcon(val drawable: String, val tinted: Boolean)

data class OpenTaskApp(val name: String, val packageName: String)

val CaldavAccount.openTaskApp: OpenTaskApp?
    get() = when {
        uuid.isDavx5() -> OpenTaskApp("DAVx\u2075", PACKAGE_DAVX5)
        uuid.isDavx5Managed() -> OpenTaskApp("DAVx\u2075", PACKAGE_DAVX5_MANAGED)
        uuid.isEteSync() -> OpenTaskApp("EteSync", PACKAGE_ETESYNC)
        uuid.isDecSync() -> OpenTaskApp("DecSync CC", PACKAGE_DECSYNC)
        else -> null
    }

val CaldavAccount.composeTitle: String?
    get() = when {
        isTasksOrg -> "tasks_org"
        isCaldavAccount -> "caldav"
        isEtebaseAccount || uuid.isEteSync() -> "etesync"
        uuid.isDavx5() || uuid.isDavx5Managed() -> "davx5"
        uuid.isDecSync() -> "decsync"
        isMicrosoft -> "microsoft"
        isGoogleTasks -> "gtasks_GPr_header"
        isLocalList -> "local_lists"
        else -> null
    }

val CaldavAccount.composeIcon: AccountIcon?
    get() = when {
        isTasksOrg -> AccountIcon("ic_round_icon", false)
        isCaldavAccount -> AccountIcon("ic_webdav_logo", true)
        isEtebaseAccount || uuid.isEteSync() -> AccountIcon("ic_etesync", false)
        uuid.isDavx5() -> AccountIcon("ic_davx5_icon_green_bg", false)
        uuid.isDavx5Managed() -> AccountIcon("ic_davx5_icon_blue_bg", false)
        uuid.isDecSync() -> AccountIcon("ic_decsync", false)
        isMicrosoft -> AccountIcon("ic_microsoft_tasks", false)
        isGoogleTasks -> AccountIcon("ic_google", false)
        isLocalList -> AccountIcon("ic_outline_cloud_off_24px", true)
        else -> null
    }
