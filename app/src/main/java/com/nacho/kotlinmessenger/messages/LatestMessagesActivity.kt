package com.nacho.kotlinmessenger.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.database.*
import com.nacho.kotlinmessenger.R
import com.nacho.kotlinmessenger.models.ChatMessage
import com.nacho.kotlinmessenger.models.User
import com.nacho.kotlinmessenger.registerLogin.RegisterActivity
import com.nacho.kotlinmessenger.views.LatestMessageRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        val TAG = "Jesus"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        recyclerview_latest_messages.adapter=adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "123")
            //tenemos que abrir la activity que corresponda al usuario que contiene el row
            val intent = Intent(this, ChatLogActivity::class.java)
            //necesitamos el usuario del row
            val row = item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser) // -----> Metemos en el intent el chat partner
            startActivity(intent)
        }

//        setupDummyRows()
        listenForLatestMessages()

        fetchCurrentUser()

        verifyUserIsLoggedIn()

        }

    //hasmap utilizado para actualizar de forma correcta el recycler de LatestMessages, porque sino comienza a agregar rows por c/mensaje nuevo
    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        //obtendremos los últimos mensajes para disponerlos en el recycler
        val fromId = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        //listener correspondiente a los children del nodo de la referencia
        reference.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                //formateamos la data del parámetro en una clase nuestra
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return //-----> operador Elvis

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                //queremos que se muestre en esta activity el último mensaje de cada chat cada vez que cambie
                //formateamos la data del parámetro en una clase nuestra
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }



    val adapter = GroupAdapter<ViewHolder>()

//    private fun setupDummyRows() {
//
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//    }

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
                currentUser = p0.getValue(User::class.java)
                Log.d("Jesus", "Current toUser: ${currentUser?.username}")

            }
        })


    }

    private fun verifyUserIsLoggedIn() {
        //chekeamos si es que el usuario esta logeado para que se ejecute el register o latestMessages
        val uid =  FirebaseAuth.getInstance().uid
        // si el identificador uúnico esta vació es porque no se logeo
        if(uid==null){
            val intent = Intent(this, RegisterActivity::class.java)
            //clear the stack
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
         }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_new_message ->{
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)

            }
            R.id.menu_sign_out ->{
                //sign out cuando se hace click en el item de salir en el menuBar
                //y vamos al register
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                //clear the stack
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
