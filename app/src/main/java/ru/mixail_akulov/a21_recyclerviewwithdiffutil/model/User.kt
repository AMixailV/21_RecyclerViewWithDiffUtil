package ru.mixail_akulov.a21_recyclerviewwithdiffutil.model

// т.к. это дата класс, то сравнение его объектов через == будет сравнивать все его поля.
// таким образом определены методы тостринг и экилс у дата классов по умолчанию
data class User(
    val id: Long,
    val photo: String,
    val name: String,
    val company: String
)