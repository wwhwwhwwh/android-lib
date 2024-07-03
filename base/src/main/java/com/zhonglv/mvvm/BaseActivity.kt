package com.zhonglv.mvvm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.zhonglv.mvvm.ThemeActivity
import java.lang.reflect.ParameterizedType


abstract class BaseActivity<VB : ViewBinding, VM : ViewModel> : ThemeActivity()  {

    lateinit var viewModel: VM

    private var _binding: VB? = null
    protected val viewBind get() = _binding!!
    protected var mVbRoot: View? = null


    protected abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 添加控件点击事件或添加监听器
     * */
    open fun initListener() {}

    /**
     * 创建ViewBinding
     * 利用反射 根据泛型得到 ViewBinding
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBind()
        setContentView(mVbRoot)
        viewModel = createViewModel()
        mVbRoot?.post {
            //初始化View
            initView(savedInstanceState)
            // 设置点击事件
            initListener();
        }

    }
    /**
     * 创建ViewBinding
     * 利用反射 根据泛型得到 ViewBinding
     */
    private fun initViewBind() {
        Log.d("BaseActivity", javaClass.simpleName) //获取当前实例的Class对象，相当于在Java中调用 getClass()方法
        // 原来的写法 binding = ActivityTestBinding.inflate(layoutInflater)
        val aClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        _binding = method.invoke(null, layoutInflater) as VB
        mVbRoot = viewBind.root
    }
    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    private fun <VM> getVmClazz(obj: Any): VM {
        return (obj.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as VM
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}