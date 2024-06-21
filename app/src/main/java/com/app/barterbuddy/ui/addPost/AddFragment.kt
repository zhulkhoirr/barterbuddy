package com.app.barterbuddy.ui.addPost

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.barterbuddy.ViewModelsFactory
import com.app.barterbuddy.databinding.FragmentAddBinding
import com.app.barterbuddy.di.models.AddPost
import com.app.barterbuddy.ui.MainActivity
import com.app.barterbuddy.ui.home.HomeFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var auth: FirebaseAuth
    private var file: File? = null
    private val addViewModel by viewModels<AddViewModel> {
        ViewModelsFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        Log.d("uid", firebaseUser!!.uid)

        with(binding){
            image.setOnClickListener {
                val intent = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                resultLauncherGallery.launch(intent)
            }

            post.setOnClickListener {
                val dataTitle = title.text.toString()
                val dataDesc = desc.text.toString()
                val dataType = type.text.toString()
                val dataStatus = status.text.toString()

                when {
                    dataTitle.isEmpty() -> messageToast("Judul masih kosong")
                    dataDesc.isEmpty() -> messageToast("Deskripsi masih kosong")
                    dataType.isEmpty() -> messageToast("Tipe masih kosong")
                    dataStatus.isEmpty() -> messageToast("Status masih kosng")
                    file == null -> messageToast("Gambar Masih kosong")
                    file!!.length() > 1_048_576 -> messageToast("Ukuran gambar tidak boleh lebih dari 1 MB")

                    else -> {
                        val poto = file!!.asRequestBody("image/jpeg".toMediaTypeOrNull())

                        val request = AddPost(
                            userId = firebaseUser.uid,
                            title = dataTitle.toRequestBody("text/plain".toMediaType()),
                            description = dataDesc.toRequestBody("text/plain".toMediaType()),
                            type = dataType.toRequestBody("text/plain".toMediaType()),
                            status = dataStatus.toRequestBody("text/plain".toMediaType()),
                            image = MultipartBody.Part.createFormData("image", file!!.name, poto)
                        )
                        lifecycleScope.launch {
                            val response = addViewModel.addPost(request)
                            response.observe(viewLifecycleOwner){
                                if (it != null){
                                    messageToast("Post Berhasil")
                                    (activity as MainActivity).loadFragments(HomeFragment())
                                } else {
                                    messageToast("Post Gagal")
                                }
                            }
                        }
                    }
                }
            }
        }
        return binding.root
    }

    private val resultLauncherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null){
            val path = getPath(requireContext(), uri)
            file = File(path!!)
        }
    }

    private fun getPath(context: Context, uri: Uri): String? {
        var realPath: String? = null
        val data = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, data, null, null, null)
        cursor?.use {
            val indexColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            realPath = it.getString(indexColumn)
        }
        return realPath
    }

    private fun messageToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }
}