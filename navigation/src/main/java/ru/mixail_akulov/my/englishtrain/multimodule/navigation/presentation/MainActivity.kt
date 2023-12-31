package ru.mixail_akulov.my.englishtrain.multimodule.navigation.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import ru.mixail_akulov.my.englishtrain.multimodule.core.impl.ActivityRequired
import ru.mixail_akulov.my.englishtrain.multimodule.navigation.DestinationsProvider
import ru.mixail_akulov.my.englishtrain.multimodule.navigation.R
import ru.mixail_akulov.my.englishtrain.multimodule.navigation.databinding.ActivityMainBinding
import ru.mixail_akulov.my.englishtrain.multimodule.navigation.presentation.navigation.NavComponentRouter
import ru.mixail_akulov.my.englishtrain.multimodule.navigation.presentation.navigation.RouterHolder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RouterHolder {

    @Inject
    lateinit var navComponentRouterFactory: NavComponentRouter.Factory

    @Inject
    lateinit var destinationsProvider: DestinationsProvider

    @Inject
    lateinit var activityRequiredSet: Set<@JvmSuppressWildcards ActivityRequired>

    private val viewModel by viewModels<MainViewModel>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val navComponentRouter by lazy(LazyThreadSafetyMode.NONE) {
        navComponentRouterFactory.create(R.id.fragmentContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        if (savedInstanceState != null) {
            navComponentRouter.onRestoreInstanceState(savedInstanceState)
        }

        navComponentRouter.onCreate()
        if (savedInstanceState == null) {
            navComponentRouter.switchToStack(destinationsProvider.provideStartDestinationId())
        }
        with(binding) {
            observeUsername()
        }
        activityRequiredSet.forEach {
            it.onCreated(this)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return (navComponentRouter.onNavigateUp()) || super.onSupportNavigateUp()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navComponentRouter.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        activityRequiredSet.forEach { it.onStarted() }
    }

    override fun onStop() {
        super.onStop()
        activityRequiredSet.forEach { it.onStopped() }
    }

    override fun onDestroy() {
        super.onDestroy()
        navComponentRouter.onDestroy()
        activityRequiredSet.forEach { it.onDestroyed() }
    }

    override fun requireRouter(): NavComponentRouter {
        return navComponentRouter
    }

    @SuppressLint("SetTextI18n")
    private fun ActivityMainBinding.observeUsername() {
        viewModel.usernameLiveValue.observe(this@MainActivity) { username ->
            usernameTextView.isVisible = username != null
            if (username != null) {
                usernameTextView.text = "@$username"
            }
        }
    }

}