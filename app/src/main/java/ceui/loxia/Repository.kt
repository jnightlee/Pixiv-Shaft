package ceui.loxia

import androidx.lifecycle.MutableLiveData
import ceui.refactor.ListItemHolder

abstract class Repository<FragmentT> {

    val refreshState = MutableLiveData<RefreshState>()
    val holderList = MutableLiveData<List<ListItemHolder>>()

    suspend fun refreshInvoker(
        frag: FragmentT,
        hint: RefreshHint
    ) {
        try {
            refreshState.value = RefreshState.LOADING(refreshHint = hint)
            refresh(frag)
        } catch (ex: Exception) {
            ex.printStackTrace()
            holderList.value = listOf()
            refreshState.value = RefreshState.ERROR(ex)
        }
    }

    suspend fun loadMoreInvoker(frag: FragmentT) {
        try {
            refreshState.value = RefreshState.LOADING(refreshHint = RefreshHint.loadMore())
            loadMore(frag)
        } catch (ex: Exception) {
            ex.printStackTrace()
            holderList.value = listOf()
            refreshState.value = RefreshState.ERROR(ex)
        }
    }

    abstract suspend fun refresh(
        fragment: FragmentT
    )

    abstract suspend fun loadMore(
        fragment: FragmentT
    )
}
