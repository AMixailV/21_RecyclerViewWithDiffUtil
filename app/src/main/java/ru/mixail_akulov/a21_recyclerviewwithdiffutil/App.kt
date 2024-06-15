package ru.mixail_akulov.a21_recyclerviewwithdiffutil

import android.app.Application
import ru.mixail_akulov.a21_recyclerviewwithdiffutil.model.UsersService

// Делаем UsersService синглтоном. Прописываем его в манифесте классом нашего приложения и получаем
// доступ к нему из любого места
class App : Application() {

    val usersService = UsersService()
}