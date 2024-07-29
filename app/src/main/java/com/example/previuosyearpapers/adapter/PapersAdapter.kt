package com.example.previuosyearpapers.adapter

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.previuosyearpapers.dataClass.Papers
import com.example.previuosyearpapers.databinding.ItemPapersBinding
import com.google.firebase.auth.FirebaseAuth

class PapersAdapter(
    private val papersList: ArrayList<Papers?>, private val firebaseAuth: FirebaseAuth
) : RecyclerView.Adapter<PapersAdapter.PaperViewHolder>() {

    class PaperViewHolder(val binding: ItemPapersBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaperViewHolder {
        val binding = ItemPapersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaperViewHolder(binding)
    }

    override fun getItemCount() = papersList.size

    override fun onBindViewHolder(holder: PaperViewHolder, position: Int) {
        val paper = papersList[position]
        holder.binding.subjectTextView.text = paper?.subject
        holder.binding.yearTextView.text = paper?.year

        holder.binding.downloadButton.setOnClickListener {
            if (firebaseAuth.currentUser == null) {
                Toast.makeText(
                    holder.itemView.context,
                    "Please log in to download the file",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                paper?.fileUrl?.let { fileUrl ->
                    downloadFile(
                        holder.itemView.context, fileUrl, "${paper.subject}_${paper.year}.pdf"
                    )
                } ?: run {
                    Toast.makeText(holder.itemView.context, "File URL is null", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun downloadFile(context: Context, fileUrl: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(fileUrl)).apply {
            setTitle(fileName)
            setDescription("Downloading $fileName")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
    }
}
