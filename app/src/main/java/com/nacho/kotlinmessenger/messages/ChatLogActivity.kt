package com.nacho.kotlinmessenger.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.nacho.kotlinmessenger.R
import com.nacho.kotlinmessenger.models.ChatMessage
import com.nacho.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "LogChat"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //asignamos el adapter para administrar el recycler view
        recycler_view_chat_log.adapter = adapter

        //obtenemos el toUser mandado por la NewMessageActivity guardado con key USER_KEY
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

//       setupDummyData()
        listenForMessages()
        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message...")
            //mandamos mensaje tras click en send
            performSendMessage()
        }
    }

    private fun listenForMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        //agregamos un listener que disparará por los cambios en la data de los children
        //de la referencia, en esta caso, los obj mensajes bajo el nodo /messages, asi se actualizará más dinámicamente en "real time"
        reference.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                //encerramos la data recibida por parámetro en un obj nuestro chatMessage i le indicamos que sea
                //con el fromato de ChatMessage
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null){
                    Log.d(TAG, chatMessage.text)

                    //comprobamos si el fromId del usuario es igual del logeado para distinguir
                    //entre emisor y receptor y saber si disponer el chat message a la derecha o a la izquierda
                    if(chatMessage.fromId==FirebaseAuth.getInstance().uid){
                        val currentUser = LatestMessagesActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    }else{
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }


                }

                recycler_view_chat_log.scrollToPosition(adapter.itemCount -1 )

            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }


    private fun performSendMessage() {
        //obtenemos lo escrito en el edit text y lo meteremos en en el obj ChatMessage
        val text = edittext_chat_log.text.toString()

        //el fromId lo obtenemos del identificador único del usuario logeado
        val fromId = FirebaseAuth.getInstance().uid

        //el toId lo obtenemos con el usuario (será el uid de éste) mandado por la NewMessageActivity en el intent
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if(fromId==null) return

        //con .push() generamos automáticamente un nodo en la database
//        val reference  = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference  = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference  = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis()/1000 )

        //metemos el msg en la referencia de la insatncia de firebase bajo el nodo generado por .push()
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                //despues de dar sen y limpiar el editText, vamos al último mensaje (obtenemos su posicíon a través del adaptador que lo administra)
                recycler_view_chat_log.scrollToPosition(adapter.itemCount -1 )
            }

        toReference.setValue(chatMessage)

        val latestMessageReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageReference.setValue(chatMessage)

        val latestMessageToReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToReference.setValue(chatMessage)

    }


}
//clase que representa al emisor
class ChatFromItem(val text: String, val fromUser: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return  R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text

        //cargamos la imagen de usuario en el image View de cada row
        val uri = fromUser.profleImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_from_row
        Picasso.get().load(uri).into(targetImageView)
    }
}

//clase que representa al receptor
class ChatToItem(val text: String, val toUser: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return  R.layout.chat_to_row


    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text

        //cargamos la imagen de usuario en el image View de cada row
        val uri = toUser.profleImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }
}
