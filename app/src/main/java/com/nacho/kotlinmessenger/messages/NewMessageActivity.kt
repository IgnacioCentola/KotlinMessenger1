package com.nacho.kotlinmessenger.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nacho.kotlinmessenger.R
import com.nacho.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class NewMessageActivity : AppCompatActivity() {

    private var currentUser: User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        //necesitamos un adaptador para customizar la vista y poner los usuarios en una lista

//        val adapter = GroupAdapter<ViewHolder>() // -------> Biblioteca de terceros Grupie
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//
//        recyclerview_newmessage.adapter = adapter

        //traemos los usuarios con firebase
        fetchCurrentUser()
        fetchUsers()

//        recyclerview_newmessage.layoutManager = LinearLayoutManager(this) ----> Lo hacemos desde el xml

    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            //llamado cuando se toman todos los usuarios de la database de Firebase
            override fun onDataChange(p0: DataSnapshot) {
                //creamos un adapter nuevo
                val adapter = GroupAdapter<ViewHolder>()
                //p0 es el paraámetro que contiene toda la data
                //mostramos en Log con propósito de testing nada más
                p0.children.forEach {
                    Log.d("Jesus", it.toString())
                    //Podemos convertir la data en objetos (ej. una clase custom) para manejar de manera adaptativa
                    //En este caso la convertimos a la clase User presente en RegisterActivity.kt
                    val user = it.getValue(User::class.java)
                    //recorremos los children de la data y los vamos agregando al adapter pero sin datos todavía,
                    //simplemente se verá la cantidad de items igual a la de usuarios en la database
//                    if(user!=null){
//                        adapter.add(UserItem(user!!))
//                    }

                    if (user?.username != currentUser?.username){
                        adapter.add(UserItem(user!!))
                    }else{
                        return
                    }

                    adapter.setOnItemClickListener { item, view ->
                        //el item de parámetro representa un row del adapter del recycler
                        //el parámetro view estaría representando el activity/contexto NewMessage,
                        //por eso tenemos que obtenerlo de éste en vez de usar this, ya que no estaría haciendo
                        //referencia al contexto actual sino al del ciclo
                        val userItem = item as UserItem //

                        val intent = Intent(view.context,ChatLogActivity::class.java)
                        //mandamos el nombre de usuario a la activity de chat pata mostrarlo como título en la supportBar
//                        intent.putExtra(USER_KEY, userItem.toUser.username)
                        //podemos pasar directamente el usuario sin limitarnos a una propiedad, si es que hacemos
                        //que la clase del objeto herede de Parcelable
                        intent.putExtra(USER_KEY, userItem.user)
                        startActivity(intent)


                        //queremos que al presionar back el usuario vuelva a LatestMessageRow
                        finish()
                    }

                }

                //una vez recorrida la data y seteados los chldren del adapter, asignamos el adapter a .adapter del recyclerView

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun fetchCurrentUser() {
        //obtenemos y metemos en la referencia el identificador del usuario actual para saber quien es y así completar
        //completar el image view de cada row que representa un mensaje mandado por el toUser
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                //podemos obtener la información de usuario guardada en el parámetro que representa la info del usuario segun el uid metido en la referencia
                //y le damos formato de nuestra clase User
                LatestMessagesActivity.currentUser = p0.getValue(User::class.java)
                Log.d("Jesus", "Current toUser: ${LatestMessagesActivity.currentUser?.username}")

            }
        })


    }

}

class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        //will be called in our list for each toUser object later on

       viewHolder.itemView.username_textview_newmessage.text = user.username

        //cargamos la imagen de usuario en el imageView del recyclerView (con libreria de terceros)
        Picasso.get().load(user.profleImageUrl).into(viewHolder.itemView.imageview_newmessage)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_newmessage
    }
}

//normalmente deberíamos crear y uilizar una clase como adaptador customizado, pero lo simplificaremos usando
//bibliotecas de terceros

//class CustomAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>{
//    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}