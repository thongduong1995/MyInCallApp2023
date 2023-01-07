package com.example.myapplication.ui.notifications

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.ContactAdapter
import com.example.myapplication.adapter.ContactModel
import com.example.myapplication.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.contactListRecycleview
        val mContactAdapter = ContactAdapter(getAllContacts())
        recyclerView.apply {
            adapter = mContactAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }


        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
           // textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("Range")
    fun getAllContacts(): List<ContactModel>{
        val nameList = ArrayList<ContactModel>()
        val cursor: Cursor? = requireActivity().contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        if(cursor!=null && cursor.count!! > 0){
            while (cursor.moveToNext()){
                val colum = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                val hasPhoneNumber =
                    cursor.getString(if(colum>-1) colum else 0).toInt()
                if(hasPhoneNumber>0){
                    val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    val displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    val phoneCursor: Cursor = requireActivity().contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )!!
                    if(phoneCursor.moveToNext()){
                        val phoneNumber =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val contact = ContactModel(contactId, displayName, phoneNumber);
                        Log.i("thong", contact.toString())
                        nameList.add(contact)
                    }
                }
            }
        }
        return nameList
    }

    @SuppressLint("Range")
    private fun getContactPhoneNumbers(resolver: ContentResolver): Map<String, NameAndPhoneList> {
        val startTime = System.nanoTime()
        val map = hashMapOf<String, NameAndPhoneList>()
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        )
        val selection =
            "${ContactsContract.Contacts.DISPLAY_NAME} NOT LIKE '' and ${ContactsContract.Contacts.DISPLAY_NAME} NOT NULL"
        resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            selection,
            null,
            null,
            null
        )?.let { cursor ->
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id: String? =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name: String? =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val photoUri =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))
                    val phone =
                        if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            val pCur: Cursor = resolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(id),
                                null
                            )!!
                            val numbers = mutableListOf<String>()
                            while (pCur.moveToNext()) {
                                val phoneNo: String = pCur.getString(
                                    pCur.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER
                                    )
                                )
                                numbers.add(phoneNo)
                            }
                            pCur.close()
                            numbers
                        } else
                            null
                    // take contacts which either have email or phone numbers
                    if (id != null && name != null) {
                        map[id] = NameAndPhoneList(name, phone, photoUri?.let { Uri.parse(it) })
                    }
                }
            }
            cursor.close()
        }


        return map
    }

    private data class NameAndPhoneList(
        val name: String,
        val phoneList: List<String>?,
        val imageUri: Uri?
    )
}