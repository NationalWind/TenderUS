package com.hcmus.tenderus.ui.viewmodels

import android.util.Log
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
import com.hcmus.tenderus.model.Report
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface ReportListUiState {
    data class Success(val reportList: List<Report>) : ReportListUiState
    data object Error : ReportListUiState
    data object Loading : ReportListUiState
}

class ReportListViewModel(private val tenderUsRepository: TenderUsRepository) : ViewModel() {
    var reportListUiState: ReportListUiState by mutableStateOf(ReportListUiState.Loading)

    init {
        getReportList()
    }

    fun getReportList() {
        viewModelScope.launch {
            reportListUiState = ReportListUiState.Loading
            reportListUiState = try {
                ReportListUiState.Success(tenderUsRepository.getReportList())
            } catch (e: IOException) {
                Log.d("AdminReportList", e.message.toString())
                ReportListUiState.Error
            } catch (e: HttpException) {
                Log.d("AdminReportList", e.message.toString())
                ReportListUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TenderUsApplication)
                val tenderUsRepository = application.container.tenderUsRepository
                ReportListViewModel(tenderUsRepository = tenderUsRepository)
            }
        }
    }
}