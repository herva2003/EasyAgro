package com.puc.easyagro.ui.perfil.tarefas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.puc.easyagro.R
import com.puc.easyagro.apiServices.UserApi
import com.puc.easyagro.constants.Constants
import com.puc.easyagro.databinding.FragmentTarefaBinding
import com.puc.easyagro.datastore.UserPreferencesRepository
import com.puc.easyagro.model.Tarefa
import com.puc.easyagro.ui.perfil.tarefas.NotifyWork.Companion.NOTIFICATION_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class TarefaFragment : Fragment() {

    private var _binding: FragmentTarefaBinding? = null
    private val binding get() = _binding!!

    private lateinit var checkNotificationPermission: ActivityResultLauncher<String>
    private var isPermission = false

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {

        _binding = FragmentTarefaBinding.inflate(inflater, container, false)

        checkNotificationPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isPermission = isGranted
        }

        checkPermission()

        userInterface()

        return binding.root
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermission = true
            } else {
                isPermission = false

                checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            isPermission = true
        }
    }

    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance(requireContext())
        instanceWorkManager.beginUniqueWork(
            NotifyWork.NOTIFICATION_WORK,
            ExistingWorkPolicy.REPLACE, notificationWork).enqueue()
    }

    private fun userInterface() {

        val titleNotification = getString(R.string.notification_title)
        binding.collapsingToolbarLayout.title = titleNotification

        binding.doneFab.setOnClickListener {
            if (validateInputFields()) {
                if (isPermission) {
                    val customCalendar = Calendar.getInstance()
                    customCalendar.set(
                        binding.datePicker.year,
                        binding.datePicker.month,
                        binding.datePicker.dayOfMonth,
                        binding.timePicker.hour,
                        binding.timePicker.minute, 0
                    )
                    val customTime = customCalendar.timeInMillis
                    val currentTime = System.currentTimeMillis()
                    if (customTime > currentTime) {
                        val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
                        val delay = customTime - currentTime
                        scheduleNotification(delay, data)

                        val titleNotificationSchedule = getString(R.string.notification_schedule_title)
                        val patternNotificationSchedule =
                            getString(R.string.notification_schedule_pattern)
                        Snackbar.make(
                            binding.coordinatorLayout,
                            titleNotificationSchedule + SimpleDateFormat(
                                patternNotificationSchedule, Locale.getDefault()
                            ).format(customCalendar.time).toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        val errorNotificationSchedule = getString(R.string.notification_schedule_error)
                        Snackbar.make(
                            binding.coordinatorLayout,
                            errorNotificationSchedule,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        checkNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                findNavController().navigate(R.id.action_tarefaFragment_to_perfilFragment)

                val editText = binding.editText
                val text = editText.text.toString()

                val tarefaData = Tarefa(
                    titleNotification = text,
                    year = binding.datePicker.year,
                    month = binding.datePicker.month + 1,
                    day = binding.datePicker.dayOfMonth,
                    hour = binding.timePicker.hour,
                    minute = binding.timePicker.minute,
                    isPermission = isPermission
                )
                sendDataToServer(tarefaData)
            }
        }
    }

    private fun sendDataToServer(tarefa: Tarefa) {

        val userPreferencesRepository = UserPreferencesRepository.getInstance(requireContext())
        val userId = userPreferencesRepository.userId

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UserApi::class.java)

        val gson = Gson()
        val tarefaJsonElement = gson.toJsonTree(tarefa)

        val tarefaJson = JsonObject()
        tarefaJson.add("tarefa", tarefaJsonElement)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.addTarefa(userId, tarefaJson).execute()
                if (response.isSuccessful) {

                    launch(Dispatchers.Main) {
                        Toast.makeText(context, "Tarefa criada com sucesso!", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Log.d("taf", "Falha ao criar tarefa: $response")
                }

            }catch (e: Exception) {
                Log.d("taf", "Exception ao criar tarefa: ", e)
            }
        }
    }

    private fun validateInputFields(): Boolean {
        val name: Boolean = binding.editText.text.toString().trim().isNotEmpty()

        val blankMessage = "Esse campo não pode ser vazio"

        if (!name) {
            binding.editText.error = blankMessage
        }

        if (!name) {
            Toast.makeText(activity, "Tente novamente!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}