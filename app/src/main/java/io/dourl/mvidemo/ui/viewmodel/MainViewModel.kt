package io.dourl.mvidemo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import io.dourl.mvidemo.bean.CDrugs
import io.dourl.mvidemo.repository.DrugsRepository
import io.dourl.mvidemo.ui.intent.ActionIntent
import io.dourl.mvidemo.ui.intent.ActionState
import java.lang.Exception

/**
 * 作者：Vaccae
 * 邮箱：3657447@qq.com
 * 创建时间： 13:42
 * 功能模块说明：
 */
class MainViewModel : ViewModel() {

    private val _respository = DrugsRepository()
    //页面事件的 channel 分发
    val actionIntent = Channel<ActionIntent>(Channel.UNLIMITED)
    private val _actionstate = MutableSharedFlow<ActionState>()
    val state: SharedFlow<ActionState>
        get() = _actionstate

    var listDrugs = mutableListOf<CDrugs>()



    val searchStateFlow = MutableStateFlow("")
    val searchShareFlow = MutableSharedFlow<String>(replay = 0, onBufferOverflow = BufferOverflow.SUSPEND)

    init {
        initActionIntent()
        _actionstate.tryEmit(ActionState.Normal)
    }

    private fun initActionIntent() {
        viewModelScope.launch {
            actionIntent.consumeAsFlow().collect {
                when (it) {
                    //根据意图事件分别调用不同的方法
                    is ActionIntent.LoadDrugs -> LoadDrugs()
                    is ActionIntent.InsDrugs -> InsDrugs()
                    is ActionIntent.DelDrugs -> {
                        DelDrugs(it.idx)
                    }
                    is ActionIntent.Info -> {
                        InfoMsg(it.msg)
                    }
                    else ->{
                        //todo
                    }
                }
            }
        }
    }

    private fun InfoMsg(msg: String){
        viewModelScope.launch {
            _actionstate.emit(ActionState.Info(msg))
        }
    }

    private fun DelDrugs(idx: Int) {
        viewModelScope.launch {
            if (idx < 0) {
                _actionstate.emit(ActionState.Error("未选中要删除的药品信息"))
                return@launch
            }
            //修改为加载状态
            _actionstate.emit(ActionState.Loading)
            //开始加载数据
            _actionstate.emit(
                try {
                    listDrugs.removeAt(idx)
                    ActionState.Drugs(listDrugs)
                } catch (e: Exception) {
                    ActionState.Error(e.message.toString())
                }
            )
            //恢复状态
            _actionstate.emit(ActionState.Normal)
        }
    }

    private fun InsDrugs() {
        viewModelScope.launch {
            //修改为加载状态
            _actionstate.emit(ActionState.Loading)
            //开始加载数据
            _actionstate.emit(
                try {
                    listDrugs.add(_respository.getNewDrugs())
                    ActionState.Drugs(listDrugs)
                } catch (e: Exception) {
                    ActionState.Error(e.message.toString())
                }
            )
            //恢复状态
            _actionstate.emit(ActionState.Normal)
        }
    }

    //加载药品信息
    private fun LoadDrugs() {
        viewModelScope.launch {
            //修改为读取状态
            _actionstate.emit(ActionState.Loading)
            //开始加载数据
            _actionstate.emit(
                try {
                    listDrugs = _respository.createDrugs()
                    ActionState.Drugs(listDrugs)
                } catch (e: Exception) {
                    ActionState.Error(e.message.toString())
                }
            )
            //恢复状态
            _actionstate.emit(ActionState.Normal)
        }
    }
}