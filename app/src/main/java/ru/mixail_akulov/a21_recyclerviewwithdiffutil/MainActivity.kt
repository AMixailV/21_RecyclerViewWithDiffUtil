package ru.mixail_akulov.a21_recyclerviewwithdiffutil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mixail_akulov.a21_recyclerviewwithdiffutil.databinding.ActivityMainBinding
import ru.mixail_akulov.a21_recyclerviewwithdiffutil.model.User
import ru.mixail_akulov.a21_recyclerviewwithdiffutil.model.UsersListener
import ru.mixail_akulov.a21_recyclerviewwithdiffutil.model.UsersService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UsersAdapter

    private val usersService: UsersService
        get() = (applicationContext as App).usersService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UsersAdapter(object : UserActionListener {
            override fun onUserMove(user: User, moveBy: Int) {
                usersService.moveUser(user, moveBy) // перенаправляем на реализацию в UserService
            }

            override fun onUserDelete(user: User) {
                usersService.deleteUser(user) // перенаправляем на реализацию в UserService
            }

            override fun onUserDetails(user: User) {
                Toast.makeText(this@MainActivity, "User: ${user.name}", Toast.LENGTH_SHORT).show()
            }

            override fun onUserFire(user: User) {
                usersService.fireUser(user)
            }
        })

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        // чтобы иконки не мерцали при переписывании данных, пишем этот код и отменяем анимирование
        // изменения данных, но оставляем анимирование перемещения
        val itemAnimator = binding.recyclerView.itemAnimator
        if (itemAnimator is DefaultItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }

        usersService.addListener(usersListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        usersService.removeListener(usersListener)
    }

    private val usersListener: UsersListener = {
        adapter.users = it
    }
}