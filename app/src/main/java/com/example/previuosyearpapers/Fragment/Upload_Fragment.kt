package com.example.previuosyearpapers.Fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.previuosyearpapers.databinding.FragmentUploadBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Upload_Fragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var selectedFileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUploadBinding.inflate(inflater, container, false)

        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("papers")

        selectFileButtonListener()

        binding.uploadButton.setOnClickListener {
            val subject = binding.subjectEditText.text.toString()
            val year = binding.yearEditText.text.toString()

            if (selectedFileUri != null && subject.isNotEmpty() && year.isNotEmpty()) {
                uploadPaper(subject, year, selectedFileUri!!)
            } else {
                Toast.makeText(requireContext(), "Please fill all details", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return binding.root
    }

    private fun uploadPaper(subject: String, year: String, fileUri: Uri) {
        val storageRef = storage.reference.child("papers/${fileUri.lastPathSegment}")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val uploadTask = storageRef.putFile(fileUri).await()
                val uri = storageRef.downloadUrl.await()

                val paperMetadata = mapOf(
                    "subject" to subject,
                    "year" to year,
                    "fileUrl" to uri.toString()
                )

                databaseReference.push().setValue(paperMetadata).await()

                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Upload success", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        requireContext(),
                        "File upload failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun selectFileButtonListener() {
        binding.selectFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
        }
    }
}
