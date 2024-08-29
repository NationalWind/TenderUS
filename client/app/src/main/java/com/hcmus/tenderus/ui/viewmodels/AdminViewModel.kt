package com.hcmus.tenderus.ui.viewmodels

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hcmus.tenderus.TenderUsApplication
import com.hcmus.tenderus.data.TenderUsRepository
import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.model.AccountAction
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.ReportAction
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data object Error : UiState<Nothing>
    data object Loading : UiState<Nothing>
}

class AdminViewModel(private val tenderUsRepository: TenderUsRepository) : ViewModel() {
    var reportListUiState: UiState<List<Report>> by mutableStateOf(UiState.Loading)
    var reportDetailUiState: UiState<Report> by mutableStateOf(UiState.Loading)
    var accountListUiState: UiState<List<Account>> by mutableStateOf(UiState.Loading)
    var accountDetailUiState: UiState<Account> by mutableStateOf(UiState.Loading)

    init {
        getReportList()
        getAccountList()
    }

    fun getReportList() {
        viewModelScope.launch {
            reportListUiState = UiState.Loading
            reportListUiState = try {
                UiState.Success(tenderUsRepository.getReportList())
            } catch (e: IOException) {
                Log.d("AdminReportList", e.message.toString())
                UiState.Error
            } catch (e: HttpException) {
                Log.d("AdminReportList", e.message.toString())
                UiState.Error
            } catch (e: Exception) {
                Log.d("AdminReportList", e.message.toString())
                UiState.Error
            }
        }
    }

    fun getReportDetail(id: String) {
        viewModelScope.launch {
            reportDetailUiState = UiState.Loading
            reportDetailUiState = try {
                UiState.Success(tenderUsRepository.getReportDetail(id))
            } catch (e: IOException) {
                Log.d("AdminReportDetail", e.message.toString())
                UiState.Error
            } catch (e: HttpException) {
                Log.d("AdminReportDetail", e.message.toString())
                UiState.Error
            } catch (e: Exception) {
                Log.d("AdminReportDetail", e.message.toString())
                UiState.Error
            }
        }
    }

    fun postReportAction(id: String, reportAction: ReportAction) {
        viewModelScope.launch {
            reportDetailUiState = UiState.Loading
            try {
                tenderUsRepository.postReportAction(id, reportAction)
            } catch (e: IOException) {
                Log.d("AdminReportAction", e.message.toString())
                reportDetailUiState = UiState.Error
            } catch (e: HttpException) {
                Log.d("AdminReportAction", e.message.toString())
                reportDetailUiState = UiState.Error
            } catch (e: Exception) {
                Log.d("AdminReportAction", e.message.toString())
                reportDetailUiState = UiState.Error
            }
        }
    }

    fun getAccountList() {
        viewModelScope.launch {
            accountListUiState = UiState.Loading
            accountListUiState = try {
                UiState.Success(tenderUsRepository.getAccountList())
            } catch (e: IOException) {
                Log.d("AdminAccountList", e.message.toString())
                UiState.Error
            } catch (e: HttpException) {
                Log.d("AdminAccountList", e.message.toString())
                UiState.Error
            } catch (e: Exception) {
                Log.d("AdminAccountList", e.message.toString())
                UiState.Error
            }
        }
    }

    fun getAccountDetail(id: String) {
        viewModelScope.launch {
            accountDetailUiState = UiState.Loading
            accountDetailUiState = try {
                UiState.Success(tenderUsRepository.getAccountDetail(id))
            } catch (e: IOException) {
                Log.d("AdminAccountDetail", e.message.toString())
                UiState.Error
            } catch (e: HttpException) {
                Log.d("AdminAccountDetail", e.message.toString())
                UiState.Error
            } catch (e: Exception) {
                Log.d("AdminAccountDetail", e.message.toString())
                UiState.Error
            }
        }
    }

    fun postAccountAction(id: String, accountAction: AccountAction) {
        viewModelScope.launch {
            accountDetailUiState = UiState.Loading
            try {
                tenderUsRepository.postAccountAction(id, accountAction)
            } catch (e: IOException) {
                Log.d("AdminAccountAction", e.message.toString())
                reportDetailUiState = UiState.Error
            } catch (e: HttpException) {
                Log.d("AdminAccountAction", e.message.toString())
                reportDetailUiState = UiState.Error
            } catch (e: Exception) {
                Log.d("AdminAccountAction", e.message.toString())
                reportDetailUiState = UiState.Error
            }
        }
    }

    fun extractStatistic(context: Context) {
        viewModelScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val pdf = tenderUsRepository.getExportStatistic()
                    val values = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, "statistics.pdf")
                        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                        put(MediaStore.Downloads.RELATIVE_PATH, "Download/")
                    }

                    val resolver = context.contentResolver
                    val uri: Uri? =
                        resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

                    uri?.let {
                        resolver.openOutputStream(uri).use { outputStream ->
                            outputStream?.write(pdf.bytes())
                        }
                    }
                } catch (e: IOException) {
                    Log.d("AdminExportStatistic", e.message.toString())
                } catch (e: HttpException) {
                    Log.d("AdminExportStatistic", e.message.toString())
                }
            } else {
                Toast.makeText(
                    context,
                    "This action is not supported on your device.",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TenderUsApplication)
                val tenderUsRepository = application.container.tenderUsRepository
                AdminViewModel(tenderUsRepository = tenderUsRepository)
            }
        }
    }
}