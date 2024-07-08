package com.example.previuosyearpapers.adapter

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.previuosyearpapers.Papers
import com.example.previuosyearpapers.databinding.ItemPapersBinding

class PapersAdapter(private val papersList: ArrayList<Papers?>) :
    RecyclerView.Adapter<PapersAdapter.PaperViewHolder>() {

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
            if (paper != null) {
                downloadFile(
                    holder.binding.root.context,
                    paper.fileUrl,
                    "${paper.subject}_${paper.year}.pdf"
                )
            }
        }
    }

    private fun downloadFile(context: Context, fileUrl: String, fileName: String) {
        if (fileUrl != null) {
            val request = DownloadManager.Request(Uri.parse(fileUrl))
                .setTitle(fileName)
                .setDescription("Downloading $fileName")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadmanager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadmanager.enqueue(request)

            Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "File URL is null", Toast.LENGTH_SHORT).show()
        }


    }
}
