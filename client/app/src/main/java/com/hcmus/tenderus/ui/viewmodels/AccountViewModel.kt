package com.hcmus.tenderus.ui.viewmodels
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
//import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.initializer
//import androidx.lifecycle.viewmodel.viewModelFactory
//import com.hcmus.tenderus.TenderUsApplication
//import com.hcmus.tenderus.data.TenderUsRepository
//import com.hcmus.tenderus.model.Account
//import kotlinx.coroutines.launch
//import retrofit2.HttpException
//import java.io.IOException
//
//sealed interface AccountUiState {
//    data class Success(val accounts: List<Account>) : AccountUiState
//    data object Error : AccountUiState
//    data object Loading : AccountUiState
//}
//
//class AccountViewModel(private val tenderUsRepository: TenderUsRepository) : ViewModel() {
//    var accountUiState: AccountUiState by mutableStateOf(AccountUiState.Loading)
//        private set
//
//    init {
//        getAccounts()
//    }
//
//
//    fun getAccounts() {
//        viewModelScope.launch {
//            accountUiState = AccountUiState.Loading
//            accountUiState = try {
//                AccountUiState.Success(tenderUsRepository.getReportList())
//            } catch (e: IOException) {
//                AccountUiState.Error
//            } catch (e: HttpException) {
//                AccountUiState.Error
//            }
//        }
//    }
//
//    companion object {
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val application = (this[APPLICATION_KEY] as TenderUsApplication)
//                val tenderUsRepository = application.container.tenderUsRepository
//                AccountViewModel(tenderUsRepository)
//            }
//        }
//    }
//}