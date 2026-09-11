/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.danhdue.plugin.data.worker.DataSyncWorker
import com.danhdue.plugin.di.PluginComponentProvider
import com.danhdue.plugin.platform.MyPluginHostApiImpl
import com.danhdue.plugin.presentation.MyPluginScreen
import com.danhdue.plugin.presentation.MyPluginViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MyPluginViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = PluginComponentProvider.get(this)
        viewModel = component.getMyPluginViewModel()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Flutter Plugin Native Devbed") },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    ) { innerPadding ->
                        SampleContent(
                            modifier = Modifier.padding(innerPadding),
                            viewModel = viewModel,
                            onTriggerWorker = { triggerDataSyncWorker() },
                            onTestHostApi = { onResult -> testHostApi(onResult) }
                        )
                    }
                }
            }
        }
    }

    private fun triggerDataSyncWorker() {
        val workRequest = OneTimeWorkRequestBuilder<DataSyncWorker>()
            .addTag(WORK_TAG)
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
        Toast.makeText(this, "DataSyncWorker enqueued (ExistingWorkPolicy.KEEP)", Toast.LENGTH_SHORT).show()
    }

    private fun testHostApi(onResult: (String) -> Unit) {
        val component = PluginComponentProvider.get(this)
        val hostApi = MyPluginHostApiImpl(component.getDataUseCase())
        hostApi.getData { result ->
            if (result.isSuccess) {
                val data = result.getOrNull()
                onResult("Pigeon IPC Success: id=${data?.id}, title=${data?.title}")
            } else {
                onResult("Pigeon IPC Failure: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    companion object {
        const val WORK_NAME = "sample_data_sync_work"
        const val WORK_TAG = "sample_worker"
    }
}

@Composable
fun SampleContent(
    modifier: Modifier = Modifier,
    viewModel: MyPluginViewModel,
    onTriggerWorker: () -> Unit,
    onTestHostApi: ((String) -> Unit) -> Unit
) {
    var hostApiResult by remember { mutableStateOf("Not tested yet") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Devbed Controls Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🧪 Native Devbed Test Panel",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Test native WorkManager background execution and Pigeon IPC without FlutterEngine overhead.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onTriggerWorker,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Trigger DataSyncWorker")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        onTestHostApi { result ->
                            hostApiResult = result
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Test Pigeon HostApi Call")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = hostApiResult,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = "📱 Embedded Plugin Jetpack Compose Screen",
            style = MaterialTheme.typography.titleMedium
        )

        // Plugin Screen Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            MyPluginScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
