package org.tasks.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tasks.data.dao.CaldavDao
import org.tasks.data.entity.CaldavAccount
import org.tasks.data.entity.CaldavCalendar

private val mutex = Mutex()

suspend fun CaldavDao.newLocalAccount(): CaldavAccount = mutex.withLock {
    newLocalAccountUnsafe()
}

suspend fun CaldavDao.getLocalList() = mutex.withLock {
    getLocalList(getLocalAccount())
}

suspend fun CaldavDao.getLocalAccount() =
    getAccounts(CaldavAccount.TYPE_LOCAL).firstOrNull() ?: newLocalAccountUnsafe()

suspend fun CaldavDao.getOrCreateLocalAccount(): CaldavAccount = mutex.withLock {
    getAccounts(CaldavAccount.TYPE_LOCAL).firstOrNull()
        ?: CaldavAccount(
            accountType = CaldavAccount.TYPE_LOCAL,
            uuid = UUIDHelper.newUUID(),
        ).let { it.copy(id = insert(it)) }
}

private suspend fun CaldavDao.newLocalAccountUnsafe(): CaldavAccount {
    val account = CaldavAccount(
        accountType = CaldavAccount.TYPE_LOCAL,
        uuid = UUIDHelper.newUUID(),
    )
        .let { it.copy(id = insert(it)) }
    getLocalList(account)
    return account
}

private suspend fun CaldavDao.getLocalList(account: CaldavAccount): CaldavCalendar =
    getCalendarsByAccount(account.uuid!!).getOrNull(0)
        ?: CaldavCalendar(
            name = "default_list",
            uuid = UUIDHelper.newUUID(),
            account = account.uuid,
        ).apply {
            insert(this)
        }