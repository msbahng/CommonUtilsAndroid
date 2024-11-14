package com.msbahng.commonutils.extensions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class DataStoreKeys {
    selectedLanguage
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DEFAULT_PREFERENCE_DATA")

suspend fun <T> Context.saveSharedPreferenceData(key: DataStoreKeys, value: T) = dataStore.edit { preferences ->

    when (value) {
        is Long -> preferences[longPreferencesKey(key.name)] = value
        is String -> preferences[stringPreferencesKey(key.name)] = value
        is Int -> preferences[intPreferencesKey(key.name)] = value
        is Boolean -> preferences[booleanPreferencesKey(key.name)] = value
        is Float -> preferences[floatPreferencesKey(key.name)] = value
        else -> {}
    }
}

fun <T> Context.getSharedPreferenceData(key: DataStoreKeys, defaultValue: T?): Flow<T?> = dataStore.data.map { preferences ->

    when (defaultValue) {
        is Long -> preferences[longPreferencesKey(key.name)]
        is String -> preferences[stringPreferencesKey(key.name)]
        is Int -> preferences[intPreferencesKey(key.name)]
        is Boolean -> preferences[booleanPreferencesKey(key.name)]
        is Float -> preferences[floatPreferencesKey(key.name)]
        else -> null
    } as T ?: defaultValue
}
