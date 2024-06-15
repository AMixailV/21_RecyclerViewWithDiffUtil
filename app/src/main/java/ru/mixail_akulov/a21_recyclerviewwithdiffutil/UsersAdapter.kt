package ru.mixail_akulov.a21_recyclerviewwithdiffutil

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.mixail_akulov.a21_recyclerviewwithdiffutil.databinding.ItemUsersBinding
import ru.mixail_akulov.a21_recyclerviewwithdiffutil.model.User

// Если бы действие в списке было бы одно, то можно было определить один typealias, а т.к. у нас
// три действия,то использовать интерфейс
interface UserActionListener {

    fun onUserMove(user: User, moveBy: Int)

    fun onUserDelete(user: User)

    fun onUserDetails(user: User)

    fun onUserFire(user: User)
}

class UsersDiffCallback(
    private val oldList: List<User>,
    private val newList: List<User>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    // проверяем, совпадают ли id переданных элементов списка нового и старого
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser.id == newUser.id
    }

    // проверяет, совпадают ли все остальные поля элементов
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser == newUser
    }
}

class UsersAdapter(
    private val actionListener: UserActionListener
) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(), View.OnClickListener {

    var users: List<User> = emptyList()
        set(newValue) {
            val diffCallback = UsersDiffCallback(field, newValue) // создаем diffCallback со старым и новым списками
            val diffResult = DiffUtil.calculateDiff(diffCallback) // и передаем его в обработку
            field = newValue  // при размещении нового элемента из списка
            // передать все изменения (this), т.е. адаптеру.
            diffResult.dispatchUpdatesTo(this)  // список перерисовывается
        }

    override fun onClick(v: View) { // здесь View ни когда не равно null
        val user = v.tag as User // в тег при нажатии был положен сам User
        when (v.id) {  // поэтому можно из него получить на что именно нажали
            R.id.moreImageViewButton -> {
                showPopupMenu(v) // в нем размещаем наши три действия
            }
            else -> {
                actionListener.onUserDetails(user) // реализован onUserDetails() в MainActivity,
                                                   // передаем ему пользователя, на которого нажали
            }
        }
    }

    override fun getItemCount(): Int = users.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUsersBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.moreImageViewButton.setOnClickListener(this)

        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = users[position]
        val context = holder.itemView.context

        with(holder.binding) {
            holder.itemView.tag = user // помещаем в тэги юзера при каких-то нажатиях,
            moreImageViewButton.tag = user // чтобы можно было его вытащить в обработке нажатий onClick()

            userNameTextView.text = user.name
            userCompanyTextView.text = user.company.ifBlank { context.getString(R.string.unemployed) }

            if (user.photo.isNotBlank()) {
                Glide.with(photoImageView.context)
                    .load(user.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_user_avatar)
                    .error(R.drawable.ic_user_avatar)
                    .into(photoImageView)
            } else {
                Glide.with(photoImageView.context).clear(photoImageView)
                photoImageView.setImageResource(R.drawable.ic_user_avatar)
                // вы также можете использовать следующий код вместо этих двух строк ^
                // Glide.with(photoImageView.context)
                //        .load(R.drawable.ic_user_avatar)
                //        .into(photoImageView)
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val user = view.tag as User // вытаскиваем подьзователя из тэга
        val position = users.indexOfFirst { it.id == user.id }

        // Определяем popupMenu
        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up)).apply {
            isEnabled = position > 0
        }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down)).apply {
            isEnabled = position < users.size - 1
        }

        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.remove))

        if (user.company.isNotBlank()) {
            popupMenu.menu.add(0, ID_FIRE, Menu.NONE, context.getString(R.string.fire))
        }

        // обработка событий в popupMenu. Отправляем пользователя в соответствующий метод в MainActivity
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> {
                    actionListener.onUserMove(user, -1)
                }
                ID_MOVE_DOWN -> {
                    actionListener.onUserMove(user, 1)
                }
                ID_REMOVE -> {
                    actionListener.onUserDelete(user)
                }
                ID_FIRE -> {
                    actionListener.onUserFire(user)
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    class UsersViewHolder(
        val binding: ItemUsersBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object { // идентификаторы действий
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE = 3
        private const val ID_FIRE = 4
    }
}