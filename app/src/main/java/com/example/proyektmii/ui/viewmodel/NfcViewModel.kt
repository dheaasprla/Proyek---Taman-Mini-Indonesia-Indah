package com.example.proyektmii.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*

import com.example.proyektmii.data.AppDatabase
import com.example.proyektmii.data.CardRepository
import com.example.proyektmii.data.UserCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NfcViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).userCardDao()
    private val repo = CardRepository(dao)

    // ① LiveData untuk hasil scan
    private val _scannedId = MutableLiveData<String?>(null)
    val scannedId: LiveData<String?> = _scannedId

    // ② Fungsi untuk dipanggil saat NFC terbaca
    fun onCardScanned(cardId: String) {
        // simpan ke DB di background
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(UserCard(cardId))
        }
        // kirim event ke UI
        _scannedId.postValue(cardId)
    }
}