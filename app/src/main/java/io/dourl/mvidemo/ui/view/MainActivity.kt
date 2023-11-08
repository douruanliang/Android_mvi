package io.dourl.mvidemo.ui.view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lib_base.db.AppDatabase
import com.example.lib_base.db.UserRepository
import com.example.lib_base.viewmodel.UserViewModel
import io.dourl.mvidemo.R
import io.dourl.mvidemo.bean.CDrugs
import io.dourl.mvidemo.ui.adapter.DrugsAdapter
import io.dourl.mvidemo.ui.intent.ActionIntent
import io.dourl.mvidemo.ui.intent.ActionState
import io.dourl.mvidemo.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import com.example.lib_base.viewmodel.UserViewModelFactory

class MainActivity : AppCompatActivity() {

    private val TAG = "X Fold"

    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }
    private val btncreate: Button by lazy { findViewById(R.id.btncreate) }
    private val btnadd: Button by lazy { findViewById(R.id.btnadd) }
    private val btndel: Button by lazy { findViewById(R.id.btndel) }
    private val tvmsg: TextView by lazy { findViewById(R.id.tv_Msg) }

    private lateinit var mainViewModel: MainViewModel
    private lateinit var drugsAdapter: DrugsAdapter

    private lateinit var userRepository: UserRepository
    private lateinit var userViewModel: UserViewModel
    //adapter的位置
    private var adapterpos = -1

    private var mSensorManager: SensorManager? = null
    private var mSensor: Sensor? = null
    private val mSensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            //p0.values[0]: 测量的铰链角度,其值范围在0到360度之间
            p0?.let {
                Log.i(TAG, "当前铰链角度为：${it.values[0]}")
            }
        }

        // 当传感器精度发生改变时回调该方法
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            p0?.let {
                Log.i(TAG, "Sensor:${it.name}, value:$p1")
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        //开启监听
        /*mSensorManager?.let {
            it.registerListener(mSensorEventListener, mSensor!!,
                SensorManager.SENSOR_DELAY_NORMAL)
        }*/
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
        // 取消监听
        /*mSensorManager?.let {
            it.unregisterListener(mSensorEventListener);
        }*/
    }


    val shareFlow = flowOf(1, 2, 3, 4).shareIn(
        scope = lifecycleScope,
        started = Lazily,
        replay = 0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 获取传感器管理对象
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // 获取传感器的类型(TYPE_HINGE_ANGLE:铰链角度传感器)
        mSensorManager?.let {
            mSensor = it.getDefaultSensor(Sensor.TYPE_HINGE_ANGLE);
        }

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        drugsAdapter = DrugsAdapter(R.layout.rcl_item, mainViewModel.listDrugs)
        drugsAdapter.setOnItemClickListener { baseQuickAdapter, view, i ->
            adapterpos = i
        }

        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = drugsAdapter

        //初始化ViewModel监听
        observeViewModel()

        userRepository = UserRepository(AppDatabase.getInstance(this))

        val  factory = UserViewModelFactory(userRepository)

        userViewModel = ViewModelProvider(this,factory).get(UserViewModel::class.java)


        btncreate.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.actionIntent.send(ActionIntent.LoadDrugs)
            }

            lifecycleScope.launch {
                userViewModel.getBooksByUser()
            }
        }

        btnadd.setOnClickListener {
            lifecycleScope.launch {
                mainViewModel.actionIntent.send(ActionIntent.InsDrugs)
            }

            lifecycleScope.launch {
               userViewModel.insert()
            }
        }

        btndel.setOnClickListener {
            lifecycleScope.launch {
                Log.i("status", "$adapterpos")
                val item = try {
                    drugsAdapter.getItem(adapterpos)
                } catch (e: Exception) {
                    CDrugs()
                }
                mainViewModel.actionIntent.send(ActionIntent.DelDrugs(adapterpos, item))
            }
            lifecycleScope.launch {
                userViewModel.deleteUserWithId()
            }

        }

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.state.collect {
                    when (it) {
                        is ActionState.Normal -> {
                            btncreate.isEnabled = true
                            btnadd.isEnabled = true
                            btndel.isEnabled = true
                            tvmsg.text = ""
                        }

                        is ActionState.Loading -> {
                            btncreate.isEnabled = false
                            btncreate.isEnabled = false
                            btncreate.isEnabled = false
                        }

                        is ActionState.Drugs -> {
                            drugsAdapter.setList(it.drugs)
//                            drugsAdapter.setNewInstance(it.drugs)
                        }

                        is ActionState.Error -> {
                            Toast.makeText(this@MainActivity, it.msg, Toast.LENGTH_SHORT).show()
                        }

                        is ActionState.Info -> {
                            tvmsg.append("${it.msg}\r\n")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            shareFlow.collect {
                Log.i("豆", "shared-value->$it")
            }
        }

        //  mainViewModel.searchShareFlow.
        //  mainViewModel.searchStateFlow.value


    }


}