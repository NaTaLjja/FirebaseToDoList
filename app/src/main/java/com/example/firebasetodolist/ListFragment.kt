package com.example.firebasetodolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class ListFragment:Fragment() {

    private var listView:RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById(R.id.list)
        val fab:FloatingActionButton = view.findViewById(R.id.fab)

        fab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, AddTaskFragment())
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        val database = FirebaseDatabase.getInstance()
        val target = database.reference
            .child(account?.id?:"unknown_account")
            .child("tasks")

        target.get()
            .addOnCompleteListener {
                val tasksList:ArrayList<Task?> = ArrayList()
                if(it.isSuccessful){
                    it.result.children.forEach{dataSnapshot ->
                        val taskVal = dataSnapshot.getValue(String::class.java)
                        val taskId = dataSnapshot.key
                        val taskItem = Task(taskId!!, taskVal ?: "")

                       tasksList.add(taskItem)
                    }

                    val adapter = TaskAdapter(tasksList)
                    listView?.adapter = adapter
                    listView?.layoutManager = LinearLayoutManager(requireContext())
                }
            }
    }
}